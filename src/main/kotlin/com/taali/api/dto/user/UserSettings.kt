package com.taali.api.dto.user

import java.time.LocalDateTime

data class UserSettingsDto(
    val id: Long? = null,
    val preferredLanguage: String,
    val fontSize: String,
    val theme: String,
    val primaryColor: String,
    val notificationsEnabled: Boolean,
    val emailNotifications: Boolean,
    val smsNotifications: Boolean,
    val updatedAt: LocalDateTime? = null
)

data class UpdateUserSettingsRequest(
    val preferredLanguage: String? = null,
    val fontSize: String? = null,
    val theme: String? = null,
    val primaryColor: String? = null,
    val notificationsEnabled: Boolean? = null,
    val emailNotifications: Boolean? = null,
    val smsNotifications: Boolean? = null
)