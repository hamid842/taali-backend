package com.taali.shared

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RequestContext {
    final var language: String = "en"
        private set

    fun setLanguageFromHeader(header: String?) {
        language = when {
            header.isNullOrBlank() -> "en"
            header.startsWith("fa", ignoreCase = true) -> "fa"
            else -> "en"
        }
    }
}