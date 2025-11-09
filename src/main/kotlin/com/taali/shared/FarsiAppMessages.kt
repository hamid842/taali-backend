package com.taali.shared

import io.quarkus.qute.i18n.Localized
import io.quarkus.qute.i18n.Message
import io.quarkus.qute.i18n.MessageParam

@Localized("fa")
interface FarsiAppMessages : AppMessages {

    // Menu Messages
    @Message("داشبورد")
    override fun menu_dashboard(): String

    @Message("مدیریت مدرسه")
    override fun menu_school_management(): String

    @Message("مدیریت کاربران")
    override fun menu_user_management(): String

    @Message("امور مالی")
    override fun menu_finance(): String

    @Message("مدیریت معلمان")
    override fun menu_teacher_management(): String

    @Message("مدیریت کلاس‌ها")
    override fun menu_class_management(): String

    @Message("مدیریت دانش‌آموزان")
    override fun menu_student_management(): String

    @Message("مدیریت اولیا")
    override fun menu_parent_management(): String

    @Message("حضور و غیاب")
    override fun menu_attendance(): String

    @Message("تکالیف")
    override fun menu_assignments(): String

    @Message("پروفایل من")
    override fun menu_my_profile(): String

    @Message("ایجاد مدرسه")
    override fun menu_create_school(): String

    @Message("لیست مدارس")
    override fun menu_list_schools(): String

    @Message("ایجاد کاربر")
    override fun menu_create_user(): String

    @Message("لیست کاربران")
    override fun menu_list_users(): String

    @Message("پرداخت‌ها")
    override fun menu_payments(): String

    @Message("گزارش‌های مالی")
    override fun menu_financial_reports(): String

    @Message("مدیریت پرداخت")
    override fun menu_payment_management(): String

    @Message("فاکتورها")
    override fun menu_invoices(): String

    @Message("کلاس‌های من")
    override fun menu_my_classes(): String

    @Message("دانش‌آموزان من")
    override fun menu_my_students(): String

    @Message("نمرات من")
    override fun menu_my_grades(): String

    @Message("فرزندان من")
    override fun menu_my_children(): String

    @Message("نمرات فرزندان")
    override fun menu_children_grades(): String

    @Message("حضور و غیاب فرزندان")
    override fun menu_children_attendance(): String

    @Message("منوی غذا")
    override fun menu_food_menu(): String

    @Message("سفارشات")
    override fun menu_orders(): String

    @Message("انبار")
    override fun menu_inventory(): String

    // NEW MENU ITEMS - Added Farsi translations
    @Message("صورتحساب")
    override fun menu_billing(): String

    @Message("مدیریت شهریه")
    override fun menu_fee_management(): String

    @Message("مدیریت هزینه‌ها")
    override fun menu_expense_management(): String

    @Message("نمرات")
    override fun menu_grades(): String

    @Message("طرح‌های درسی")
    override fun menu_lesson_plans(): String

    @Message("تکالیف من")
    override fun menu_my_assignments(): String

    @Message("حضور و غیاب من")
    override fun menu_my_attendance(): String

    @Message("برنامه زمانی")
    override fun menu_schedule(): String

    @Message("اعلان‌ها")
    override fun menu_notifications(): String

    @Message("گزارش‌های حضور و غیاب")
    override fun menu_attendance_reports(): String

    @Message("تقویم آموزشی")
    override fun menu_academic_calendar(): String

    @Message("گزارش‌های فروش")
    override fun menu_sales_reports(): String

    @Message("پیگیری پرداخت")
    override fun menu_payment_tracking(): String

    // NEW: Added Farsi translations for nested menu structure
    @Message("لیست معلمان")
    override fun menu_list_teachers(): String

    @Message("ایجاد معلم")
    override fun menu_create_teacher(): String

    @Message("لیست کلاس‌ها")
    override fun menu_list_classes(): String

    @Message("ایجاد کلاس")
    override fun menu_create_class(): String

    @Message("لیست دانش‌آموزان")
    override fun menu_list_students(): String

    @Message("ایجاد دانش‌آموز")
    override fun menu_create_student(): String

    @Message("لیست اولیا")
    override fun menu_list_parents(): String

    @Message("ایجاد ولی")
    override fun menu_create_parent(): String

    @Message("شهریه")
    override fun menu_finance_tuition(): String

