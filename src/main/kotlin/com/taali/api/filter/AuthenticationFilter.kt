package com.taali.api.filter

import com.taali.shared.RequestContext
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider
import org.eclipse.microprofile.jwt.JsonWebToken
import org.slf4j.LoggerFactory

@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION)
class AuthenticationFilter : ContainerRequestFilter {

    @Inject
    lateinit var requestContext: RequestContext

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun filter(containerContext: ContainerRequestContext) {
        try {
            val path = containerContext.uriInfo.path.lowercase()
            val fullPath = containerContext.uriInfo.requestUri.path.lowercase()

            logger.debug("Processing request for path: $fullPath")

            // Skip authentication for public endpoints
            if (fullPath.contains("/auth/") ||
                fullPath.contains("/public/") ||
                fullPath.contains("/health") ||
                path.contains("auth") ||
                path.contains("public") ||
                path.contains("health")
            ) {
                logger.debug("Skipping authentication for public path: $fullPath")
                return
            }

            // Get the security context from Quarkus JWT
            val securityContext = containerContext.securityContext
            val principal = securityContext.userPrincipal

            if (principal != null && principal is JsonWebToken) {
                logger.debug("User authenticated: ${principal.name}")

                // Extract user ID from JWT claims
                val userId = extractUserIdFromToken(principal)

                // Check for roles
                val roles = listOf("OWNER", "ADMIN", "TEACHER", "USER")
                val userRole = roles.firstOrNull { securityContext.isUserInRole(it) }

                // Use the correct method for UUID strings
                requestContext.setUserContextFromUUID(userId, userRole)
                logger.debug("Set user context: $userId with role: $userRole")
            } else {
                logger.warn("No authenticated user found for path: $fullPath")
                // Don't abort here - let Quarkus security handle it
            }


            // Language header
            val languageHeader = containerContext.getHeaderString("Accept-Language")
            requestContext.setLanguageFromHeader(languageHeader)
        } catch (e: Exception) {
            logger.error("Error in authentication filter", e)
            requestContext.clearUserContext()
        }
    }

    private fun extractUserIdFromToken(token: JsonWebToken): String? {
        return try {
            // Simply return the UUID string from the token
            token.getClaim<String>("userId")
        } catch (e: Exception) {
            logger.warn("Failed to extract user ID from token: ${e.message}")
            null
        }
    }
}