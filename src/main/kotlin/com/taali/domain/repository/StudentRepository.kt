package com.taali.infrastructure.persistence.repository


import com.taali.domain.model.Student
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
interface StudentRepository : PanacheRepository<Student>
