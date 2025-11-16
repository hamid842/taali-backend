package com.taali.shared

import io.quarkus.qute.i18n.Message
import io.quarkus.qute.i18n.MessageBundle
import io.quarkus.qute.i18n.MessageParam

@MessageBundle
interface AppMessages {

    // Menu Messages
    @Message("Dashboard")
    fun menu_dashboard(): String

    @Message("School Management")
    fun menu_school_management(): String

    @Message("User Management")
    fun menu_user_management(): String

    @Message("Finance")
    fun menu_finance(): String

    @Message("Teacher Management")
    fun menu_teacher_management(): String

    @Message("Class Management")
    fun menu_class_management(): String

    @Message("Student Management")
    fun menu_student_management(): String

    @Message("Parent Management")
    fun menu_parent_management(): String

    @Message("Attendance")
    fun menu_attendance(): String

    @Message("Assignments")
    fun menu_assignments(): String

    @Message("My Profile")
    fun menu_my_profile(): String

    @Message("Create School")
    fun menu_create_school(): String

    @Message("List Schools")
    fun menu_list_schools(): String

    @Message("Create User")
    fun menu_create_user(): String

    @Message("List Users")
    fun menu_list_users(): String

    @Message("Payments")
    fun menu_payments(): String

    @Message("Financial Reports")
    fun menu_financial_reports(): String

    @Message("Payment Management")
    fun menu_payment_management(): String

    @Message("Invoices")
    fun menu_invoices(): String

    @Message("My Classes")
    fun menu_my_classes(): String

    @Message("My Students")
    fun menu_my_students(): String

    @Message("My Grades")
    fun menu_my_grades(): String

    @Message("My Children")
    fun menu_my_children(): String

    @Message("Children Grades")
    fun menu_children_grades(): String

    @Message("Children Attendance")
    fun menu_children_attendance(): String

    @Message("Food Menu")
    fun menu_food_menu(): String

    @Message("Orders")
    fun menu_orders(): String

    @Message("Inventory")
    fun menu_inventory(): String

    // NEW MENU ITEMS - Added based on the updated menu structure
    @Message("Billing")
    fun menu_billing(): String

    @Message("Fee Management")
    fun menu_fee_management(): String

    @Message("Expense Management")
    fun menu_expense_management(): String

    @Message("Grades")
    fun menu_grades(): String

    @Message("Lesson Plans")
    fun menu_lesson_plans(): String

    @Message("My Assignments")
    fun menu_my_assignments(): String

    @Message("My Attendance")
    fun menu_my_attendance(): String

    @Message("Schedule")
    fun menu_schedule(): String

    @Message("Notifications")
    fun menu_notifications(): String

    @Message("Attendance Reports")
    fun menu_attendance_reports(): String

    @Message("Academic Calendar")
    fun menu_academic_calendar(): String

    @Message("Sales Reports")
    fun menu_sales_reports(): String

    @Message("Payment Tracking")
    fun menu_payment_tracking(): String

    // NEW: Added for the nested menu structure
    @Message("List Teachers")
    fun menu_list_teachers(): String

    @Message("Create Teacher")
    fun menu_create_teacher(): String

    @Message("List Classes")
    fun menu_list_classes(): String

    @Message("Create Class")
    fun menu_create_class(): String

    @Message("List Students")
    fun menu_list_students(): String

    @Message("Create Student")
    fun menu_create_student(): String

    @Message("List Parents")
    fun menu_list_parents(): String

    @Message("Create Parent")
    fun menu_create_parent(): String

    @Message("Tuition")
    fun menu_finance_tuition(): String

    @Message("Invoice")
    fun menu_finance_invoice(): String

    @Message("Reports")
    fun menu_finance_reports(): String

    @Message("Lesson Management")
    fun menu_lessons_management(): String

    @Message("Create Lesson")
    fun menu_create_lesson(): String

    @Message("Lessons")
    fun menu_list_lessons(): String

    @Message("School Settings")
    fun menu_school_settings(): String

    @Message("Class Period Timing")
    fun menu_timestamp(): String

    // Validation Messages
    @Message("First name is required")
    fun validation_first_name_required(): String

    @Message("First name must be between 2 and 50 characters")
    fun validation_first_name_size(): String

    @Message("First name can only contain English letters, spaces, hyphens, and apostrophes")
    fun validation_first_name_pattern_en(): String

    @Message("First name can only contain Persian letters, spaces, hyphens, and apostrophes")
    fun validation_first_name_pattern_fa(): String

    @Message("Last name is required")
    fun validation_last_name_required(): String

    @Message("Last name must be between 2 and 50 characters")
    fun validation_last_name_size(): String

    @Message("Last name can only contain English letters, spaces, hyphens, and apostrophes")
    fun validation_last_name_pattern_en(): String

    @Message("Last name can only contain Persian letters, spaces, hyphens, and apostrophes")
    fun validation_last_name_pattern_fa(): String

    @Message("Email is required")
    fun validation_email_required(): String

    @Message("Please provide a valid email address")
    fun validation_email_format(): String

    @Message("Email is already registered")
    fun validation_email_exists(): String

