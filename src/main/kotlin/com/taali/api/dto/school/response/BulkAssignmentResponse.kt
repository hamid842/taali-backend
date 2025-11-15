package com.taali.api.dto.school.response

data class BulkAssignmentResponse(
    val total: Int,
    val successful: Int,
    val failed: Int,
    val details: List<StudentAssignmentResponse>
)