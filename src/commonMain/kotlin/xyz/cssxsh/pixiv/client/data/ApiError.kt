package xyz.cssxsh.pixiv.client.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    @SerialName("error")
    val error: ErrorInfo
) {
    @Serializable
    data class ErrorInfo(
        @SerialName("user_message")
        val userMessage: String,
        @SerialName("message")
        val message: String,
        @SerialName("reason")
        val reason: String,
        @SerialName("user_message_details")
        val userMessageDetails: Map<String, String>
    )
}