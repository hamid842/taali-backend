package com.taali.api.dto.shared

data class PagedResponseDto<T>(
    val items: List<T>,
    val pagination: PaginationInfoDto
)
