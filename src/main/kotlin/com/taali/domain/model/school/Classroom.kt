package com.taali.domain.model.school


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "classrooms")
class Classroom(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var code: String = "",

    @Column(nullable = false)
    var grade: String = "",

    @Column(nullable = false)
    var capacity: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School = School(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    var teacher: Teacher? = null,

    @OneToMany(mappedBy = "classroom", cascade = [CascadeType.ALL], orphanRemoval = true)
    var students: MutableList<Student> = mutableListOf(),

    @Column(name = "created_by")
    var createdBy: String? = null,

    @Column(name = "created_at")
    var createdAt: String = LocalDateTime.now().toString(),

    @Column(name = "updated_at")
    var updatedAt: String? = null
)
