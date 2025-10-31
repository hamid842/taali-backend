package com.taali.api.rest

import com.taali.domain.model.Student
import com.taali.domain.service.StudentService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentResource(private val studentService: StudentService) {

    @GET
    fun getAll(): List<Student> = studentService.getAllStudents()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Student? = studentService.getStudent(id)

    @POST
    fun create(student: Student): Unit = studentService.createStudent(student)

    @PUT
    fun update(student: Student): Unit = studentService.updateStudent(student)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = studentService.deleteStudent(id)
}
