package com.taali.api.dto.parent.response

data class ChildrenAttendanceResponse(
    val children: List<ChildAttendanceResponse>,
    val overallAttendanceRate: Int
)