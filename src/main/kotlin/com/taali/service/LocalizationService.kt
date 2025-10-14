package com.taali.service

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders

@ApplicationScoped
class LocalizationService {

    @Inject
    @Location("messages")
    lateinit var messagesTemplate: Template

    @Context
    lateinit var httpHeaders: HttpHeaders

    fun getMessage(key: String, locale: Locale = getCurrentLocale(), vararg params: Any): String {
        return try {
            val template = messagesTemplate.instance()
                .setAttribute("locale", locale)
                .data(key, key)

            params.forEachIndexed { index, param ->
                template.data("param$index", param)
            }

            template.render().trim()
        } catch (e: Exception) {
            key // Fallback to key if translation not found
        }
    }

    fun getCurrentLocale(): Locale {
        return try {
            val acceptLanguage = httpHeaders.getHeaderString("Accept-Language")
            if (!acceptLanguage.isNullOrEmpty()) {
                Locale.forLanguageTag(acceptLanguage)
            } else {
                Locale.ENGLISH
            }
        } catch (e: Exception) {
            Locale.ENGLISH
        }
    }

    fun getSupportedLocales(): List<Locale> {
        return listOf(Locale.ENGLISH, Locale("fa"))
    }
}