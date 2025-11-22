package com.taali.api.rest.school

import com.taali.api.dto.school.*
import com.taali.application.service.school.SchoolService
import com.taali.shared.RequestContext
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/schools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SchoolResource {

    @Inject
    lateinit var schoolService: SchoolService

    @Inject
    lateinit var requestContext: RequestContext

    @GET
    @Path("/my-schools")
    @RolesAllowed("OWNER")
    fun getMySchools(): Response {
        try {
            val ownerId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()
            println("Current user ID from context: $ownerId")
            val schools = schoolService.getSchoolsByOwner(ownerId)
            return Response.ok(schools).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch schools: ${e.message}")).build()
        }
    }

    @POST
    @RolesAllowed("OWNER")
    fun createSchool(request: CreateSchoolRequest): Response {
        try {
            val ownerId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()

            val school = schoolService.createSchool(request, ownerId)
            return Response.status(Response.Status.CREATED).entity(school).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Failed to create school: ${e.message}")).build()
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER")
    fun getSchool(@PathParam("id") id: Long): Response {
        try {
            val school = schoolService.getSchoolById(id) ?: return Response.status(Response.Status.NOT_FOUND).build()

            return Response.ok(school).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch school: ${e.message}")).build()
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER")
    fun updateSchool(@PathParam("id") id: Long, request: UpdateSchoolRequest): Response {
        try {
            val school = schoolService.updateSchool(id, request)
            return Response.ok(school).build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Failed to update school: ${e.message}")).build()
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("OWNER")
    fun deleteSchool(@PathParam("id") id: Long): Response {
        try {
            val deleted = schoolService.deleteSchool(id)
            return if (deleted) {
                Response.noContent().build()
            } else {
                Response.status(Response.Status.NOT_FOUND).build()
            }
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        } catch (e: IllegalStateException) {
            return Response.status(Response.Status.CONFLICT).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to delete school: ${e.message}")).build()
        }
    }

    @PATCH
    @Path("/{id}/logo")
    @RolesAllowed("OWNER", "SCHOOL_MANAGER")
    fun updateSchoolLogo(
        @PathParam("id") id: Long, @QueryParam("imageUrl") imageUrl: String
    ): Response {
        try {
            val school = schoolService.updateSchoolLogo(id, imageUrl)
            return Response.ok(school).build()
        } catch (e: NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update school logo: ${e.message}")).build()
        }
    }

    @GET
    @Path("/search")
    @RolesAllowed("OWNER")
    fun searchSchools(@QueryParam("q") query: String): Response {
        try {
            val schools = schoolService.searchSchools(query)
            return Response.ok(schools).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to search schools: ${e.message}")).build()
        }
    }
}