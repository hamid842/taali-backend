package com.taali.api.dto.teacher

data class TeacherDashboardStats(
    val totalStudents: Int,
    val totalClasses: Int,
    val upcomingClasses: Int,
    val attendanceRate: Double
)
