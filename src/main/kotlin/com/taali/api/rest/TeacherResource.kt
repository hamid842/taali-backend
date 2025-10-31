package com.taali.api.rest

import com.taali.domain.model.Teacher
import com.taali.domain.service.TeacherService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/teachers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TeacherResource(private val teacherService: TeacherService) {

    @GET
    fun getAll(): List<Teacher> = teacherService.getAllTeachers()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Teacher? = teacherService.getTeacher(id)

    @POST
    fun create(teacher: Teacher): Unit = teacherService.createTeacher(teacher)

    @PUT
    fun update(teacher: Teacher): Unit = teacherService.updateTeacher(teacher)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = teacherService.deleteTeacher(id)
}
