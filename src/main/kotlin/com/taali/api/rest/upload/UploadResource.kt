package com.taali.api.rest.upload

import com.taali.shared.RequestContext
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Path("/upload")
@Produces(MediaType.APPLICATION_JSON)
class UploadResource {

    @Inject
    lateinit var requestContext: RequestContext

    data class UploadResponse(
        val url: String, val filename: String, val size: Long?
    )

    // -------------------------------
    // Upload school logo
    // -------------------------------
    @POST
    @Path("/school-logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed("OWNER")
    fun uploadSchoolLogo(@RestForm("file") file: FileUpload?): Response {
        try {
            val userId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()

            if (file?.size()?.toInt() == 0) {
                return badRequest("File is required")
            }

            val allowedTypes = setOf("image/jpeg", "image/png", "image/webp", "image/gif")

            if (file?.contentType() !in allowedTypes) {
                return badRequest("Only JPEG, PNG, WebP, and GIF images are allowed")
            }

            file?.size()?.let {
                if (it > 5 * 1024 * 1024) {
                    return badRequest("File size must be less than 5MB")
                }
            }
            val originalFilename = file?.fileName() ?: "logo.png"
            val fileExtension = getFileExtension(originalFilename)
            val filename = "school-logo-${UUID.randomUUID()}.$fileExtension"

            val uploadDir = Paths.get("uploads/schools/logos")
            Files.createDirectories(uploadDir)

            val filePath = uploadDir.resolve(filename)
            Files.copy(file!!.uploadedFile(), filePath)

            val fileUrl = "/uploads/schools/logos/$filename"
            val response = UploadResponse(fileUrl, filename, file.size())

            return Response.status(Response.Status.CREATED).entity(response).build()
        } catch (e: Exception) {
            return internalError("Failed to upload image: ${e.message}")
        }
    }

    // -------------------------------
    // Upload profile image
    // -------------------------------
    @POST
    @Path("/profile-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed("OWNER", "SCHOOL_MANAGER", "TEACHER", "STUDENT")
    fun uploadProfileImage(@RestForm("file") file: FileUpload?): Response {
        try {
            val userId = requestContext.userId ?: return Response.status(Response.Status.UNAUTHORIZED).build()

            if (file?.size()?.toInt() == 0) {
                return badRequest("File is required")
            }

            val allowedTypes = setOf("image/jpeg", "image/png", "image/webp")
            if (file?.contentType() !in allowedTypes) {
                return badRequest("Only JPEG, PNG, and WebP images are allowed")
            }

            file?.size()?.let {
                if (it > 2 * 1024 * 1024) {
                    return badRequest("File size must be less than 2MB")
                }
            }
            val originalFilename = file?.fileName() ?: "logo.png"
            val fileExtension = getFileExtension(originalFilename)
            val filename = "profile-$userId-${UUID.randomUUID()}.$fileExtension"

            val uploadDir = Paths.get("uploads/profiles")
            Files.createDirectories(uploadDir)

            val filePath = uploadDir.resolve(filename)
            Files.copy(file!!.uploadedFile(), filePath)

            val fileUrl = "/uploads/profiles/$filename"
            val response = UploadResponse(fileUrl, filename, file.size())

            return Response.status(Response.Status.CREATED).entity(response).build()
        } catch (e: Exception) {
            return internalError("Failed to upload profile image: ${e.message}")
        }
    }

    // -------------------------------
    // Helpers
    // -------------------------------
    private fun getFileExtension(filename: String?): String {
        if (filename.isNullOrEmpty()) return "png" // default fallback
        return filename.substringAfterLast('.', "").lowercase().takeIf { it.isNotEmpty() } ?: "png"
    }

    private fun badRequest(message: String): Response =
        Response.status(Response.Status.BAD_REQUEST).entity(mapOf("error" to message)).build()

    private fun internalError(message: String): Response =
        Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(mapOf("error" to message)).build()
}
