package com.taali.api.rest.school

import com.taali.application.service.school.ScheduleService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/lessons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LessonResource {

    @Inject
    lateinit var scheduleService: ScheduleService

    @GET
    fun getAllLessons(): Response {
        val lessons = scheduleService.getAllLessons()
        return Response.ok(lessons).build()
    }

    @GET
    @Path("/grade-level/{gradeLevel}")
    fun getLessonsByGradeLevel(@PathParam("gradeLevel") gradeLevel: String): Response {
        try {
            val lessons = scheduleService.getLessonsByGradeLevel(gradeLevel)
            return Response.ok(lessons).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }
}