package com.taali.api.dto.parent.response

data class SubjectGradeResponse(
    val subjectName: String,
    val grade: String,
    val percentage: Int
)