    @Message("فاکتور")
    override fun menu_finance_invoice(): String

    @Message("گزارشات")
    override fun menu_finance_reports(): String

    // Validation Messages
    @Message("نام الزامی است")
    override fun validation_first_name_required(): String

    @Message("نام باید بین ۲ تا ۵۰ کاراکتر باشد")
    override fun validation_first_name_size(): String

    @Message("نام فقط می‌تواند شامل حروف انگلیسی، فاصله، خط تیره و آپاستروف باشد")
    override fun validation_first_name_pattern_en(): String

    @Message("نام فقط می‌تواند شامل حروف فارسی، فاصله، خط تیره و آپاستروف باشد")
    override fun validation_first_name_pattern_fa(): String

    @Message("نام خانوادگی الزامی است")
    override fun validation_last_name_required(): String

    @Message("نام خانوادگی باید بین ۲ تا ۵۰ کاراکتر باشد")
    override fun validation_last_name_size(): String

    @Message("نام خانوادگی فقط می‌تواند شامل حروف انگلیسی، فاصله، خط تیره و آپاستروف باشد")
    override fun validation_last_name_pattern_en(): String

    @Message("نام خانوادگی فقط می‌تواند شامل حروف فارسی، فاصله، خط تیره و آپاستروف باشد")
    override fun validation_last_name_pattern_fa(): String

    @Message("ایمیل الزامی است")
    override fun validation_email_required(): String

    @Message("لطفا یک آدرس ایمیل معتبر وارد کنید")
    override fun validation_email_format(): String

    @Message("این ایمیل قبلا ثبت شده است")
    override fun validation_email_exists(): String

    @Message("شماره تلفن الزامی است")
    override fun validation_phone_required(): String

    @Message("لطفا یک شماره تلفن معتبر وارد کنید")
    override fun validation_phone_format(): String

    @Message("رمز عبور الزامی است")
    override fun validation_password_required(): String

    @Message("رمز عبور باید حداقل ۸ کاراکتر باشد")
    override fun validation_password_size(): String

    @Message("رمز عبور باید شامل حداقل یک حرف بزرگ، یک حرف کوچک و یک عدد باشد")
    override fun validation_password_pattern(): String

    @Message("نقش کاربری الزامی است")
    override fun validation_role_required(): String

    @Message("شناسه کاربر الزامی است")
    override fun validation_user_id_required(): String

    @Message("کد تأیید الزامی است")
    override fun validation_otp_required(): String

    @Message("کد تأیید باید ۶ رقم باشد")
    override fun validation_otp_size(): String

    // User & Auth
    @Message("کاربر با این ایمیل قبلا ثبت شده است")
    override fun user_email_exists(): String

    @Message("کاربر با این شماره تلفن قبلا ثبت شده است")
    override fun user_phone_exists(): String

    @Message("رمز عبور باید حداقل ۸ کاراکتر و شامل حروف و اعداد باشد")
    override fun user_weak_password(): String

    @Message("کاربر یافت نشد")
    override fun user_not_found(): String

    @Message("حساب کاربری غیرفعال است")
    override fun user_inactive(): String

    @Message("ثبت‌نام با موفقیت انجام شد")
    override fun auth_registration_success(): String

    @Message("ثبت‌نام انجام نشد")
    override fun auth_registration_failed(): String

    @Message("کد تأیید به ایمیل شما ارسال شد")
    override fun auth_otp_sent(): String

    @Message("کد تأیید جدید ارسال شد")
    override fun auth_otp_resent(): String

    @Message("کد تأیید نامعتبر است")
    override fun auth_invalid_otp(): String

    @Message("جلسه ثبت‌نام نامعتبر یا منقضی شده است")
    override fun auth_invalid_session(): String

    @Message("تأیید کد انجام نشد")
    override fun auth_verification_failed(): String

    @Message("ارسال مجدد کد انجام نشد")
    override fun auth_resend_failed(): String

    @Message("ورود موفقیت‌آمیز بود")
    override fun auth_login_success(): String

    @Message("ورود انجام نشد. لطفا اطلاعات خود را بررسی کنید")
    override fun auth_login_failed(): String

    @Message("ایمیل یا رمز عبور نامعتبر است")
    override fun auth_invalid_credentials(): String

    @Message("حساب کاربری موقتا قفل شده است")
    override fun auth_account_locked(): String

