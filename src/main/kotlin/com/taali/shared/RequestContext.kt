package com.taali.shared

import com.taali.domain.enum.UserRole
import jakarta.enterprise.context.RequestScoped

@RequestScoped
class RequestContext {
    var language: String = "en"
    var userRole: UserRole? = null
    // Add other common request data here
}