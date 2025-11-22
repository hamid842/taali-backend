package com.taali.api.rest.message

import com.taali.api.dto.shared.ApiResponse
import com.taali.application.service.message.UserLookupService
import com.taali.domain.enum.UserRole
import com.taali.domain.model.user.User
import com.taali.domain.model.school.Student
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
class UserLookupResource {

    @Inject
    lateinit var userLookupService: UserLookupService

    @GET
    @Path("/teachers")
    fun getTeachersForParent(@HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val user = User.findById(userId)
                ?: return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error<List<User>>("User not found"))
                    .build()

            if (user.role != UserRole.PARENT) {
                return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponse.error<List<User>>("Only parents can access this endpoint"))
                    .build()
            }

            val teachers = userLookupService.getTeachersForParent(userId)

            Response.ok(ApiResponse.success("Teachers retrieved successfully", teachers))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<List<User>>("Failed to fetch teachers: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/parents")
    fun getParentsForTeacher(@HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val user = User.findById(userId)
                ?: return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error<List<User>>("User not found"))
                    .build()

            if (user.role != UserRole.TEACHER) {
                return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponse.error<List<User>>("Only teachers can access this endpoint"))
                    .build()
            }

            val parents = userLookupService.getParentsForTeacher(userId)

            Response.ok(ApiResponse.success("Parents retrieved successfully", parents))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<List<User>>("Failed to fetch parents: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/students")
    fun getStudentsForParent(@HeaderParam("X-User-Id") userId: Long): Response {
        return try {
            val user = User.findById(userId)
                ?: return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error<List<Student>>("User not found"))
                    .build()

            if (user.role != UserRole.PARENT) {
                return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponse.error<List<Student>>("Only parents can access this endpoint"))
                    .build()
            }

            val students = userLookupService.getStudentsForParent(userId)

            Response.ok(ApiResponse.success("Students retrieved successfully", students))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<List<Student>>("Failed to fetch students: ${e.message}"))
                .build()
        }
    }
}