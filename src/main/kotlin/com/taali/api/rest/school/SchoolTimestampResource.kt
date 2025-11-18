package com.taali.api.rest.school

import com.taali.api.dto.school.request.CreateClassTimestampRequest
import com.taali.api.dto.school.request.UpdateClassTimestampRequest
import com.taali.application.service.school.SchoolService
import com.taali.application.service.school.SchoolTimestampService
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/school")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "School Management")
class SchoolTimestampResource {

    @Inject lateinit var schoolService: SchoolService

    @Inject
    lateinit var timestampService: SchoolTimestampService

    @GET
    @Path("/{schoolId}/timestamps")
    @RolesAllowed("SCHOOL_MANAGER", "SCHOOL_ADMIN","OWNER")
    fun getTimestamps(@PathParam("schoolId") schoolId: Long): Response {
        return Response.ok(timestampService.getClassTimestamps(schoolId)).build()
    }

    @POST
    @Path("/{schoolId}/timestamps")
    @RolesAllowed("SCHOOL_MANAGER", "SCHOOL_ADMIN","OWNER")
    fun createTimestamp(
        @PathParam("schoolId") schoolId: Long,
        request: CreateClassTimestampRequest
    ): Response {
        return Response.ok(timestampService.createClassTimestamp(schoolId, request)).build()
    }

    @PUT
    @Path("/timestamps/{id}")
    @RolesAllowed("SCHOOL_MANAGER", "SCHOOL_ADMIN","OWNER")
    fun updateTimestamp(
        @PathParam("id") id: Long,
        request: UpdateClassTimestampRequest
    ): Response {
        return Response.ok(timestampService.updateClassTimestamp(id, request)).build()
    }

    @DELETE
    @Path("/timestamps/{id}")
    @RolesAllowed("SCHOOL_MANAGER", "SCHOOL_ADMIN","OWNER")
    fun deleteTimestamp(@PathParam("id") id: Long): Response {
        timestampService.deleteClassTimestamp(id)
        return Response.noContent().build()
    }

    @GET
    @Path("/{schoolId}/available-timestamp-types")
    @RolesAllowed("SCHOOL_MANAGER", "SCHOOL_ADMIN","OWNER")
    fun getAvailableTypes(@PathParam("schoolId") schoolId: Long): Response {
        return Response.ok(timestampService.getAvailableTypes(schoolId)).build()
    }
}