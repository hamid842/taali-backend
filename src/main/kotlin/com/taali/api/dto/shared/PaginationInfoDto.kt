package com.taali.api.dto.shared

data class PaginationInfoDto(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Long,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

