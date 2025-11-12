package com.taali.api.rest.school

import com.taali.api.dto.school.request.ParentAssociationRequest
import com.taali.api.dto.school.request.StudentDetailsRequest
import com.taali.application.service.school.StudentService
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentResource {

    @Inject
    lateinit var studentService: StudentService

    @GET
    @Path("/school/{schoolId}")
    fun getStudentsBySchool( @RestPath schoolId: Long,
                             @QueryParam("page") @DefaultValue("0") page: Int,
                             @QueryParam("size") @DefaultValue("20") size: Int,
                             @QueryParam("search") search: String?,
                             @QueryParam("gradeLevel") gradeLevel: String?,
                             @QueryParam("classId") classId: Long?): Response {
        return try {
            val studentsPage = studentService.getStudentsBySchool(schoolId, page, size, search, gradeLevel, classId)
            Response.ok(studentsPage).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/school/{schoolId}/classes")
    fun getClassesBySchool(@RestPath schoolId: Long): Response {
        return try {
            val classes = studentService.getClassesBySchool(schoolId)
            Response.ok(classes).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch classes: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getStudentById(@RestPath id: Long): Response {
        return try {
            val student = studentService.getStudentById(id)
            if (student != null) {
                Response.ok(student).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Student not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch student: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/class/{classId}")
    fun getStudentsByClass(@RestPath classId: Long): Response {
        return try {
            val students = studentService.getStudentsByClass(classId)
            Response.ok(students).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/school/{schoolId}/grade-levels")
    fun getGradeLevelsBySchool(@RestPath schoolId: Long): Response {
        return try {
            val gradeLevels = studentService.getGradeLevelsBySchool(schoolId)
            Response.ok(gradeLevels).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch grade levels: ${e.message}"))
                .build()
        }
    }

    // Step 2: Update student details
    @PUT
    @Path("/user/{userId}/details")
    @Transactional
    fun updateStudentDetails(
        @RestPath userId: Long,
        details: StudentDetailsRequest
    ): Response {
        return try {
            val updatedStudent = studentService.updateStudentDetailsByUser(userId, details)
            if (updatedStudent != null) {
                Response.ok(updatedStudent).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Student not found for this user"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update student details: ${e.message}"))
                .build()
        }
    }

    // Step 3: Associate parents with student
    @POST
    @Path("/user/{userId}/parents")
    @Transactional
    fun associateParents(
        @RestPath userId: String,
        parentData: ParentAssociationRequest
    ): Response {
        return try {
            val result = studentService.associateParentsByUser(userId, parentData)
            Response.ok(result).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to associate parents: ${e.message}"))
                .build()
        }
    }

    // Get student by user ID (useful after step 1)
    @GET
    @Path("/user/{userId}")
    fun getStudentByUser(@RestPath userId: String): Response {
        return try {
            val student = studentService.getStudentByUser(userId)
            if (student != null) {
                Response.ok(student).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Student not found for this user"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch student: ${e.message}"))
                .build()
        }
    }
}





