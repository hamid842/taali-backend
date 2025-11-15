package com.taali.api.rest.school

import com.taali.application.service.school.TeacherService
import com.taali.domain.model.school.Teacher
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/teachers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TeacherResource {

    @Inject
    lateinit var teacherService: TeacherService

    @GET
    @Path("/school/{schoolId}")
    fun getTeachersBySchool(@RestPath schoolId: Long): Response {
        return try {
            val teachers = teacherService.getTeachersBySchool(schoolId)
            Response.ok(teachers).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teachers: ${e.message}"))
                .build()
        }
    }

    // NEW: Get active teachers by school
    @GET
    @Path("/school/{schoolId}/active")
    fun getActiveTeachersBySchool(@RestPath schoolId: Long): Response {
        return try {
            val teachers = teacherService.getActiveTeachersBySchool(schoolId)
            Response.ok(teachers).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch active teachers: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getTeacherById(@RestPath id: Long): Response {
        return try {
            val teacher = teacherService.getTeacherById(id)
            if (teacher != null) {
                Response.ok(teacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/classes")
    fun getTeacherClasses(@RestPath id: Long): Response {
        return try {
            val classes = teacherService.getTeacherClasses(id)
            Response.ok(classes).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher classes: ${e.message}"))
                .build()
        }
    }

    // DASHBOARD ENDPOINTS

    @GET
    @Path("/{id}/dashboard/stats")
    fun getTeacherDashboardStats(@RestPath id: Long): Response {
        return try {
            val stats = teacherService.getTeacherDashboardStats(id)
            Response.ok(stats).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher dashboard stats: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/teacher-classes")
    fun getTeacherClassesWithDetails(@RestPath id: Long): Response {
        return try {
            val classes = teacherService.getTeacherClassesWithDetails(id)
            Response.ok(classes).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teacher classes: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/upcoming-classes")
    fun getUpcomingClasses(@RestPath id: Long): Response {
        return try {
            val classes = teacherService.getUpcomingClasses(id)
            Response.ok(classes).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch upcoming classes: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/today-schedule")
    fun getTodaySchedule(@RestPath id: Long): Response {
        return try {
            val schedule = teacherService.getTodaySchedule(id)
            Response.ok(schedule).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch today's schedule: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/{id}/recent-activity")
    fun getRecentActivity(@RestPath id: Long): Response {
        return try {
            val activity = teacherService.getRecentActivity(id)
            Response.ok(activity).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch recent activity: ${e.message}"))
                .build()
        }
    }

    @POST
    @Transactional
    fun createTeacher(teacher: Teacher): Response {
        return try {
            val createdTeacher = teacherService.createTeacher(teacher)
            Response.status(Response.Status.CREATED).entity(createdTeacher).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to create teacher: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    fun updateTeacher(@RestPath id: Long, teacherData: Map<String, Any>): Response {
        return try {
            val updatedTeacher = teacherService.updateTeacher(id, teacherData)
            if (updatedTeacher != null) {
                Response.ok(updatedTeacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update teacher: ${e.message}"))
                .build()
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    fun deleteTeacher(@RestPath id: Long): Response {
        return try {
            val deleted = teacherService.deleteTeacher(id)
            if (deleted) {
                Response.ok(mapOf("message" to "Teacher deleted successfully")).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to delete teacher: ${e.message}"))
                .build()
        }
    }

    // Search endpoints
    @GET
    @Path("/search")
    fun searchTeachers(@QueryParam("schoolId") schoolId: Long, @QueryParam("q") searchTerm: String): Response {
        return try {
            val teachers = teacherService.searchTeachers(schoolId, searchTerm)
            Response.ok(teachers).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to search teachers: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/by-subject")
    fun getTeachersBySubject(@QueryParam("schoolId") schoolId: Long, @QueryParam("subject") subject: String): Response {
        return try {
            val teachers = teacherService.getTeachersBySubject(schoolId, subject)
            Response.ok(teachers).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch teachers by subject: ${e.message}"))
                .build()
        }
    }

    @PATCH
    @Path("/{id}/status")
    @Transactional
    fun updateTeacherStatus(@RestPath id: Long, request: Map<String, Boolean>): Response {
        return try {
            val isActive = request["isActive"] ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "isActive field is required")).build()

            val updatedTeacher = teacherService.updateTeacherStatus(id, isActive)
            if (updatedTeacher != null) {
                Response.ok(updatedTeacher).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update teacher status: ${e.message}"))
                .build()
        }
    }

    @POST
    @Path("/{id}/assign-classes")
    @Transactional
    fun assignClassesToTeacher(@RestPath id: Long, request: Map<String, List<Long>>): Response {
        return try {
            val classIds = request["classIds"] ?: emptyList()
            val success = teacherService.assignClassesToTeacher(id, classIds)
            if (success) {
                Response.ok(mapOf("message" to "Classes assigned successfully")).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Teacher not found"))
                    .build()
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to assign classes: ${e.message}"))
                .build()
        }
    }
}