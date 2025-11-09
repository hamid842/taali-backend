package com.taali.application.service.school

import com.taali.domain.model.school.Teacher
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TeacherService {

    fun getTeachersBySchool(schoolId: Long): List<Teacher> {
        return Teacher.findBySchool(schoolId)
    }

    fun getTeacherById(id: Long): Teacher? {
        return Teacher.findByUser(id)
    }
}