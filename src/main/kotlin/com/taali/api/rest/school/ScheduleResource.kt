package com.taali.api.rest.school

import com.taali.api.dto.school.request.CreateClassScheduleRequest
import com.taali.api.dto.school.request.UpdateClassScheduleRequest
import com.taali.application.service.school.ScheduleService
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.time.DayOfWeek

@Path("/class-schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("OWNER", "ADMIN", "SUPERVISOR", "TEACHER")
class ScheduleResource {

    @Inject
    lateinit var scheduleService: ScheduleService

    @POST
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun createSchedule(request: CreateClassScheduleRequest): Response {
        try {
            val schedule = scheduleService.createSchedule(request)
            return Response.status(Response.Status.CREATED).entity(schedule).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @GET
    @Path("/class/{classId}")
    fun getSchedulesByClass(@PathParam("classId") classId: Long): Response {
        try {
            val schedules = scheduleService.getSchedulesByClass(classId)
            return Response.ok(schedules).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @GET
    @Path("/class/{classId}/day/{dayOfWeek}")
    fun getSchedulesByClassAndDay(
        @PathParam("classId") classId: Long,
        @PathParam("dayOfWeek") dayOfWeek: DayOfWeek
    ): Response {
        try {
            val schedules = scheduleService.getSchedulesByClassAndDay(classId, dayOfWeek)
            return Response.ok(schedules).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @GET
    @Path("/teacher/{teacherId}")
    fun getTeacherSchedule(@PathParam("teacherId") teacherId: Long): Response {
        try {
            val schedules = scheduleService.getTeacherSchedule(teacherId)
            return Response.ok(schedules).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun updateSchedule(
        @PathParam("id") id: Long,
        request: UpdateClassScheduleRequest
    ): Response {
        try {
            val schedule = scheduleService.updateSchedule(id, request)
            return Response.ok(schedule).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun deleteSchedule(@PathParam("id") id: Long): Response {
        try {
            scheduleService.deleteSchedule(id)
            return Response.noContent().build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }
}