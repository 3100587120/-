package com.example.deepseekchat.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepseekchat.api.DeepSeekApiService
import com.example.deepseekchat.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _messages = MutableStateFlow<List<DisplayMessage>>(emptyList())
    val messages: StateFlow<List<DisplayMessage>> = _messages

    private val _apiKey = MutableStateFlow(prefs.getString("api_key", "") ?: "")
    val apiKey: StateFlow<String> = _apiKey

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val baseUrl = "https://api.deepseek.com/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(DeepSeekApiService::class.java)

    fun saveApiKey(key: String) {
        prefs.edit().putString("api_key", key).apply()
        _apiKey.value = key
    }

    fun sendMessage(userMessage: String) {
        if (apiKey.value.isBlank()) {
            _error.value = "请先设置 API Key"
            return
        }
        if (userMessage.isBlank()) return

        val userMsg = DisplayMessage("user", userMessage)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true
        _error.value = null

        val historyMessages = _messages.value.takeLast(20).map {
            Message(it.role, it.content)
        }

        val request = ChatRequest(
            model = "deepseek-chat",
            messages = historyMessages,
            stream = false
        )

        viewModelScope.launch {
            try {
                val response = apiService.getChatCompletion(
                    auth = "Bearer ${apiKey.value}",
                    request = request
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    val replyContent = body?.choices?.firstOrNull()?.message?.content ?: ""
                    val assistantMsg = DisplayMessage("assistant", replyContent)
                    _messages.value = _messages.value + assistantMsg
                } else {
                    val errorBody = response.errorBody()?.string() ?: "未知错误"
                    _error.value = "API 请求失败: ${response.code()} $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "网络错误: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearChat() {
        _messages.value = emptyList()
    }
}
