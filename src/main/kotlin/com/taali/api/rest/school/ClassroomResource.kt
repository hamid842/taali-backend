package com.taali.api.rest.school

import com.taali.api.dto.school.CreateClassroomRequest
import com.taali.api.dto.school.UpdateClassroomRequest
import com.taali.application.service.school.ClassroomService
import com.taali.shared.RequestContext
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/classrooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ClassroomResource {

    @Inject
    lateinit var classroomService: ClassroomService

    @Inject
    lateinit var requestContext: RequestContext

    @GET
    @Path("/my-classrooms")
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun getMyClassrooms(): Response {
        return try {
            val userId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()
            val classrooms = classroomService.getClassroomsByUser(userId)
            Response.ok(classrooms).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to fetch classrooms: ${e.message}")).build()
        }
    }


    @POST
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun createClassroom(request: CreateClassroomRequest): Response {
        return try {
            val userId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()
            val classroom = classroomService.createClassroom(request, userId)
            Response.status(Response.Status.CREATED).entity(classroom).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to create classroom: ${e.message}")).build()
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed( "ADMIN", "SUPERVISOR")
    fun getClassroom(@PathParam("id") id: Long): Response {
        return try {
            val classroom = classroomService.getClassroomById(id)
                ?: return Response.status(Response.Status.NOT_FOUND).build()
            Response.ok(classroom).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to fetch classroom: ${e.message}")).build()
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN", "OWNER")
    fun updateClassroom(@PathParam("id") id: Long, request: UpdateClassroomRequest): Response {
        return try {
            val updated = classroomService.updateClassroom(id, request)
            Response.ok(updated).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to update classroom: ${e.message}")).build()
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("OWNER", "ADMIN")
    fun deleteClassroom(@PathParam("id") id: Long): Response {
        return try {
            val deleted = classroomService.deleteClassroom(id)
            if (deleted) Response.noContent().build()
            else Response.status(Response.Status.NOT_FOUND).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to delete classroom: ${e.message}")).build()
        }
    }


    @GET
    @Path("/search")
    @RolesAllowed("OWNER", "ADMIN", "SUPERVISOR")
    fun searchClassrooms(@QueryParam("q") query: String): Response {
        return try {
            val classrooms = classroomService.searchClassrooms(query)
            Response.ok(classrooms).build()
        } catch (e: Exception) {
            Response.serverError().entity(mapOf("error" to "Failed to search classrooms: ${e.message}")).build()
        }
    }
}
