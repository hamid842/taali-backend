package com.taali.domain.model


import jakarta.persistence.*;
import java.time.LocalDateTime

@Entity
@Table(name = "reminders")
data class Reminder(
    @Id @GeneratedValue var id: Long? = null,
    var title: String,
    var description: String,
    var date: LocalDateTime
)
