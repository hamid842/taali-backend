package com.taali.shared

import com.taali.domain.model.user.User
import com.taali.domain.repository.user.UserRepository
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.UriInfo
import org.slf4j.LoggerFactory
import java.util.*

@RequestScoped
class RequestContext {

    private val logger = LoggerFactory.getLogger(RequestContext::class.java)

    @Inject
    lateinit var userRepository: UserRepository

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

    fun setUserContextFromUUID(userUUID: String?, userRole: String?) {
        this.userUUID = userUUID
        this.userRole = userRole
        this.userId = lookupNumericUserId(userUUID)

        logger.info("Authenticated userId in RequestContext: $userId, userUUID: $userUUID, role: $userRole")
    }

    fun clearUserContext() {
        userId = null
        userUUID = null
        userRole = null
    }

    private fun lookupNumericUserId(uuid: String?): Long? {
        if (uuid == null) return null

        // ✅ Correct query: field name is "userId", not "uuid"
        val user = User.find("userId", UUID.fromString(uuid)).firstResult()
        if (user != null) {
            return user.id
        }

        logger.warn("User with UUID $uuid not found in database")
        return null
    }
}