package com.taali.api.dto.parent.response

data class AssignmentResponse(
    val assignmentName: String,
    val subject: String,
    val grade: String,
    val dueDate: String
)