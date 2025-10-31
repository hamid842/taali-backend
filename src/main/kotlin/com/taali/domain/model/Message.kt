package com.taali.domain.model

import jakarta.persistence.*;
import java.time.LocalDateTime


@Entity
@Table(name = "messages")
data class Message(
    @Id @GeneratedValue var id: Long? = null,
    var content: String,
    var senderRole: String,
    var receiverRole: String,
    var timestamp: LocalDateTime = LocalDateTime.now()
)
