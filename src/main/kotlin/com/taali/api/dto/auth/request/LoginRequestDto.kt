package com.taali.api.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequestDto(
    @field:NotBlank(message = "validation.email.required")
    @field:Email(message = "validation.email.format")
    val email: String,

    @field:NotBlank(message = "validation.password.required")
    @field:Size(min = 8, message = "validation.password.size")
    val password: String
)