package com.acaris.features.chatbot.domain.model

data class ChatMessageDomain(
    val id: String,
    val sender: String,
    val text: String,
    val createdAt: String
)

data class ChatSessionDomain(
    val sessionId: String,
    val isActive: Boolean,
    val messages: List<ChatMessageDomain>
)

data class ChatReplyDomain(
    val sessionId: String,
    val replyMessage: ChatMessageDomain
)

data class ChatSummaryDomain(
    val draftSummary: String
)