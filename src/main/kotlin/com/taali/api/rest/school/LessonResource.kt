package com.taali.api.rest.school

import com.taali.application.service.school.ScheduleService
import com.taali.domain.model.school.Lesson
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger

@Path("/lessons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LessonResource {

    @Inject
    lateinit var scheduleService: ScheduleService

    private val logger: Logger = Logger.getLogger(LessonResource::class.java)

    @GET
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getAllLessons(): Response {
        try {
            logger.info("Fetching all lessons")
            val lessons = scheduleService.getAllLessons()
            return Response.ok(lessons).build()
        } catch (e: Exception) {
            logger.error("Failed to fetch lessons", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch lessons: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/available-grade-levels/{schoolId}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getAvailableGradeLevels(@PathParam("schoolId") schoolId: Long): Response {
        try {
            logger.info("Fetching available grade levels for school: $schoolId")
            val gradeLevels = scheduleService.getAvailableGradeLevelsForSchool(schoolId)
            return Response.ok(gradeLevels).build()
        } catch (e: Exception) {
            logger.error("Failed to fetch available grade levels for school: $schoolId", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch available grade levels: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/grade-level/{gradeLevel}/school/{schoolId}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getLessonsByGradeLevel(
        @PathParam("gradeLevel") gradeLevel: String,
        @PathParam("schoolId") schoolId: Long
    ): Response {
        try {
            logger.info("Fetching lessons for grade level: $gradeLevel in school: $schoolId")
            val lessons = scheduleService.getLessonsByGradeLevel(gradeLevel, schoolId)
            return Response.ok(lessons).build()
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid grade level: $gradeLevel for school: $schoolId")
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        } catch (e: Exception) {
            logger.error("Failed to fetch lessons for grade level: $gradeLevel in school: $schoolId", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch lessons: ${e.message}"))
                .build()
        }
    }

    @GET
    @Path("/grade-levels/school/{schoolId}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getLessonsByMultipleGradeLevels(
        @QueryParam("gradeLevels") gradeLevelsParam: String?,
        @PathParam("schoolId") schoolId: Long
    ): Response {
        try {
            logger.info("Fetching lessons for grade levels: $gradeLevelsParam in school: $schoolId")

            if (gradeLevelsParam.isNullOrBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "gradeLevels parameter is required"))
                    .build()
            }

            val gradeLevels = gradeLevelsParam.split(',').map { it.trim() }

            if (gradeLevels.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "At least one grade level must be provided"))
                    .build()
            }

            val lessons = scheduleService.getLessonsByGradeLevels(gradeLevels, schoolId)
            return Response.ok(lessons).build()
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid grade levels: $gradeLevelsParam for school: $schoolId - ${e.message}")
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        } catch (e: Exception) {
            logger.error("Failed to fetch lessons for grade levels: $gradeLevelsParam in school: $schoolId", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch lessons: ${e.message}"))
                .build()
        }
    }

    private fun validateGradeLevelForSchool(gradeLevel: String, availableGradeLevels: List<String>): String {
        val normalized = gradeLevel.trim().uppercase()

        if (normalized.isBlank()) {
            throw IllegalArgumentException("Grade level cannot be empty")
        }

        if (!availableGradeLevels.contains(normalized)) {
            throw IllegalArgumentException(
                "Invalid grade level: $gradeLevel. " +
                        "Available grade levels for this school: ${availableGradeLevels.joinToString()}"
            )
        }

        return normalized
    }



    @GET
    @Path("/{id}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun getLessonById(@PathParam("id") id: Long): Response {
        try {
            logger.info("Fetching lesson with ID: $id")
            val lesson = scheduleService.getLessonById(id)
            return Response.ok(lesson).build()
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.warn("Lesson not found with ID: $id")
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Lesson not found with ID: $id"))
                .build()
        } catch (e: Exception) {
            logger.error("Failed to fetch lesson with ID: $id", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch lesson: ${e.message}"))
                .build()
        }
    }

    @POST
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun createLesson(lesson: Lesson): Response {
        try {
            logger.info("Creating new lesson: ${lesson.name} for grade level: ${lesson.gradeLevel}")
            val createdLesson = scheduleService.createLesson(lesson)
            return Response.status(Response.Status.CREATED).entity(createdLesson).build()
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid lesson data: ${e.message}")
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        } catch (e: Exception) {
            logger.error("Failed to create lesson", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to create lesson: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun updateLesson(@PathParam("id") id: Long, lesson: Lesson): Response {
        try {
            logger.info("Updating lesson with ID: $id")

            // Ensure the ID in the path matches the ID in the body
            if (lesson.id != null && lesson.id != id) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(mapOf("error" to "Lesson ID in path does not match ID in body"))
                    .build()
            }

            val updatedLesson = scheduleService.updateLesson(id, lesson)
            return Response.ok(updatedLesson).build()
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid lesson data for update: ${e.message}")
            return Response.status(Response.Status.BAD_REQUEST).entity(
                mapOf("error" to e.message)
            ).build()
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.warn("Lesson not found with ID: $id")
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Lesson not found with ID: $id"))
                .build()
        } catch (e: Exception) {
            logger.error("Failed to update lesson with ID: $id", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to update lesson: ${e.message}"))
                .build()
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("OWNER","SCHOOL_MANAGER","SCHOOL_ADMIN")
    fun deleteLesson(@PathParam("id") id: Long): Response {
        try {
            logger.info("Deleting lesson with ID: $id")
            scheduleService.deleteLesson(id)
            return Response.noContent().build()
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.warn("Lesson not found with ID: $id")
            return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Lesson not found with ID: $id"))
                .build()
        } catch (e: Exception) {
            logger.error("Failed to delete lesson with ID: $id", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to delete lesson: ${e.message}"))
                .build()
        }
    }
}