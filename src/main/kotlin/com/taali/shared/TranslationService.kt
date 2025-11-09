package com.taali.shared

import io.quarkus.qute.i18n.Localized
import io.quarkus.qute.i18n.MessageBundles
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.util.Locale

@ApplicationScoped
class TranslationService {

    private val logger = LoggerFactory.getLogger(TranslationService::class.java)

    @Inject
    lateinit var requestContext: RequestContext

    // Main translation method without parameters
    fun translate(key: String): String {
        return translateText(key)
    }

    // Translation with parameters
    fun translate(key: String, locale: String, vararg args: Any): String {
        return translateText(key, locale, *args)
    }


    fun getValidationMessage(key: String, locale: String, vararg args: Array<out Any>): String {
        return translate("validation_$key", locale, *args)
    }

    fun getResponseMessage(key: String, locale: String, vararg args: Any): String {
        return translate("user_$key", locale, *args)
    }

    fun getEmailMessage(key: String, locale: String, vararg args: Any): String {
        return translate("email_$key", locale, *args)
    }


    fun translateText(key: String, locale: String = requestContext.language, vararg args: Any): String {

        return try {
            // Handle both "fa" and "fa-IR" locales
            val targetLocale = when {
                locale.equals("fa", ignoreCase = true) || locale.equals("fa-IR", ignoreCase = true) -> {
                    Locale.forLanguageTag("fa-IR")
                }

                else -> Locale.ENGLISH
            }

            val localizedMessages =
                MessageBundles.get(AppMessages::class.java, Localized.Literal.of(targetLocale.language))
            when (key) {
                // Menu translations - updated to snake_case
                "menu_dashboard" -> localizedMessages.menu_dashboard()
                "menu_school_management" -> localizedMessages.menu_school_management()
                "menu_create_school" -> localizedMessages.menu_create_school()
                "menu_list_schools" -> localizedMessages.menu_list_schools()
                "menu_user_management" -> localizedMessages.menu_user_management()
                "menu_create_user" -> localizedMessages.menu_create_user()
                "menu_list_users" -> localizedMessages.menu_list_users()
                "menu_finance" -> localizedMessages.menu_finance()
                "menu_teacher_management" -> localizedMessages.menu_teacher_management()
                "menu_class_management" -> localizedMessages.menu_class_management()
                "menu_student_management" -> localizedMessages.menu_student_management()
                "menu_parent_management" -> localizedMessages.menu_parent_management()
                "menu_my_classes" -> localizedMessages.menu_my_classes()
                "menu_my_students" -> localizedMessages.menu_my_students()
                "menu_attendance" -> localizedMessages.menu_attendance()
                "menu_assignments" -> localizedMessages.menu_assignments()
                "menu_my_profile" -> localizedMessages.menu_my_profile()
                "menu_my_grades" -> localizedMessages.menu_my_grades()
                "menu_my_children" -> localizedMessages.menu_my_children()
                "menu_children_grades" -> localizedMessages.menu_children_grades()
                "menu_children_attendance" -> localizedMessages.menu_children_attendance()
                "menu_payments" -> localizedMessages.menu_payments()
                "menu_food_menu" -> localizedMessages.menu_food_menu()
                "menu_orders" -> localizedMessages.menu_orders()
                "menu_inventory" -> localizedMessages.menu_inventory()
                "menu_financial_reports" -> localizedMessages.menu_financial_reports()
                "menu_payment_management" -> localizedMessages.menu_payment_management()
                "menu_invoices" -> localizedMessages.menu_invoices()
                "menu_billing" -> localizedMessages.menu_billing()
                "menu_fee_management" -> localizedMessages.menu_fee_management()
                "menu_expense_management" -> localizedMessages.menu_expense_management()
                "menu_grades" -> localizedMessages.menu_grades()
                "menu_lesson_plans" -> localizedMessages.menu_lesson_plans()
                "menu_my_assignments" -> localizedMessages.menu_my_assignments()
                "menu_my_attendance" -> localizedMessages.menu_my_attendance()
                "menu_schedule" -> localizedMessages.menu_schedule()
                "menu_notifications" -> localizedMessages.menu_notifications()
                "menu_attendance_reports" -> localizedMessages.menu_attendance_reports()
                "menu_academic_calendar" -> localizedMessages.menu_academic_calendar()
                "menu_sales_reports" -> localizedMessages.menu_sales_reports()
                "menu_payment_tracking" -> localizedMessages.menu_payment_tracking()
                "menu_list_teachers" -> localizedMessages.menu_list_teachers()
                "menu_create_teacher" -> localizedMessages.menu_create_teacher()
                "menu_list_classes" -> localizedMessages.menu_list_classes()
                "menu_create_class" -> localizedMessages.menu_create_class()
                "menu_list_students" -> localizedMessages.menu_list_students()
                "menu_create_student" -> localizedMessages.menu_create_student()
                "menu_list_parents" -> localizedMessages.menu_list_parents()
                "menu_create_parent" -> localizedMessages.menu_create_parent()
                "menu_finance_tuition" -> localizedMessages.menu_finance_tuition()
                "menu_finance_invoice" -> localizedMessages.menu_finance_invoice()
                "menu_finance_reports" -> localizedMessages.menu_finance_reports()

                // Validation keys - updated to snake_case
                "validation_first_name_required" -> localizedMessages.validation_first_name_required()
                "validation_first_name_size" -> localizedMessages.validation_first_name_size()
                "validation_first_name_pattern.en" -> localizedMessages.validation_first_name_pattern_en()
                "validation_first_name_pattern.fa" -> localizedMessages.validation_first_name_pattern_fa()
                "validation_last_name_required" -> localizedMessages.validation_last_name_required()
                "validation_last_name_size" -> localizedMessages.validation_last_name_size()
                "validation_last_name_pattern.en" -> localizedMessages.validation_last_name_pattern_en()
                "validation_last_name_pattern.fa" -> localizedMessages.validation_last_name_pattern_fa()
                "validation_email_required" -> localizedMessages.validation_email_required()
                "validation_email_format" -> localizedMessages.validation_email_format()
                "validation_email_exists" -> localizedMessages.validation_email_exists()
                "validation_phone_required" -> localizedMessages.validation_phone_required()
                "validation_phone_format" -> localizedMessages.validation_phone_format()
                "validation_password_required" -> localizedMessages.validation_password_required()
                "validation_password_size" -> localizedMessages.validation_password_size()
                "validation_password_pattern" -> localizedMessages.validation_password_pattern()
                "validation_role_required" -> localizedMessages.validation_role_required()
                "validation_userId_required" -> localizedMessages.validation_user_id_required()
                "validation_otp_required" -> localizedMessages.validation_otp_required()
                "validation_otp_size" -> localizedMessages.validation_otp_size()

                // User and Auth keys - updated to snake_case
                "user_email_exists" -> localizedMessages.user_email_exists()
                "user_phone_exists" -> localizedMessages.user_phone_exists()
                "user_weak_password" -> localizedMessages.user_weak_password()
                "user_not_found" -> localizedMessages.user_not_found()
                "user_inactive" -> localizedMessages.user_inactive()

                "auth_registration_success" -> localizedMessages.auth_registration_success()
                "auth_registration_failed" -> localizedMessages.auth_registration_failed()
                "auth_otp_sent" -> localizedMessages.auth_otp_sent()
                "auth_otp_resent" -> localizedMessages.auth_otp_resent()
                "auth_invalid_otp" -> localizedMessages.auth_invalid_otp()
                "auth_invalid_session" -> localizedMessages.auth_invalid_session()
                "auth_verification_failed" -> localizedMessages.auth_verification_failed()
                "auth_resend_failed" -> localizedMessages.auth_resend_failed()
                "auth_login_success" -> localizedMessages.auth_login_success()
                "auth_login_failed" -> localizedMessages.auth_login_failed()
                "auth_invalid_credentials" -> localizedMessages.auth_invalid_credentials()
                "auth_account_locked" -> localizedMessages.auth_account_locked()
                "auth_unauthorized" -> localizedMessages.auth_unauthorized()

                // Toast keys - updated to snake_case
                "toast_success" -> localizedMessages.toast_success()
                "toast_error" -> localizedMessages.toast_error()
                "toast_warning" -> localizedMessages.toast_warning()
                "toast_info" -> localizedMessages.toast_info()
                "toast_loading" -> localizedMessages.toast_loading()
                "toast_otp_verified" -> localizedMessages.toast_otp_verified()
                "toast_otp_invalid" -> localizedMessages.toast_otp_invalid()
                "toast_otp_expired" -> localizedMessages.toast_otp_expired()
                "toast_profile_updated" -> localizedMessages.toast_profile_updated()
                "toast_password_changed" -> localizedMessages.toast_password_changed()
                "toast_otp_sent" -> localizedMessages.toast_otp_sent()
                "toast_register_success" -> localizedMessages.toast_register_success()
                "toast_register_failed" -> localizedMessages.toast_register_failed()
                "toast_login_success" -> localizedMessages.toast_login_success()
                "toast_login_failed" -> localizedMessages.toast_login_failed()
                "toast_redirecting_dashboard" -> localizedMessages.toast_redirecting_dashboard()

                // Email keys - updated to snake_case and fixed mappings
                "email_verification_subject" -> localizedMessages.email_verification_subject()
                "email_verification_greeting" -> localizedMessages.email_verification_greeting(getStringArg(args, 0))
                "email_verification_message" -> localizedMessages.email_verification_message()
                "email_verification_button" -> localizedMessages.email_verification_button()
                "email_verification_expiry" -> localizedMessages.email_verification_expiry()

                "email_welcome_subject" -> localizedMessages.email_welcome_subject()
                "email_welcome_greeting" -> localizedMessages.email_welcome_greeting(getStringArg(args, 0))
                "email_welcome_message" -> localizedMessages.email_welcome_message()

                "email_otp_subject" -> localizedMessages.email_otp_subject()
                "email_otp_greeting" -> localizedMessages.email_otp_greeting(getStringArg(args, 0))
                "email_otp_message" -> localizedMessages.email_otp_message()
                "email_otp_expiry" -> localizedMessages.email_otp_expiry()
                "email_otp_ignore" -> localizedMessages.email_otp_ignore()

                "email_password_reset_subject" -> localizedMessages.email_password_reset_subject()
                "email_password_reset_greeting" -> localizedMessages.email_password_reset_greeting(
                    getStringArg(
                        args,
                        0
                    )
                )

                "email_password_reset_message" -> localizedMessages.email_password_reset_message()
                "email_password_reset_button" -> localizedMessages.email_password_reset_button()
                "email_password_reset_warning" -> localizedMessages.email_password_reset_warning()
                "email_password_reset_expiry" -> localizedMessages.email_password_reset_expiry()

                "email_footer" -> localizedMessages.email_footer()

                else -> {
                    logger.warn("Translation key not found: $key")
                    key // Fallback to key if not found
                }
            }
        } catch (e: Exception) {
            logger.error("Error in translation for key: $key, locale: $locale", e)
            key // Fallback to key on error
        }
    }


    private fun getStringArg(args: Array<out Any>, index: Int): String {
        return if (args.size > index) args[index].toString() else ""
    }

    private fun getIntArg(args: Array<out Any>, index: Int): Int {
        return if (args.size > index) {
            when (val arg = args[index]) {
                is Int -> arg
                is Number -> arg.toInt()
                is String -> arg.toIntOrNull() ?: 0
                else -> 0
            }
        } else {
            0
        }
    }

    // Simple method to get current language (you'll call this from your filter)
    fun getCurrentLanguage(): String {
        // This should be set by your LanguageFilter
        return "en" // Default, will be overridden by filter
    }
}