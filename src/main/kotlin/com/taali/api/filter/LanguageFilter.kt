package com.taali.api.filter

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider
import java.util.Locale

@Provider
class LanguageFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val langHeader = requestContext.headers.getFirst("Accept-Language")
        val locale = if (!langHeader.isNullOrBlank()) Locale.forLanguageTag(langHeader) else Locale.getDefault()
        Locale.setDefault(locale)
    }
}