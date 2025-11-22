package com.taali.api.dto.parent.response

data class ChildAttendanceResponse(
    val childId: Long,
    val childName: String,
    val attendanceRate: Int,
    val presentDays: Int,
    val absentDays: Int,
    val lateDays: Int,
    val monthlyAttendance: Map<String, Int>
)