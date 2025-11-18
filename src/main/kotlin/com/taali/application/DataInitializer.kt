package com.taali.application

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.jboss.logging.Logger
import io.quarkus.runtime.configuration.ProfileManager

@ApplicationScoped
class DataInitializer {

    private val logger: Logger = Logger.getLogger(DataInitializer::class.java)

    @Inject
    lateinit var entityManager: EntityManager

    @Transactional
    fun onStart(@Observes event: StartupEvent) {
        val activeProfile = ProfileManager.getActiveProfile()
        logger.info("Active profile: $activeProfile")

        if (activeProfile == "dev" || activeProfile == "development") {
            logger.info("Running development data initialization...")
            initializeLessons()
        } else {
            logger.info("Skipping development data initialization for profile: $activeProfile")
        }
    }

    private fun initializeLessons() {
        val lessonCount = entityManager
            .createQuery("SELECT COUNT(l) FROM Lesson l", Long::class.java)
            .singleResult

        if (lessonCount > 0) {
            logger.info("Lessons already exist in database. Skipping initialization.")
            return
        }

        logger.info("Inserting default lessons for Iranian education system...")

        val insertSql = """
            INSERT INTO lessons (id, name, name_en, grade_level, color, created_at, updated_at) VALUES
            -- PRE_PRIMARY (پیش دبستانی)
            (nextval('lessons_seq'), 'ریاضی', 'Mathematics', 'PRE_PRIMARY', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم', 'Science', 'PRE_PRIMARY', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر', 'Art', 'PRE_PRIMARY', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش', 'Physical Education', 'PRE_PRIMARY', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ادبیات', 'Literature', 'PRE_PRIMARY', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- FIRST PERIOD PRIMARY (دوره اول ابتدایی)
            -- Grade 1 (اول ابتدایی)
            (nextval('lessons_seq'), 'ریاضی اول', 'First Grade Mathematics', 'PRIMARY_1', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم اول', 'First Grade Science', 'PRIMARY_1', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی اول', 'First Grade Persian', 'PRIMARY_1', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی اول', 'First Grade Religion', 'PRIMARY_1', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر اول', 'First Grade Art', 'PRIMARY_1', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 2 (دوم ابتدایی)
            (nextval('lessons_seq'), 'ریاضی دوم', 'Second Grade Mathematics', 'PRIMARY_2', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم دوم', 'Second Grade Science', 'PRIMARY_2', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی دوم', 'Second Grade Persian', 'PRIMARY_2', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی دوم', 'Second Grade Religion', 'PRIMARY_2', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر دوم', 'Second Grade Art', 'PRIMARY_2', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 3 (سوم ابتدایی)
            (nextval('lessons_seq'), 'ریاضی سوم', 'Third Grade Mathematics', 'PRIMARY_3', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم سوم', 'Third Grade Science', 'PRIMARY_3', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی سوم', 'Third Grade Persian', 'PRIMARY_3', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی سوم', 'Third Grade Religion', 'PRIMARY_3', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر سوم', 'Third Grade Art', 'PRIMARY_3', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی سوم', 'Third Grade Social Studies', 'PRIMARY_3', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- SECOND PERIOD PRIMARY (دوره دوم ابتدایی)
            -- Grade 4 (چهارم ابتدایی)
            (nextval('lessons_seq'), 'ریاضی چهارم', 'Fourth Grade Mathematics', 'PRIMARY_4', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم چهارم', 'Fourth Grade Science', 'PRIMARY_4', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی چهارم', 'Fourth Grade Persian', 'PRIMARY_4', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی چهارم', 'Fourth Grade Religion', 'PRIMARY_4', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر چهارم', 'Fourth Grade Art', 'PRIMARY_4', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی چهارم', 'Fourth Grade Social Studies', 'PRIMARY_4', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 5 (پنجم ابتدایی)
            (nextval('lessons_seq'), 'ریاضی پنجم', 'Fifth Grade Mathematics', 'PRIMARY_5', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم پنجم', 'Fifth Grade Science', 'PRIMARY_5', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی پنجم', 'Fifth Grade Persian', 'PRIMARY_5', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی پنجم', 'Fifth Grade Religion', 'PRIMARY_5', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر پنجم', 'Fifth Grade Art', 'PRIMARY_5', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی پنجم', 'Fifth Grade Social Studies', 'PRIMARY_5', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 6 (ششم ابتدایی)
            (nextval('lessons_seq'), 'ریاضی ششم', 'Sixth Grade Mathematics', 'PRIMARY_6', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم ششم', 'Sixth Grade Science', 'PRIMARY_6', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی ششم', 'Sixth Grade Persian', 'PRIMARY_6', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هدیه های آسمانی ششم', 'Sixth Grade Religion', 'PRIMARY_6', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر ششم', 'Sixth Grade Art', 'PRIMARY_6', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی ششم', 'Sixth Grade Social Studies', 'PRIMARY_6', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- LOWER SECONDARY (دوره اول متوسطه)
            -- Grade 7 (هفتم)
            (nextval('lessons_seq'), 'ریاضی هفتم', 'Seventh Grade Mathematics', 'LOWER_SECONDARY_7', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم هفتم', 'Seventh Grade Science', 'LOWER_SECONDARY_7', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی هفتم', 'Seventh Grade Persian', 'LOWER_SECONDARY_7', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی هفتم', 'Seventh Grade Arabic', 'LOWER_SECONDARY_7', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی هفتم', 'Seventh Grade English', 'LOWER_SECONDARY_7', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی هفتم', 'Seventh Grade Social Studies', 'LOWER_SECONDARY_7', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'پیام های آسمانی هفتم', 'Seventh Grade Religion', 'LOWER_SECONDARY_7', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر هفتم', 'Seventh Grade Art', 'LOWER_SECONDARY_7', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش هفتم', 'Seventh Grade Physical Education', 'LOWER_SECONDARY_7', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 8 (هشتم)
            (nextval('lessons_seq'), 'ریاضی هشتم', 'Eighth Grade Mathematics', 'LOWER_SECONDARY_8', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم هشتم', 'Eighth Grade Science', 'LOWER_SECONDARY_8', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی هشتم', 'Eighth Grade Persian', 'LOWER_SECONDARY_8', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی هشتم', 'Eighth Grade Arabic', 'LOWER_SECONDARY_8', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی هشتم', 'Eighth Grade English', 'LOWER_SECONDARY_8', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی هشتم', 'Eighth Grade Social Studies', 'LOWER_SECONDARY_8', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'پیام های آسمانی هشتم', 'Eighth Grade Religion', 'LOWER_SECONDARY_8', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر هشتم', 'Eighth Grade Art', 'LOWER_SECONDARY_8', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش هشتم', 'Eighth Grade Physical Education', 'LOWER_SECONDARY_8', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 9 (نهم)
            (nextval('lessons_seq'), 'ریاضی نهم', 'Ninth Grade Mathematics', 'LOWER_SECONDARY_9', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'علوم نهم', 'Ninth Grade Science', 'LOWER_SECONDARY_9', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فارسی نهم', 'Ninth Grade Persian', 'LOWER_SECONDARY_9', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی نهم', 'Ninth Grade Arabic', 'LOWER_SECONDARY_9', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی نهم', 'Ninth Grade English', 'LOWER_SECONDARY_9', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'مطالعات اجتماعی نهم', 'Ninth Grade Social Studies', 'LOWER_SECONDARY_9', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'پیام های آسمانی نهم', 'Ninth Grade Religion', 'LOWER_SECONDARY_9', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر نهم', 'Ninth Grade Art', 'LOWER_SECONDARY_9', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش نهم', 'Ninth Grade Physical Education', 'LOWER_SECONDARY_9', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- UPPER SECONDARY (دوره دوم متوسطه)
            -- Grade 10 (دهم)
            (nextval('lessons_seq'), 'ریاضی دهم', 'Tenth Grade Mathematics', 'UPPER_SECONDARY_10', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فیزیک دهم', 'Tenth Grade Physics', 'UPPER_SECONDARY_10', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'شیمی دهم', 'Tenth Grade Chemistry', 'UPPER_SECONDARY_10', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ادبیات فارسی دهم', 'Tenth Grade Persian Literature', 'UPPER_SECONDARY_10', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی دهم', 'Tenth Grade Arabic', 'UPPER_SECONDARY_10', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی دهم', 'Tenth Grade English', 'UPPER_SECONDARY_10', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'دین و زندگی دهم', 'Tenth Grade Religion', 'UPPER_SECONDARY_10', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر دهم', 'Tenth Grade Art', 'UPPER_SECONDARY_10', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش دهم', 'Tenth Grade Physical Education', 'UPPER_SECONDARY_10', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 11 (یازدهم)
            (nextval('lessons_seq'), 'ریاضی یازدهم', 'Eleventh Grade Mathematics', 'UPPER_SECONDARY_11', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فیزیک یازدهم', 'Eleventh Grade Physics', 'UPPER_SECONDARY_11', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'شیمی یازدهم', 'Eleventh Grade Chemistry', 'UPPER_SECONDARY_11', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ادبیات فارسی یازدهم', 'Eleventh Grade Persian Literature', 'UPPER_SECONDARY_11', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی یازدهم', 'Eleventh Grade Arabic', 'UPPER_SECONDARY_11', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی یازدهم', 'Eleventh Grade English', 'UPPER_SECONDARY_11', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'دین و زندگی یازدهم', 'Eleventh Grade Religion', 'UPPER_SECONDARY_11', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر یازدهم', 'Eleventh Grade Art', 'UPPER_SECONDARY_11', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش یازدهم', 'Eleventh Grade Physical Education', 'UPPER_SECONDARY_11', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

            -- Grade 12 (دوازدهم)
            (nextval('lessons_seq'), 'ریاضی دوازدهم', 'Twelfth Grade Mathematics', 'UPPER_SECONDARY_12', '#3b82f6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'فیزیک دوازدهم', 'Twelfth Grade Physics', 'UPPER_SECONDARY_12', '#10b981', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'شیمی دوازدهم', 'Twelfth Grade Chemistry', 'UPPER_SECONDARY_12', '#06b6d4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ادبیات فارسی دوازدهم', 'Twelfth Grade Persian Literature', 'UPPER_SECONDARY_12', '#ef4444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'عربی دوازدهم', 'Twelfth Grade Arabic', 'UPPER_SECONDARY_12', '#84cc16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'انگلیسی دوازدهم', 'Twelfth Grade English', 'UPPER_SECONDARY_12', '#f59e0b', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'دین و زندگی دوازدهم', 'Twelfth Grade Religion', 'UPPER_SECONDARY_12', '#8b5cf6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'هنر دوازدهم', 'Twelfth Grade Art', 'UPPER_SECONDARY_12', '#ec4899', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            (nextval('lessons_seq'), 'ورزش دوازدهم', 'Twelfth Grade Physical Education', 'UPPER_SECONDARY_12', '#22c55e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

        """.trimIndent()

        try {
            entityManager.createNativeQuery(insertSql).executeUpdate()
            logger.info("Successfully inserted Iranian education system lessons")
        } catch (e: Exception) {
            logger.error("Failed to insert lessons: ${e.message}", e)
        }
    }
}