package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "class_teachers")
class ClassTeacher : AuditableEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    var teacher: Teacher? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    var schoolClass: SchoolClass? = null

    @Column(name = "subject")
    var subject: String? = null

    @Column(name = "is_main_teacher")
    var isMainTeacher: Boolean = false

    companion object : PanacheCompanion<ClassTeacher> {
        fun findByTeacher(teacherId: Long): List<ClassTeacher> {
            return find("teacher.id", teacherId).list()
        }

        fun findByClass(classId: Long): List<ClassTeacher> {
            return find("schoolClass.id", classId).list()
        }

        fun findByTeacherAndClass(teacherId: Long, classId: Long): ClassTeacher? {
            return find("teacher.id = ?1 and schoolClass.id = ?2", teacherId, classId).firstResult()
        }
    }
}