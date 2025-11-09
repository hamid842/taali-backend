package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "school_classes")
class SchoolClass : AuditableEntity() {

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "grade_level")
    var gradeLevel: String? = null

    @Column(name = "academic_year", nullable = false)
    var academicYear: String = "" // e.g., "1403-1404"

    @Column(name = "capacity")
    var capacity: Int = 30

    @Column(name = "is_active")
    var isActive: Boolean = true

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_teacher_id")
    var mainTeacher: Teacher? = null

    // Many-to-Many with Students
    @ManyToMany
    @JoinTable(
        name = "class_students",
        joinColumns = [JoinColumn(name = "class_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id")]
    )
    var students: MutableSet<Student> = mutableSetOf()

    @ManyToMany
    @JoinTable(
        name = "class_teachers",
        joinColumns = [JoinColumn(name = "class_id")],
        inverseJoinColumns = [JoinColumn(name = "teacher_id")]
    )
    var teachers: MutableSet<Teacher> = mutableSetOf()

    // Add this field to fix the relationship with Teacher.classes
    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    var classTeachers: MutableSet<ClassTeacher> = mutableSetOf()

    companion object : PanacheCompanion<SchoolClass> {
        fun findBySchool(schoolId: Long): List<SchoolClass> {
            return find("school.id", schoolId).list()
        }

        fun findActiveBySchool(schoolId: Long): List<SchoolClass> {
            return find("school.id = ?1 and isActive = ?2", schoolId, true).list()
        }
    }
}