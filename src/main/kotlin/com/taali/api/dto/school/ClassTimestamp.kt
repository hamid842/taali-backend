package com.taali.api.dto.school

import com.taali.domain.enum.TimestampType
import com.taali.domain.model.common.AuditableEntity
import com.taali.domain.model.school.School
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "class_timestamps")
class ClassTimestamp : AuditableEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School? = null

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime? = null

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: TimestampType = TimestampType.REGULAR

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int = 0

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "description")
    var description: String? = null

    companion object : PanacheCompanion<ClassTimestamp> {
        fun findBySchool(schoolId: Long) =
            find("school.id", schoolId).list()
    }
}
