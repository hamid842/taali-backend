package com.taali.domain.model.school

import com.taali.domain.model.common.AuditableEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
@Table(name = "class_schedules")
class ClassSchedule : AuditableEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    var schoolClass: SchoolClass? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    var dayOfWeek: DayOfWeek? = null

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime? = null

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime? = null

    @Column(name = "subject_name", nullable = false)
    var subjectName: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    var teacher: Teacher? = null

    @Column(name = "room_number")
    var roomNumber: String? = null

    companion object : PanacheCompanion<ClassSchedule> {
        fun findByClass(classId: Long): List<ClassSchedule> {
            return find("schoolClass.id", classId).list()
        }

        fun findByClassAndDay(classId: Long, dayOfWeek: DayOfWeek): List<ClassSchedule> {
            return find("schoolClass.id = ?1 and dayOfWeek = ?2", classId, dayOfWeek).list()
        }

        fun findByTeacher(teacherId: Long): List<ClassSchedule> {
            return find("teacher.id", teacherId).list()
        }

        fun findByClassAndTime(classId: Long, dayOfWeek: DayOfWeek, startTime: LocalTime): ClassSchedule? {
            return find(
                "schoolClass.id = ?1 and dayOfWeek = ?2 and startTime = ?3",
                classId,
                dayOfWeek,
                startTime
            ).firstResult()
        }

        fun existsByClassAndTime(
            classId: Long,
            dayOfWeek: DayOfWeek,
            startTime: LocalTime,
            excludeId: Long? = null
        ): Boolean {
            return if (excludeId != null) {
                find(
                    "schoolClass.id = ?1 and dayOfWeek = ?2 and startTime = ?3 and id != ?4",
                    classId,
                    dayOfWeek,
                    startTime,
                    excludeId
                ).count() > 0
            } else {
                find(
                    "schoolClass.id = ?1 and dayOfWeek = ?2 and startTime = ?3",
                    classId,
                    dayOfWeek,
                    startTime
                ).count() > 0
            }
        }
    }
}