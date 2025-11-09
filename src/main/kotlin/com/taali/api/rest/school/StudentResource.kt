package com.taali.api.rest.school

import com.taali.application.service.school.StudentService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentResource {

    @Inject
    lateinit var studentService: StudentService

    @GET
    @Path("/school/{schoolId}")
    fun getStudentsBySchool(@RestPath schoolId: Long): Response {
        return try {
            val students = studentService.getStudentsBySchool(schoolId)
            Response.ok(students).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getStudentById(@RestPath id: Long): Response {
        return try {
            val student = studentService.getStudentById(id)
            if (student != null) {
                Response.ok(student).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Student not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch student: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/class/{classId}")
    fun getStudentsByClass(@RestPath classId: Long): Response {
        return try {
            val students = studentService.getStudentsByClass(classId)
            Response.ok(students).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}"))
                .build()
        }
    }
}