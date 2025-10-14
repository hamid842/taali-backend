package com.taali.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "validation.email.required")
    @field:Email(message = "validation.email.invalid")
    val email: String,

    @field:NotBlank(message = "validation.password.required")
    @field:Size(min = 6, message = "validation.password.length")
    val password: String,

    @field:NotBlank(message = "validation.firstname.required")
    val firstName: String,

    @field:NotBlank(message = "validation.lastname.required")
    val lastName: String,

    @field:NotBlank(message = "validation.phone.required")
    val phone: String,

    @field:NotBlank(message = "validation.role.required")
    val role: String
)