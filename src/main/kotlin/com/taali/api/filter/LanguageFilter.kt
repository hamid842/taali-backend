package com.taali.api.filter

import com.taali.shared.RequestContext
import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider

@Provider
@Priority(Priorities.AUTHENTICATION)
class LanguageFilter @Inject constructor(
    private val requestContext: RequestContext
) : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val langHeader = requestContext.getHeaderString("Accept-Language")
        this.requestContext.setLanguageFromHeader(langHeader)
    }
}