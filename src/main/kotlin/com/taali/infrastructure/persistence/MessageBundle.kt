package com.taali.infrastructure.persistence

import io.quarkus.qute.i18n.Message
import io.quarkus.qute.i18n.MessageBundle

@MessageBundle
interface AppMessages {

    // Menu Messages
    @Message("menu.dashboard")
    fun menuDashboard(): String

    @Message("menu.schoolManagement")
    fun menuSchoolManagement(): String

    @Message("menu.createSchool")
    fun menuCreateSchool(): String

    @Message("menu.listSchools")
    fun menuListSchools(): String

    @Message("menu.userManagement")
    fun menuUserManagement(): String

    @Message("menu.createUser")
    fun menuCreateUser(): String

    @Message("menu.listUsers")
    fun menuListUsers(): String

    @Message("menu.finance")
    fun menuFinance(): String

    @Message("menu.teacherManagement")
    fun menuTeacherManagement(): String

    @Message("menu.classManagement")
    fun menuClassManagement(): String

    @Message("menu.studentManagement")
    fun menuStudentManagement(): String

    @Message("menu.parentManagement")
    fun menuParentManagement(): String

    @Message("menu.myClasses")
    fun menuMyClasses(): String

    @Message("menu.myStudents")
    fun menuMyStudents(): String

    @Message("menu.attendance")
    fun menuAttendance(): String

    @Message("menu.assignments")
    fun menuAssignments(): String

    @Message("menu.myProfile")
    fun menuMyProfile(): String

    @Message("menu.myGrades")
    fun menuMyGrades(): String

    @Message("menu.myChildren")
    fun menuMyChildren(): String

    @Message("menu.childrenGrades")
    fun menuChildrenGrades(): String

    @Message("menu.childrenAttendance")
    fun menuChildrenAttendance(): String

    @Message("menu.payments")
    fun menuPayments(): String

    @Message("menu.foodMenu")
    fun menuFoodMenu(): String

    @Message("menu.orders")
    fun menuOrders(): String

    @Message("menu.inventory")
    fun menuInventory(): String

    @Message("menu.financialReports")
    fun menuFinancialReports(): String

    @Message("menu.paymentManagement")
    fun menuPaymentManagement(): String

    @Message("menu.invoices")
    fun menuInvoices(): String

    // Toast Messages
    @Message("toast.otpSent")
    fun toastOtpSent(): String

    @Message("toast.registerSuccess")
    fun toastRegisterSuccess(): String

    @Message("toast.registerFailed")
    fun toastRegisterFailed(): String

    @Message("toast.loginSuccess")
    fun toastLoginSuccess(): String

    @Message("toast.loginFailed")
    fun toastLoginFailed(): String

    @Message("toast.redirectingDashboard")
    fun toastRedirectingDashboard(): String

    // Your Existing Validation Messages
    @Message("validation.firstName.required")
    fun validationFirstNameRequired(): String

    @Message("validation.firstName.size")
    fun validationFirstNameSize(): String

    @Message("validation.firstName.pattern.en")
    fun validationFirstNamePatternEn(): String

    @Message("validation.firstName.pattern.fa")
    fun validationFirstNamePatternFa(): String

    @Message("validation.lastName.required")
    fun validationLastNameRequired(): String

    @Message("validation.lastName.size")
    fun validationLastNameSize(): String

    @Message("validation.lastName.pattern.en")
    fun validationLastNamePatternEn(): String

    @Message("validation.lastName.pattern.fa")
    fun validationLastNamePatternFa(): String

    @Message("validation.email.required")
    fun validationEmailRequired(): String

    @Message("validation.email.format")
    fun validationEmailFormat(): String

    @Message("validation.email.exists")
    fun validationEmailExists(): String

    @Message("validation.phone.required")
    fun validationPhoneRequired(): String

    @Message("validation.phone.format")
    fun validationPhoneFormat(): String

    @Message("validation.password.required")
    fun validationPasswordRequired(): String

    @Message("validation.password.size")
    fun validationPasswordSize(): String

    @Message("validation.password.pattern")
    fun validationPasswordPattern(): String

    @Message("validation.role.required")
    fun validationRoleRequired(): String

    @Message("validation.userId.required")
    fun validationUserIdRequired(): String

    @Message("validation.otp.required")
    fun validationOtpRequired(): String

