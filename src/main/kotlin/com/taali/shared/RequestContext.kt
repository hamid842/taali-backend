package com.taali.shared

import com.taali.api.filter.AuthenticationFilter
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory

@RequestScoped
class RequestContext {

    private val logger = LoggerFactory.getLogger(RequestContext::class.java)


    var language: String = "en"
        private set

    var userId: String? = null
        private set

    var userRole: String? = null
        private set

    var uriInfo: UriInfo? = null
        private set

    var method: String? = null
        private set

    fun initializeFrom(containerRequest: ContainerRequestContext) {
        // Store request metadata
        this.uriInfo = containerRequest.uriInfo
        this.method = containerRequest.method

        // Also set language from header immediately
        setLanguageFromHeader(containerRequest.getHeaderString("Accept-Language"))
    }

    fun setLanguageFromHeader(header: String?) {
        language = when {
            header.isNullOrBlank() -> "en"
            header.startsWith("fa", ignoreCase = true) -> "fa"
            else -> "en"
        }
    }

    fun setUserContext(userId: String?, userRole: String?) {
        this.userId = userId
        this.userRole = userRole
        logger.debug("Set user context - userId: $userId, userRole: $userRole")
    }

    fun clearUserContext() {
        userId = null
        userRole = null
    }

    fun isAuthenticated(): Boolean = userId != null

    fun isSecure(): Boolean =
        uriInfo?.requestUri?.scheme.equals("https", ignoreCase = true)
}
