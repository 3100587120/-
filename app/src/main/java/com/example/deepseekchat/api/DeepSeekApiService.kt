package com.example.deepseekchat.api

import com.example.deepseekchat.model.ChatRequest
import com.example.deepseekchat.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekApiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
