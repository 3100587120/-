import com.google.gson.annotations.SerializedName
package com.example.deepseekchat.model

data class DisplayMessage(
    val role: String,               // "user" 或 "assistant"
    val content: String,
    val reasoning: String = ""      // 推理内容，仅 deepseek-reasoner 有效
)

// ---------- API 数据结构 ----------
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val stream: Boolean = false
)

data class Message(
    val role: String,
    val content: String
)

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

// 响应的 message 包含 reasoning_content 字段（仅 reasoner 模型）
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
