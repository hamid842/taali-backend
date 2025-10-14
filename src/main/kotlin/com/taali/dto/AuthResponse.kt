package com.taali.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String,
    val message: String
)