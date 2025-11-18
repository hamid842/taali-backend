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

    companion object : PanacheCompanion<Lesson> {
        fun findByGradeLevel(gradeLevel: String): List<Lesson> {
            return find("gradeLevel", gradeLevel).list()
        }

        fun findByNameAndGradeLevel(name: String, gradeLevel: String): Lesson? {
            return find("name = ?1 and gradeLevel = ?2", name, gradeLevel).firstResult()
        }

        fun findByPeriod(period: String): List<Lesson> {
            return find("gradeLevel LIKE ?1", "$period%").list()
        }
    }
}