package com.taali.api.dto.parent.response

data class DashboardStatsResponse(
    val totalChildren: Int,
    val unreadNotifications: Int,
    val pendingPayments: Int,
    val overallAttendanceRate: Int,
    val upcomingEvents: Int
)