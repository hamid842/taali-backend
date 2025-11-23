package com.taali.api.rest.parent

import com.taali.api.dto.parent.request.CreateParentRequest
import com.taali.api.dto.parent.response.*
import com.taali.application.service.parent.ParentService
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger

@Path("/parents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("PARENT","SCHOOL_MANAGER","SCHOOL_ADMIN","OWNER")
class ParentResource {

    @Inject
    lateinit var parentService: ParentService

    @Inject
    lateinit var jwt: JsonWebToken

    @Context
    lateinit var securityContext: SecurityContext

    private val logger = Logger.getLogger(ParentResource::class.java)

    // REMOVE Principal parameter from all methods - use SecurityContext instead

    @POST
    fun createParent(createParentRequest: CreateParentRequest): Response {
        try {
            val parent = parentService.createParent(createParentRequest)
            return Response.status(Response.Status.CREATED).entity(parent).build()
        } catch (e: Exception) {
            logger.error("Failed to create parent", e)
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @POST
    @Path("/{studentId}/associate")
    fun associateParentsWithStudent(
        @PathParam("studentId") studentId: Long,
        parentIds: List<Long>
    ): Response {
        try {
            val student = parentService.associateParentsWithStudent(studentId, parentIds)
            return Response.ok(student).build()
        } catch (e: Exception) {
            logger.error("Failed to associate parents with student", e)
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/student/{studentId}")
    fun getParentsByStudent(@PathParam("studentId") studentId: Long): Response {
        try {
            val parents = parentService.findParentsByStudent(studentId)
            return Response.ok(parents).build()
        } catch (e: Exception) {
            logger.error("Failed to get parents by student", e)
            return Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/dashboard/stats")
    fun getDashboardStats(): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            logger.info("Fetching dashboard stats for user: $email")
            val stats = parentService.getDashboardStats(email)
            return Response.ok(stats).build()
        } catch (e: SecurityException) {
            logger.error("Security exception in getDashboardStats", e)
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            logger.error("Failed to get dashboard stats", e)
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/my-children")
    fun getMyChildren(): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            logger.info("Fetching children for user: $email")
            val children = parentService.getMyChildren(email)
            return Response.ok(children).build()
        } catch (e: SecurityException) {
            logger.error("Security exception in getMyChildren", e)
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            logger.error("Failed to get children", e)
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/children/{childId}")
    fun getChildDetail(@PathParam("childId") childId: Long): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            val childDetail = parentService.getChildDetail(email, childId)
            return Response.ok(childDetail).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/children/attendance")
    fun getChildrenAttendance(): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            val attendance = parentService.getChildrenAttendance(email)
            return Response.ok(attendance).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/children/grades")
    fun getChildrenGrades(): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            val grades = parentService.getChildrenGrades(email)
            return Response.ok(grades).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/children/{childId}/attendance")
    fun getChildAttendance(@PathParam("childId") childId: Long): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            val attendance = parentService.getChildAttendance(email, childId)
            return Response.ok(attendance).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    @GET
    @Path("/children/{childId}/grades")
    fun getChildGrades(@PathParam("childId") childId: Long): Response { // REMOVED Principal parameter
        try {
            val email = getCurrentUserEmail()
            val grades = parentService.getChildGrades(email, childId)
            return Response.ok(grades).build()
        } catch (e: SecurityException) {
            return Response.status(Response.Status.FORBIDDEN).entity(mapOf("error" to e.message)).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to e.message)).build()
        }
    }

    // Helper method to get current user email from SecurityContext
    private fun getCurrentUserEmail(): String {
        val principal = securityContext.userPrincipal
        if (principal == null) {
            logger.error("User principal is null - user not authenticated")
            throw SecurityException("User not authenticated")
        }

        if (!securityContext.isUserInRole("PARENT")) {
            logger.error("User does not have PARENT role: ${principal.name}")
            throw SecurityException("User does not have PARENT role")
        }

        // Try to get email from JWT token first (more reliable)
        return if (jwt.claim<String>("email").isPresent) {
            jwt.claim<String>("email").get().toString().also { email ->
                logger.debug("Got email from JWT: $email")
            }
        } else {
            // Fallback to principal name
            principal.name.also { email ->
                logger.debug("Got email from principal name: $email")
            }
        }
    }
}