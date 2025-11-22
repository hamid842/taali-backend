package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity
@Table(name = "lessons")
class Lesson : AuditableEntity() {

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "name_en")
    var nameEn: String? = null

    @Column(name = "grade_level", nullable = false)
    var gradeLevel: String = ""

    @Column(name = "color")
    var color: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School? = null

    companion object : PanacheCompanion<Lesson> {

        fun findByGradeLevel(gradeLevel: String): List<Lesson> {
            return find("gradeLevel = ?1", gradeLevel).list()
        }
        fun findByNameAndGradeLevel(name: String, gradeLevel: String): Lesson? {
            return find("name = ?1 and gradeLevel = ?2", name, gradeLevel).firstResult()
        }
        fun findByGradeLevelsAndSchoolId(gradeLevels: List<String>, schoolId: Long): List<Lesson> {
            return find("gradeLevel in ?1 and school.id = ?2", gradeLevels, schoolId).list()
        }

        fun findByGradeLevelAndSchoolId(gradeLevel: String, schoolId: Long): Lesson? {
            return find("gradeLevel = ?1 and school.id = ?2", gradeLevel, schoolId).firstResult()
        }

        fun findAvailableGradeLevelsBySchool(schoolId: Long): List<Lesson> {
            val query = """
            SELECT DISTINCT l.gradeLevel 
            FROM Lesson l 
            WHERE l.school_id = ?1 
            ORDER BY l.gradeLevel
        """
            return find(query, schoolId).list()
        }
    }
}