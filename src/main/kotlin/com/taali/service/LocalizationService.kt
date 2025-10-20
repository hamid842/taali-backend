package com.taali.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import java.util.*

@ApplicationScoped
class LocalizationService {

    @Context lateinit var httpHeaders: HttpHeaders

    private val messages =
            mapOf(
                    "en" to
                            mapOf(
                                    "auth.register.success" to "Registration successful",
                                    "auth.register.error" to "Registration failed",
                                    "auth.register.email_exists" to "Email already exists",
                                    "auth.register.invalid_role" to
                                            "Invalid role. Valid roles are: {0}",
                                    "auth.login.success" to "Login successful",
                                    "auth.login.error" to "Login failed",
                                    "auth.login.invalid_credentials" to "Invalid email or password",
                                    "auth.login.account_inactive" to "Account is inactive",
                            ),
                    "fa" to
                            mapOf(
                                    "auth.register.success" to "ثبت‌نام با موفقیت انجام شد",
                                    "auth.register.error" to "ثبت‌نام انجام نشد",
                                    "auth.register.email_exists" to "این ایمیل قبلاً ثبت شده است",
                                    "auth.register.invalid_role" to
                                            "نقش نامعتبر است. نقش‌های معتبر: {0}",
                                    "auth.login.success" to "ورود موفقیت‌آمیز بود",
                                    "auth.login.error" to "ورود انجام نشد",
                                    "auth.login.invalid_credentials" to
                                            "ایمیل یا رمز عبور نادرست است",
                                    "auth.login.account_inactive" to "حساب کاربری غیرفعال است",
                            )
            )

    fun getMessage(key: String, locale: Locale = getCurrentLocale(), vararg params: Any): String {
        val lang = locale.language
        var message = messages[lang]?.get(key) ?: messages["en"]?.get(key) ?: key

        // Replace parameters
        params.forEachIndexed { index, param ->
            message = message.replace("{$index}", param.toString())
        }

        return message
    }

    fun getMessage(key: String, language: String, vararg params: Any): String {
        val locale = Locale.forLanguageTag(language)
        return getMessage(key, locale, *params)
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
}
