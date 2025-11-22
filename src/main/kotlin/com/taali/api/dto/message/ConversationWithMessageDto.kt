package com.taali.api.dto.message

import MessageDTO

data class ConversationWithMessagesDTO(
    val conversation: ConversationDTO,
    val messages: List<MessageDTO>
)
