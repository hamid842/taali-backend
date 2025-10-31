package com.taali.api.rest


import com.taali.domain.model.Classroom
import com.taali.domain.service.ClassroomService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/classrooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ClassroomResource(private val classroomService: ClassroomService) {

    @GET
    fun getAll(): List<Classroom> = classroomService.getAllClassrooms()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Classroom? = classroomService.getClassroom(id)

    @POST
    fun create(classroom: Classroom): Unit = classroomService.createClassroom(classroom)

    @PUT
    fun update(classroom: Classroom): Unit = classroomService.updateClassroom(classroom)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = classroomService.deleteClassroom(id)

    @POST
    @Path("/{classroomId}/assign-teacher/{teacherId}")
    fun assignTeacher(
        @PathParam("classroomId") classroomId: Long,
        @PathParam("teacherId") teacherId: Long
    ) = classroomService.assignTeacher(classroomId, teacherId)

    @POST
    @Path("/{classroomId}/assign-student/{studentId}")
    fun assignStudent(
        @PathParam("classroomId") classroomId: Long,
        @PathParam("studentId") studentId: Long
    ) = classroomService.assignStudent(classroomId, studentId)
}