    @Message("validation.otp.size")
    fun validationOtpSize(): String

    // Your Existing Response Messages
    @Message("response.registration.success")
    fun responseRegistrationSuccess(): String

    @Message("response.registration.failed")
    fun responseRegistrationFailed(): String

    @Message("response.email.already_exists")
    fun responseEmailAlreadyExists(): String

    // Your Existing Email Messages
    @Message("email.verification.subject")
    fun emailVerificationSubject(): String

    @Message("email.verification.greeting")
    fun emailVerificationGreeting(): String

    @Message("email.verification.message")
    fun emailVerificationMessage(): String

    @Message("email.verification.button")
    fun emailVerificationButton(): String

    @Message("email.verification.expiry")
    fun emailVerificationExpiry(): String

    @Message("email.welcome.subject")
    fun emailWelcomeSubject(): String

    @Message("email.welcome.greeting")
    fun emailWelcomeGreeting(): String

    @Message("email.welcome.message")
    fun emailWelcomeMessage(): String

    @Message("email.welcome.feature1")
    fun emailWelcomeFeature1(): String

    @Message("email.welcome.feature2")
    fun emailWelcomeFeature2(): String

    @Message("email.welcome.feature3")
    fun emailWelcomeFeature3(): String

    @Message("email.welcome.feature4")
    fun emailWelcomeFeature4(): String

    @Message("email.password_reset.subject")
    fun emailPasswordResetSubject(): String

    @Message("email.password_reset.greeting")
    fun emailPasswordResetGreeting(): String

    @Message("email.password_reset.message")
    fun emailPasswordResetMessage(): String

    @Message("email.password_reset.button")
    fun emailPasswordResetButton(): String

    @Message("email.password_reset.warning")
    fun emailPasswordResetWarning(): String

    @Message("email.password_reset.expiry")
    fun emailPasswordResetExpiry(): String

    @Message("email.footer")
    fun emailFooter(): String

    @Message("toast.success")
    fun toastSuccess(): String

    @Message("toast.error")
    fun toastError(): String

    @Message("toast.warning")
    fun toastWarning(): String

    @Message("toast.info")
    fun toastInfo(): String

    @Message("toast.loading")
    fun toastLoading(): String

    @Message("toast.otpVerified")
    fun toastOtpVerified(): String

    @Message("toast.otpInvalid")
    fun toastOtpInvalid(): String

    @Message("toast.otpExpired")
    fun toastOtpExpired(): String

    @Message("toast.profileUpdated")
    fun toastProfileUpdated(): String

    @Message("toast.passwordChanged")
    fun toastPasswordChanged(): String

    @Message("email.otp.subject")
    fun emailOtpSubject(): String

    @Message("email.otp.greeting")
    fun emailOtpGreeting(): String

    @Message("email.otp.message")
    fun emailOtpMessage(): String

    @Message("email.otp.expiry")
    fun emailOtpExpiry(): String

    @Message("email.otp.ignore")
    fun emailOtpIgnore(): String

    @Message("user.email_exists")
    fun userEmailExists(): String

    @Message("user.phone_exists")
    fun userPhoneExists(): String

    @Message("user.weak_password")
    fun userWeakPassword(): String

    @Message("user.not_found")
    fun userNotFound(): String

    @Message("user.inactive")
    fun userInactive(): String

    @Message("auth.registration_success")
    fun authRegistrationSuccess(): String

    @Message("auth.registration_failed")
    fun authRegistrationFailed(): String

    @Message("auth.otp_sent")
    fun authOtpSent(): String

    @Message("auth.otp_resent")
    fun authOtpResent(): String

    @Message("auth.invalid_otp")
    fun authInvalidOtp(): String

    @Message("auth.invalid_session")
    fun authInvalidSession(): String

    @Message("auth.verification_failed")
    fun authVerificationFailed(): String

    @Message("auth.resend_failed")
    fun authResendFailed(): String

    @Message("auth.login_success")
    fun authLoginSuccess(): String

    @Message("auth.login_failed")
    fun authLoginFailed(): String

    @Message("auth.invalid_credentials")
    fun authInvalidCredentials(): String

    @Message("auth.account_locked")
    fun authAccountLocked(): String

    @Message("auth.unauthorized")
    fun authUnauthorized(): String

}