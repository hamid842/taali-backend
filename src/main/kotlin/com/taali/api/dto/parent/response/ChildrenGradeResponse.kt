package com.taali.api.dto.parent.response

data class ChildGradesResponse(
    val childId: Long,
    val childName: String,
    val averageGrade: String,
    val subjectGrades: List<SubjectGradeResponse>,
    val recentAssignments: List<AssignmentResponse>
)