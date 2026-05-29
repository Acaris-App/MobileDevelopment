package com.acaris.features.chatbot.presentation.mapper

import com.acaris.core.utils.DateUtils
import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.model.ChatMessageDomain
import com.acaris.features.chatbot.presentation.model.ChatHistoryItemUiModel
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel

fun ChatMessageDomain.toPresentation(): ChatMessageUiModel {
    return ChatMessageUiModel(
        id = this.id,
        text = this.text,
        isFromUser = this.sender.lowercase() == "user",
        time = DateUtils.formatIsoToTimeOnly(this.createdAt)
    )
}

fun ChatHistoryDomain.toPresentation(): ChatHistoryItemUiModel {
    return ChatHistoryItemUiModel(
        sessionId = this.sessionId,
        title = this.title,
        date = DateUtils.formatIsoToIndo(this.createdAt),
        status = this.status
    )
}