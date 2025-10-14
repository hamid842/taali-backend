package com.taali.dto

import javax.validation.constraints.NotBlank

data class AuthRequest(
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)