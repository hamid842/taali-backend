package com.taali.api.rest.school

import com.taali.api.dto.school.request.CreateSchoolClassRequest
import com.taali.api.dto.school.request.UpdateSchoolClassRequest
import com.taali.application.service.school.SchoolClassService
import io.quarkus.security.Authenticated
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/school-classes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
class SchoolClassResource {

    @Inject
    lateinit var schoolClassService: SchoolClassService

    @POST
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    fun createClass(request: CreateSchoolClassRequest): Response {
        try {
            val schoolClass = schoolClassService.createClass(request)
            return Response.status(Response.Status.CREATED).entity(schoolClass).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @GET
    @Path("/{id}")
    fun getClass(@PathParam("id") id: Long): Response {
        try {
            val schoolClass = schoolClassService.getClassById(id)
            return Response.ok(schoolClass).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @GET
    @Path("/school/{schoolId}")
    fun getClassesBySchool(@PathParam("schoolId") schoolId: Long): Response {
        val classes = schoolClassService.getClassesBySchool(schoolId)
        return Response.ok(classes).build()
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    fun updateClass(@PathParam("id") id: Long, request: UpdateSchoolClassRequest): Response {
        try {
            val schoolClass = schoolClassService.updateClass(id, request)
            return Response.ok(schoolClass).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    fun deleteClass(@PathParam("id") id: Long): Response {
        try {
            schoolClassService.deleteClass(id)
            return Response.noContent().build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @POST
    @Path("/{classId}/students/{studentId}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    fun addStudentToClass(
        @PathParam("classId") classId: Long, @PathParam("studentId") studentId: Long
    ): Response {
        try {
            val schoolClass = schoolClassService.addStudentToClass(classId, studentId)
            return Response.ok(schoolClass).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }

    @DELETE
    @Path("/{classId}/students/{studentId}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "SCHOOL_ADMIN")
    fun removeStudentFromClass(
        @PathParam("classId") classId: Long, @PathParam("studentId") studentId: Long
    ): Response {
        try {
            val schoolClass = schoolClassService.removeStudentFromClass(classId, studentId)
            return Response.ok(schoolClass).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        }
    }
}
