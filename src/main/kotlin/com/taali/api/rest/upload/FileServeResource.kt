package com.taali.api.rest.upload

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.StreamingOutput
import java.io.IOException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

@Path("/uploads")
class FileServeResource {

    // -------------------------------
    // Serve school logo
    // -------------------------------
    @GET
    @Path("/schools/logos/{filename}")
    @Produces("image/*")
    fun getSchoolLogo(@PathParam("filename") filename: String): Response {
        return serveFile("uploads/schools/logos", filename)
    }

    // -------------------------------
    // Serve profile image
    // -------------------------------
    @GET
    @Path("/profiles/{filename}")
    @Produces("image/*")
    fun getProfileImage(@PathParam("filename") filename: String): Response {
        return serveFile("uploads/profiles", filename)
    }

    // -------------------------------
    // File serving logic
    // -------------------------------
    private fun serveFile(directory: String, filename: String): Response {
        try {
            // Security check — prevent directory traversal
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid filename")
                    .build()
            }

//            val filePath: java.nio.file.Path = Paths.get(directory, filename)
//            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
//                return Response.status(Response.Status.NOT_FOUND)
//                    .entity("File not found")
//                    .build()
//            }
//
//            val stream = StreamingOutput { output ->
//                Files.newInputStream(filePath).use { input ->
//                    input.copyTo(output)
//                }
//            }
//
//            val contentType = detectContentType(filePath)
//            return Response.ok(stream)
//                .type(contentType)
//                .header("Cache-Control", "public, max-age=86400") // Cache for 1 day
//                .build()
            // Always resolve relative to project root (one level above /target if in prod)
            val basePath = Paths.get(System.getProperty("user.dir"))
            val filePath = basePath.resolve(directory).resolve(filename).normalize()

            println(">>> Looking for file: ${filePath.toAbsolutePath()}") // <-- DEBUG

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found: ${filePath.toAbsolutePath()}")
                    .build()
            }

            val stream = StreamingOutput { output ->
                Files.newInputStream(filePath).use { input ->
                    input.copyTo(output)
                }
            }

            val contentType = detectContentType(filePath)
            return Response.ok(stream)
                .type(contentType)
                .header("Cache-Control", "public, max-age=86400")
                .build()

        } catch (e: NoSuchFileException) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("File not found")
                .build()
        } catch (e: IOException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("I/O error while reading file: ${e.message}")
                .build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Failed to serve file: ${e.message}")
                .build()
        }
    }

    // -------------------------------
    // Helpers
    // -------------------------------
    private fun detectContentType(filePath: java.nio.file.Path): String {
        val filename = filePath.fileName.toString().lowercase()
        return when {
            filename.endsWith(".png") -> MediaType.valueOf("image/png")
            filename.endsWith(".jpg") || filename.endsWith(".jpeg") -> MediaType.valueOf("image/jpeg")
            filename.endsWith(".webp") -> MediaType.valueOf("image/webp")
            filename.endsWith(".gif") -> MediaType.valueOf("image/gif")
            else -> MediaType.APPLICATION_OCTET_STREAM
        }.toString()
    }
}
