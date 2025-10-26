package com.taali.domain.service.shared

import com.taali.infrastructure.persistence.AppMessages
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.UriInfo
import java.util.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStreamReader

@ApplicationScoped
class TranslationService {

    private val logger: Logger = LoggerFactory.getLogger(TranslationService::class.java)

    @Inject
    lateinit var appMessages: AppMessages

    @Context
    lateinit var uriInfo: UriInfo

    @Context
    lateinit var headers: HttpHeaders

    @ConfigProperty(name = "quarkus.default-locale", defaultValue = "en")
    lateinit var defaultLocale: Locale

    fun getMessage(key: String, locale: Locale = Locale.ENGLISH, vararg args: Any): String {

        val actualLocale = if (locale.language == "fa") locale else getCurrentLocale()
        var message = getRawMessage(key, actualLocale)

        // If message is still the key, it means translation wasn't found
        if (message == key) {
            // Fallback to English or default message
            message = getRawMessage(key, Locale.ENGLISH)
            if (message == key) {
                // Final fallback
                return "Translation not found for: $key"
            }
        }

        args.forEachIndexed { index, arg ->
            message = message.replace("{$index}", arg.toString())
        }
        return message
    }


    fun getValidationMessage(key: String, locale: Locale, vararg args: Any): String {
        return getMessage("validation.$key", locale, *args)
    }

    fun getResponseMessage(key: String, locale: Locale, vararg args: Any): String {
        return getMessage("user.$key", locale, *args)
    }

    fun getEmailMessage(key: String, locale: Locale, vararg args: Any): String {
        return getMessage("email.$key", locale, *args)
    }

    fun getMessagesForLocale(locale: Locale): Map<String, String> {
        // Return all messages for frontend
        val messagesMap = mutableMapOf<String, String>()

        val allKeys = listOf(
            // Menu keys (existing)
            "menu.dashboard",
            "menu.schoolManagement",
            "menu.createSchool",
            "menu.listSchools",
            "menu.userManagement",
            "menu.createUser",
            "menu.listUsers",
            "menu.finance",
            "menu.teacherManagement",
            "menu.classManagement",
            "menu.studentManagement",
            "menu.parentManagement",
            "menu.myClasses",
            "menu.myStudents",
            "menu.attendance",
            "menu.assignments",
            "menu.myProfile",
            "menu.myGrades",
            "menu.myChildren",
            "menu.childrenGrades",
            "menu.childrenAttendance",
            "menu.payments",
            "menu.foodMenu",
            "menu.orders",
            "menu.inventory",
            "menu.financialReports",
            "menu.paymentManagement",
            "menu.invoices",

            // Toast keys (existing)
            "toast.otpSent",
            "toast.registerSuccess",
            "toast.registerFailed",
            "toast.loginSuccess",
            "toast.loginFailed",
            "toast.redirectingDashboard",

            // Validation keys (existing)
            "validation.firstName.required",
            "validation.firstName.size",
            "validation.firstName.pattern.en",
            "validation.firstName.pattern.fa",
            "validation.lastName.required",
            "validation.lastName.size",
            "validation.lastName.pattern.en",
            "validation.lastName.pattern.fa",
            "validation.email.required",
            "validation.email.format",
            "validation.email.exists",
            "validation.phone.required",
            "validation.phone.format",
            "validation.password.required",
            "validation.password.size",
            "validation.password.pattern",
            "validation.role.required",
            "validation.userId.required",
            "validation.otp.required",
            "validation.otp.size",

            // NEW: User and Auth keys
            "user.email_exists",
            "user.phone_exists",
            "user.weak_password",
            "user.not_found",
            "user.inactive",

            "auth.registration_success",
            "auth.registration_failed",
            "auth.otp_sent",
            "auth.otp_resent",
            "auth.invalid_otp",
            "auth.invalid_session",
            "auth.verification_failed",
            "auth.resend_failed",
            "auth.login_success",
            "auth.login_failed",
            "auth.invalid_credentials",
            "auth.account_locked",
            "auth.unauthorized",

            // NEW: Toast keys
            "toast.success",
            "toast.error",
            "toast.warning",
            "toast.info",
            "toast.loading",
            "toast.otpVerified",
            "toast.otpInvalid",
            "toast.otpExpired",
            "toast.profileUpdated",
            "toast.passwordChanged",

            // NEW: Email OTP keys
            "email.otp.subject",
            "email.otp.greeting",
            "email.otp.message",
            "email.otp.expiry",
            "email.otp.ignore"
        )

        allKeys.forEach { key -> messagesMap[key] = getRawMessage(key, locale) }

        return messagesMap
    }


    fun getRawMessage(key: String, locale: Locale): String {
        return try {
            val bundle = ResourceBundle.getBundle(
                "messages", locale,
                object : ResourceBundle.Control() {
                    override fun newBundle(
                        baseName: String,
                        locale: Locale,
                        format: String,
                        loader: ClassLoader,
                        reload: Boolean
                    ): ResourceBundle? {
                        val resourceName = toResourceName(toBundleName(baseName, locale), "properties")
                        return loader.getResourceAsStream(resourceName)?.use { inputStream ->
                            InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                                PropertyResourceBundle(reader)
                            }
                        }
                    }
                })
            bundle.getString(key)
        } catch (e: MissingResourceException) {
            // Fallback to English
            try {
                ResourceBundle.getBundle("messages", Locale.ENGLISH).getString(key)
            } catch (e2: MissingResourceException) {
                key
            }
        } catch (e: Exception) {
            key
        }
    }

    fun getCurrentLocale(): Locale {
        return try {
            // Get Accept-Language header safely
            val acceptLanguage = headers.acceptableLanguages.firstOrNull()

            if (acceptLanguage != null && acceptLanguage.language.isNotBlank()) {
                // Validate it's a real locale
                if (isValidLocale(acceptLanguage)) {
                    acceptLanguage
                } else {
                    Locale.ENGLISH
                }
            } else {
                Locale.ENGLISH
            }
        } catch (e: Exception) {
            logger.warn("Error parsing Accept-Language header, using English fallback", e)
            Locale.ENGLISH
        }
    }

    private fun isValidLocale(locale: Locale): Boolean {
        return try {
            // Check if it's a valid locale by trying to get available locales
            val availableLocales = Locale.getAvailableLocales()
            availableLocales.any { it.language == locale.language }
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentLanguage(): Locale {
        return getCurrentLocale()
    }
}