    @Message("Phone number is required")
    fun validation_phone_required(): String

    @Message("Please provide a valid phone number")
    fun validation_phone_format(): String

    @Message("Password is required")
    fun validation_password_required(): String

    @Message("Password must be at least 8 characters")
    fun validation_password_size(): String

    @Message("Password must contain at least one uppercase letter, one lowercase letter, and one number")
    fun validation_password_pattern(): String

    @Message("Role is required")
    fun validation_role_required(): String

    @Message("User ID is required")
    fun validation_user_id_required(): String

    @Message("OTP code is required")
    fun validation_otp_required(): String

    @Message("OTP code must be 6 digits")
    fun validation_otp_size(): String

    // User & Auth
    @Message("User with this email already exists")
    fun user_email_exists(): String

    @Message("User with this phone number already exists")
    fun user_phone_exists(): String

    @Message("Password must be at least 8 characters with letters and numbers")
    fun user_weak_password(): String

    @Message("User not found")
    fun user_not_found(): String

    @Message("User account is inactive")
    fun user_inactive(): String

    @Message("Registration completed successfully")
    fun auth_registration_success(): String

    @Message("Registration failed")
    fun auth_registration_failed(): String

    @Message("Verification code sent to your email")
    fun auth_otp_sent(): String

    @Message("New verification code sent")
    fun auth_otp_resent(): String

    @Message("Invalid OTP code")
    fun auth_invalid_otp(): String

    @Message("Invalid or expired registration session")
    fun auth_invalid_session(): String

    @Message("OTP verification failed")
    fun auth_verification_failed(): String

    @Message("Failed to resend OTP")
    fun auth_resend_failed(): String

    @Message("Login successful")
    fun auth_login_success(): String

    @Message("Login failed. Please check your credentials")
    fun auth_login_failed(): String

    @Message("Invalid email or password")
    fun auth_invalid_credentials(): String

    @Message("Account is temporarily locked")
    fun auth_account_locked(): String

    @Message("Unauthorized access")
    fun auth_unauthorized(): String

    // Email
    @Message("Verify Your Email - Taali App")
    fun email_verification_subject(): String

    @Message("Hello {name}!")
    fun email_verification_greeting(@MessageParam("name") name: String): String

    @Message("Thank you for registering with Taali App. To complete your registration, please verify your email address by clicking the button below:")
    fun email_verification_message(): String

    @Message("Verify Email Address")
    fun email_verification_button(): String

    @Message("This verification link will expire in 24 hours.")
    fun email_verification_expiry(): String

    @Message("Welcome to Taali App!")
    fun email_welcome_subject(): String

    @Message("Hello {name}!")
    fun email_welcome_greeting(@MessageParam("name") name: String): String

    @Message("Welcome to Taali App! We're excited to have you on board. Your account has been successfully created and verified.")
    fun email_welcome_message(): String

    @Message("Your Verification Code - Taali App")
    fun email_otp_subject(): String

    @Message("Hello {name}!")
    fun email_otp_greeting(@MessageParam("name") name: String): String

    @Message("Your verification code is:")
    fun email_otp_message(): String

    @Message("This code will expire in 10 minutes")
    fun email_otp_expiry(): String

    @Message("If you didn't request this code, please ignore this email")
    fun email_otp_ignore(): String

    @Message("Reset Your Password - Taali App")
    fun email_password_reset_subject(): String

    @Message("Hello {name}!")
    fun email_password_reset_greeting(@MessageParam("name") name: String): String

    @Message("We received a request to reset your password for your Taali App account. Click the button below to reset your password:")
    fun email_password_reset_message(): String

    @Message("Reset Password")
    fun email_password_reset_button(): String

    @Message("If you didn't request a password reset, please ignore this email. Your account remains secure.")
    fun email_password_reset_warning(): String

    @Message("This password reset link will expire in 1 hour.")
    fun email_password_reset_expiry(): String

    @Message("&copy; 2024 Taali App. All rights reserved.")
    fun email_footer(): String

    // Toast / Notifications
    @Message("Verification code sent")
    fun toast_otp_sent(): String

    @Message("Account created successfully!")
    fun toast_register_success(): String

    @Message("Registration failed. Please try again.")
    fun toast_register_failed(): String

    @Message("Login successful!")
    fun toast_login_success(): String

    @Message("Login failed. Please check your credentials.")
    fun toast_login_failed(): String

    @Message("Redirecting to dashboard...")
    fun toast_redirecting_dashboard(): String

    @Message("Operation completed successfully")
    fun toast_success(): String

    @Message("An error occurred")
    fun toast_error(): String

    @Message("Warning")
    fun toast_warning(): String

    @Message("Information")
    fun toast_info(): String

    @Message("Loading...")
    fun toast_loading(): String

    @Message("Email verified successfully")
    fun toast_otp_verified(): String

    @Message("Invalid verification code")
    fun toast_otp_invalid(): String

    @Message("Verification code has expired")
    fun toast_otp_expired(): String

    @Message("Profile updated successfully")
    fun toast_profile_updated(): String

    @Message("Password changed successfully")
    fun toast_password_changed(): String
}