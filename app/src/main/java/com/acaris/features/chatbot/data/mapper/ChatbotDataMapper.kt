package com.acaris.features.chatbot.data.mapper

import com.acaris.features.chatbot.data.remote.model.ChatMessageResponse
import com.acaris.features.chatbot.data.remote.model.ChatReplyResponse
import com.acaris.features.chatbot.data.remote.model.ChatSessionResponse
import com.acaris.features.chatbot.data.remote.model.ChatSummaryResponse
import com.acaris.features.chatbot.domain.model.ChatMessageDomain
import com.acaris.features.chatbot.domain.model.ChatReplyDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.chatbot.domain.model.ChatSummaryDomain
import java.util.UUID

fun ChatMessageResponse.toDomain(): ChatMessageDomain {
    return ChatMessageDomain(
        id = this.id ?: UUID.randomUUID().toString(),
        sender = this.sender.orEmpty(),
        text = this.text.orEmpty(),
        createdAt = this.createdAt.orEmpty()
    )
}

fun ChatSessionResponse.toDomain(): ChatSessionDomain {
    return ChatSessionDomain(
        sessionId = this.sessionId.orEmpty(),
        isActive = this.isActive ?: false,
        messages = this.messages?.map { it.toDomain() } ?: emptyList()
    )
}

fun ChatReplyResponse.toDomain(): ChatReplyDomain {
    return ChatReplyDomain(
        sessionId = this.sessionId.orEmpty(),
        replyMessage = ChatMessageDomain(
            id = UUID.randomUUID().toString(),
            sender = "bot",
            text = this.replyText.orEmpty(),
            createdAt = this.createdAt.orEmpty()
        )
    )
}

fun ChatSummaryResponse.toDomain(): ChatSummaryDomain {
    return ChatSummaryDomain(
        draftSummary = this.draftSummary.orEmpty()
    )
}