package com.taali.api.dto.user

import com.taali.domain.model.user.User

data class UserPageDto(
    val users: List<User>,
    val currentPage: Int,
    val totalPages: Long,
    val totalItems: Long,
    val itemsPerPage: Int
)
