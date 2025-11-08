package com.taali.shared

import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory
import java.util.*

@RequestScoped
class RequestContext {

    private val logger = LoggerFactory.getLogger(RequestContext::class.java)

    var language: String = "en"
        private set

    var userId: Long? = null
        private set

    var userUUID: String? = null  // Add this to store the UUID
        private set

    var userRole: String? = null
        private set

    var uriInfo: UriInfo? = null
        private set

    var method: String? = null
        private set

    fun initializeFrom(containerRequest: ContainerRequestContext) {
        this.uriInfo = containerRequest.uriInfo
        this.method = containerRequest.method
        setLanguageFromHeader(containerRequest.getHeaderString("Accept-Language"))
    }

    fun setLanguageFromHeader(header: String?) {
        language = when {
            header.isNullOrBlank() -> "en"
            header.startsWith("fa", ignoreCase = true) -> "fa"
            else -> "en"
        }
    }

    // Keep existing method for Long IDs
    fun setUserContext(userId: Long?, userRole: String?) {
        this.userId = userId
        this.userRole = userRole
        logger.debug("Set user context - userId: $userId, userRole: $userRole")
    }

    // Add new method for UUID strings
    fun setUserContextFromUUID(userUUID: String?, userRole: String?) {
        this.userUUID = userUUID
        this.userRole = userRole

        // You'll need to look up the numeric ID from database
        this.userId = lookupNumericUserId(userUUID)

        logger.debug("Set user context - userUUID: $userUUID, numericUserId: $userId, userRole: $userRole")
    }

    fun clearUserContext() {
        userId = null
        userUUID = null
        userRole = null
    }

    fun isAuthenticated(): Boolean = userId != null || userUUID != null

    fun isSecure(): Boolean =
        uriInfo?.requestUri?.scheme.equals("https", ignoreCase = true)

    private fun lookupNumericUserId(uuid: String?): Long? {
        if (uuid == null) return null

        // TODO: Implement database lookup to convert UUID to numeric ID
        // For now, return a temporary value (hash of UUID)
        logger.warn("Using temporary UUID to numeric ID mapping for: $uuid")
        return uuid.hashCode().toLong()
    }
}