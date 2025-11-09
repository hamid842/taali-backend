package com.taali.api.rest.school

import com.taali.application.service.school.TeacherService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/teachers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TeacherResource {

    @Inject
    lateinit var teacherService: TeacherService

    @GET
    @Path("/school/{schoolId}")
    fun getTeachersBySchool(@PathParam("schoolId") schoolId: Long): Response {
        try {
            val teachers = teacherService.getTeachersBySchool(schoolId)
            return Response.ok(teachers).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teachers: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getTeacherById(@PathParam("id") id: Long): Response {
        try {
            val teacher = teacherService.getTeacherById(id)
            return if (teacher != null) {
                Response.ok(teacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found with id: $id"))
                    .build()
            }
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher: ${e.message}"))
                .build()
        }
    }
}