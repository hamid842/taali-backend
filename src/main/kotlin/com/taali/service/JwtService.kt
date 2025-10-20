package com.taali.service

import com.taali.entity.User
import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.jwt.Claims
import java.time.Duration

@ApplicationScoped
class JwtService {

    fun generateToken(user: User): String {
        val roles = mutableSetOf(user.role.toString())

        return Jwt.issuer("https://tcall.com")
            .upn(user.email)
            .subject(user.id.toString())
            .groups(roles)
            .claim(Claims.full_name, "${user.firstName} ${user.lastName}")
            .claim(Claims.email, user.email)
            .claim("userId", user.id)
            .claim("phone", user.phoneNumber)
            .expiresIn(Duration.ofHours(24))
            .sign()
    }
}