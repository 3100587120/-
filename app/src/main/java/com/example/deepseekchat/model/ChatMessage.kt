package com.example.deepseekchat.model

import com.google.gson.annotations.SerializedName

// ---------- UI 显示用 ----------
data class DisplayMessage(
    val role: String,               // "user" 或 "assistant"
    val content: String,
    val reasoning: String = ""      // 推理内容，仅 deepseek-reasoner 有效
)

// ---------- API 请求 ----------
data class ChatRequest(
    val model: String = "deepseek-v4-flash",  // ✅ 已升级到 V4
    val messages: List<Message>,
    val stream: Boolean = false
)

data class Message(
    val role: String,
    val content: String
)

// ---------- API 响应 ----------
data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val index: Int,
    val message: ResponseMessage,
    val finish_reason: String?
)

data class ResponseMessage(
    val role: String,
    val content: String,
    @SerializedName("reasoning_content")
    val reasoningContent: String? = null
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
