package com.taali.application.service.message

import com.taali.domain.model.school.Student
import com.taali.domain.model.user.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

@ApplicationScoped
class UserLookupService {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    fun getTeachersForParent(parentId: Long): List<User> {
        return entityManager.createQuery(
            """
            SELECT DISTINCT u FROM User u 
            WHERE u.role = 'TEACHER' AND u.id IN (
                SELECT DISTINCT t.id FROM User t 
                JOIN SchoolClass c ON c.teacher.id = t.id 
                JOIN c.students s 
                WHERE s.parent.id = :parentId
            )
            """, User::class.java
        ).setParameter("parentId", parentId).resultList
    }

    fun getParentsForTeacher(teacherId: Long): List<User> {
        return entityManager.createQuery(
            """
            SELECT DISTINCT u FROM User u 
            WHERE u.role = 'PARENT' AND u.id IN (
                SELECT DISTINCT p.id FROM User p 
                JOIN Student s ON s.parent.id = p.id 
                JOIN s.classes c 
                WHERE c.teacher.id = :teacherId
            )
            """, User::class.java
        ).setParameter("teacherId", teacherId).resultList
    }

    fun getStudentsForParent(parentId: Long): List<Student> {
        return Student.find("parent.id = ?1", parentId).list()
    }
}