package com.taali.api.dto.school.request

data class BulkAssignClassRequest(
    val studentIds: List<Long>,
    val classId: Long
)