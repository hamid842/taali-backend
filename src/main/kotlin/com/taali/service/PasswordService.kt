package com.taali.service

import org.mindrot.jbcrypt.BCrypt
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PasswordService {

    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12))
    }

    fun checkPassword(plainPassword: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(plainPassword, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}