package com.taali.service

import at.favre.lib.crypto.bcrypt.BCrypt
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.LoggerFactory

@ApplicationScoped
class PasswordService {

    private val logger = LoggerFactory.getLogger(PasswordService::class.java)

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return try {
            BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified
        } catch (e: Exception) {
            logger.error("Password verification failed", e)
            false
        }
    }
}
