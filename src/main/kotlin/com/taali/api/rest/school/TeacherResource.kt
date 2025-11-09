package com.taali.api.rest.school

import com.taali.application.service.school.TeacherService
import com.taali.domain.model.school.Teacher
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/teachers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TeacherResource {

    @Inject
    lateinit var teacherService: TeacherService

    @GET
    @Path("/school/{schoolId}")
    fun getTeachersBySchool(@RestPath schoolId: Long): Response {
        return try {
            val teachers = teacherService.getTeachersBySchool(schoolId)
            Response.ok(teachers).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teachers: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getTeacherById(@RestPath id: Long): Response {
        return try {
            val teacher = teacherService.getTeacherById(id)
            if (teacher != null) {
                Response.ok(teacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/classes")
    fun getTeacherClasses(@RestPath id: Long): Response {
        return try {
            val classes = teacherService.getTeacherClasses(id)
            Response.ok(classes).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher classes: ${e.message}"))
                .build()
        }
    }

    @POST
    @Transactional
    fun createTeacher(teacher: Teacher): Response {
        return try {
            val createdTeacher = teacherService.createTeacher(teacher)
            Response.status(Response.Status.CREATED).entity(createdTeacher).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to create teacher: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    fun updateTeacher(@RestPath id: Long, teacherData: Map<String, Any>): Response {
        return try {
            val updatedTeacher = teacherService.updateTeacher(id, teacherData)
            if (updatedTeacher != null) {
                Response.ok(updatedTeacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update teacher: ${e.message}"))
                .build()
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    fun deleteTeacher(@RestPath id: Long): Response {
        return try {
            val deleted = teacherService.deleteTeacher(id)
            if (deleted) {
                Response.ok(mapOf("message" to "Teacher deleted successfully")).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to delete teacher: ${e.message}"))
                .build()
        }
    }
}