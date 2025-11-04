package com.taali.domain.model.school

import com.taali.domain.enum.SchoolStatus
import com.taali.domain.model.common.AuditableEntity
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "schools")
class School : AuditableEntity() {

    @Column(name = "name", nullable = false, length = 255)
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must not exceed 255 characters")
    var name: String = ""

    @Column(name = "code", unique = true, nullable = false, length = 50)
    @field:NotBlank(message = "School code is required")
    @field:Size(max = 50, message = "School code must not exceed 50 characters")
    var code: String = ""

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "address", columnDefinition = "TEXT")
    var address: String? = null

    @Column(name = "email", length = 255)
    @field:Email(message = "Please provide a valid email address")
    var email: String? = null

    @Column(name = "phone", length = 20)
    var phone: String? = null

    @Column(name = "owner_id", length = 100)
    var ownerId: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: SchoolStatus = SchoolStatus.ACTIVE

    // Relationships
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var teachers: MutableSet<Teacher> = mutableSetOf()

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var classes: MutableSet<SchoolClass> = mutableSetOf()

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var students: MutableSet<Student> = mutableSetOf()

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var canteens: MutableSet<Canteen> = mutableSetOf()

    // Helper methods to get counts without loading entire collections
    fun getTeacherCount(): Long = Teacher.count("school", this)
    fun getClassCount(): Long = SchoolClass.count("school", this)
    fun getStudentCount(): Long = Student.count("school", this)
    fun getCanteenCount(): Long = Canteen.count("school", this)

    override fun toString(): String =
        "School(id=$id, name='$name', code='$code', status=$status)"

}