    @Message("دسترسی غیرمجاز")
    override fun auth_unauthorized(): String

    // Email
    @Message("تأیید ایمیل - اپلیکیشن طالی")
    override fun email_verification_subject(): String

    @Message("سلام {name}!")
    override fun email_verification_greeting(@MessageParam("name") name: String): String

    @Message("با تشکر از ثبت‌نام شما در اپلیکیشن طالی. برای تکمیل ثبت‌نام، لطفا آدرس ایمیل خود را با کلیک بر روی دکمه زیر تأیید کنید:")
    override fun email_verification_message(): String

    @Message("تأیید آدرس ایمیل")
    override fun email_verification_button(): String

    @Message("این لینک تأیید تا ۲۴ ساعت اعتبار دارد.")
    override fun email_verification_expiry(): String

    @Message("خوش آمدید به اپلیکیشن طالی!")
    override fun email_welcome_subject(): String

    @Message("سلام {name}!")
    override fun email_welcome_greeting(@MessageParam("name") name: String): String

    @Message("به اپلیکیشن طالی خوش آمدید! ما از همراهی شما خوشحالیم. حساب کاربری شما با موفقیت ایجاد و تأیید شد.")
    override fun email_welcome_message(): String

    @Message("کد تأیید شما - اپلیکیشن طالی")
    override fun email_otp_subject(): String

    @Message("سلام {name}!")
    override fun email_otp_greeting(@MessageParam("name") name: String): String

    @Message("کد تأیید شما:")
    override fun email_otp_message(): String

    @Message("این کد تا ۱۰ دقیقه اعتبار دارد")
    override fun email_otp_expiry(): String

    @Message("اگر این کد را درخواست نکرده‌اید، لطفا این ایمیل را نادیده بگیرید")
    override fun email_otp_ignore(): String

    @Message("بازیابی رمز عبور - اپلیکیشن طالی")
    override fun email_password_reset_subject(): String

    @Message("سلام {name}!")
    override fun email_password_reset_greeting(@MessageParam("name") name: String): String

    @Message("درخواست بازیابی رمز عبور برای حساب کاربری شما در اپلیکیشن طالی دریافت شد. برای بازیابی رمز عبور بر روی دکمه زیر کلیک کنید:")
    override fun email_password_reset_message(): String

    @Message("بازیابی رمز عبور")
    override fun email_password_reset_button(): String

    @Message("اگر درخواست بازیابی رمز عبور نداده‌اید، لطفا این ایمیل را نادیده بگیرید. حساب کاربری شما در امان است.")
    override fun email_password_reset_warning(): String

    @Message("این لینک بازیابی تا ۱ ساعت اعتبار دارد.")
    override fun email_password_reset_expiry(): String

    @Message("© 2024 اپلیکیشن طالی. تمام حقوق محفوظ است.")
    override fun email_footer(): String

    // Toast / Notifications
    @Message("کد تأیید ارسال شد")
    override fun toast_otp_sent(): String

    @Message("حساب کاربری با موفقیت ایجاد شد!")
    override fun toast_register_success(): String

    @Message("ثبت‌نام انجام نشد. لطفا مجددا تلاش کنید.")
    override fun toast_register_failed(): String

    @Message("ورود موفقیت‌آمیز بود!")
    override fun toast_login_success(): String

    @Message("ورود انجام نشد. لطفا اطلاعات خود را بررسی کنید.")
    override fun toast_login_failed(): String

    @Message("در حال انتقال به داشبورد...")
    override fun toast_redirecting_dashboard(): String

    @Message("عملیات با موفقیت انجام شد")
    override fun toast_success(): String

    @Message("خطایی رخ داد")
    override fun toast_error(): String

    @Message("هشدار")
    override fun toast_warning(): String

    @Message("اطلاعات")
    override fun toast_info(): String

    @Message("در حال بارگذاری...")
    override fun toast_loading(): String

    @Message("ایمیل با موفقیت تأیید شد")
    override fun toast_otp_verified(): String

    @Message("کد تأیید نامعتبر است")
    override fun toast_otp_invalid(): String

    @Message("کد تأیید منقضی شده است")
    override fun toast_otp_expired(): String

    @Message("پروفایل با موفقیت به‌روزرسانی شد")
    override fun toast_profile_updated(): String

    @Message("رمز عبور با موفقیت تغییر کرد")
    override fun toast_password_changed(): String
}