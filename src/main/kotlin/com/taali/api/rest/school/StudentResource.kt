package com.taali.api.rest.school

import com.taali.api.dto.school.request.AssignClassRequest
import com.taali.api.dto.school.request.BulkAssignClassRequest
import com.taali.api.dto.school.request.ParentAssociationRequest
import com.taali.api.dto.school.request.StudentDetailsRequest
import com.taali.api.dto.shared.ApiResponse
import com.taali.application.service.school.StudentService
import jakarta.annotation.security.RolesAllowed
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
    fun getStudentsBySchool(
        @RestPath schoolId: Long,
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
        @QueryParam("search") search: String?,
        @QueryParam("gradeLevel") gradeLevel: String?,
        @QueryParam("classId") classId: Long?
    ): Response {
        return try {
            val studentsPage = studentService.getStudentsBySchool(schoolId, page, size, search, gradeLevel, classId)
            Response.ok(studentsPage).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}")).build()
        }
    }

    @GET
    @Path("/{studentId}/teachers")
    @RolesAllowed("PARENT","STUDENT","OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getTeachersByStudent(@RestPath studentId: Long): Response {
        return try {
            val teachers = studentService.getTeachersByStudent(studentId)
            Response.ok(ApiResponse.success(teachers)).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error<Any>(e.message ?: "Student not found"))
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error<Any>("Failed to fetch teachers: ${e.message}"))
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
                .entity(mapOf("error" to "Failed to fetch classes: ${e.message}")).build()
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
                Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "Student not found")).build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch student: ${e.message}")).build()
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
                .entity(mapOf("error" to "Failed to fetch students: ${e.message}")).build()
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
                .entity(mapOf("error" to "Failed to fetch grade levels: ${e.message}")).build()
        }
    }

    // NEW: Assign student to class
    @POST
    @Path("/{studentId}/assign-class")
    @Transactional
    fun assignStudentToClass(
        @RestPath studentId: Long, request: AssignClassRequest
    ): Response {
        return try {
            val success = studentService.assignStudentToClass(studentId, request.classId)
            if (success) {
                Response.ok(mapOf("message" to "Student assigned to class successfully")).build()
            } else {
                Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "Student or class not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to assign student to class: ${e.message}")).build()
        }
    }

    // NEW: Remove student from class
    @DELETE
    @Path("/{studentId}/class")
    @Transactional
    fun removeStudentFromClass(@RestPath studentId: Long): Response {
        return try {
            val success = studentService.removeStudentFromClass(studentId)
            if (success) {
                Response.ok(mapOf("message" to "Student removed from class successfully")).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Student not found or not in any class")).build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to remove student from class: ${e.message}")).build()
        }
    }

    // NEW: Bulk assign students to class
    @POST
    @Path("/bulk-assign-class")
    @Transactional
    fun bulkAssignStudentsToClass(request: BulkAssignClassRequest): Response {
        return try {
            val results = studentService.bulkAssignStudentsToClass(request.studentIds, request.classId)
            Response.ok(results).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to assign students to class: ${e.message}")).build()
        }
    }

    // Step 2: Update student details
    @PUT
    @Path("/user/{userId}/details")
    @Transactional
    fun updateStudentDetails(
        @RestPath userId: Long, details: StudentDetailsRequest
    ): Response {
        return try {
            val updatedStudent = studentService.updateStudentDetailsByUser(userId, details)
            if (updatedStudent != null) {
                Response.ok(updatedStudent).build()
            } else {
                Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "Student not found for this user"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update student details: ${e.message}")).build()
        }
    }

    // Step 3: Associate parents with student
    @POST
    @Path("/user/{userId}/parents")
    @Transactional
    fun associateParents(
        @RestPath userId: String, parentData: ParentAssociationRequest
    ): Response {
        return try {
            val result = studentService.associateParentsByUser(userId, parentData)
            Response.ok(result).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to associate parents: ${e.message}")).build()
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
                Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "Student not found for this user"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch student: ${e.message}")).build()
        }
    }
}


