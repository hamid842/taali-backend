package com.taali.application.service.menu

import com.taali.api.dto.menu.MenuItemDto
import com.taali.domain.enum.UserRole
import com.taali.domain.model.menu.MenuItem
import com.taali.domain.repository.menu.MenuItemRepository
import com.taali.shared.RequestContext
import com.taali.shared.TranslationService
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class MenuService @Inject constructor(
    val menuItemRepository: MenuItemRepository,
    private val translationService: TranslationService,
    private val requestContext: RequestContext
) {

    @PostConstruct
    @Transactional
    fun initializeDefaultMenu() {
//        if (menuItemRepository.count() == 0L) {
            createDefaultMenu()
//        }
    }

    fun getMenuForRole(role: UserRole): List<MenuItemDto> {
        val menuItems = menuItemRepository.findByRole(role.name)
        return buildMenuTree(menuItems, requestContext.language)
    }

    fun getPermissionsForRole(role: UserRole): List<String> =
        when (role) {
            UserRole.OWNER -> listOf(
                "school:create", "school:read", "school:update", "school:delete",
                "user:create", "user:read", "user:update", "user:delete",
                "finance:read", "finance:tuition:read", "finance:invoice:read", "finance:reports:read",
                "reports:generate", "system:manage", "billing:manage"
            )

            UserRole.ADMIN -> listOf(
                "user:create", "user:read", "user:update", "user:delete",
                "teacher:read", "teacher:create", "teacher:update", "teacher:delete",
                "class:read", "class:create", "class:update", "class:delete",
                "student:read", "student:create", "student:update", "student:delete",
                "parent:read", "parent:create", "parent:update", "parent:delete",
                "finance:read", "finance:tuition:read", "finance:invoice:read", "finance:reports:read",
                "reports:generate",
                "lesson:create", "lesson:read", "lesson:update", "lesson:delete",
                "school:settings:read", "school:settings:update","school:settings:create"
            )

            UserRole.SUPERVISOR -> listOf(
                "teacher:read", "teacher:create", "teacher:update", "teacher:delete",
                "class:read", "class:create", "class:update", "class:delete",
                "student:read", "student:create", "student:update", "student:delete",
                "parent:read", "parent:create", "parent:update", "parent:delete",
                "finance:read", "finance:tuition:read", "finance:invoice:read", "finance:reports:read",
                "reports:generate",
                "attendance:view", "calendar:view", "grades:view"
            )

            UserRole.TEACHER -> listOf(
                "class:read", "student:read", "attendance:manage",
                "grades:manage", "assignments:create", "assignments:read",
                "assignments:update", "student:progress:view", "lesson_plans:manage"
            )

            UserRole.STUDENT -> listOf(
                "profile:read", "grades:read", "attendance:read",
                "assignments:read", "classes:read", "schedule:view"
            )

            UserRole.PARENT -> listOf(
                "children:read", "children_grades:read",
                "children_attendance:read", "payments:view",
                "children:schedule:view", "notifications:receive"
            )

            UserRole.CANTEEN_OPERATOR -> listOf(
                "menu:create", "menu:read", "menu:update", "menu:delete",
                "orders:manage", "inventory:manage", "reports:generate"
            )

            UserRole.FINANCE_TEAM -> listOf(
                "finance:read", "payments:manage", "reports:generate",
                "invoices:create", "invoices:read", "invoices:update",
                "financial_reports:view", "expenses:manage"
            )
        }

    fun getAllMenuItems(): List<MenuItemDto> =
        buildMenuTree(menuItemRepository.listAll(), "en")

    fun getMenuItemsByPermission(permission: String): List<MenuItemDto> =
        buildMenuTree(menuItemRepository.findByRequiredPermission(permission), "en")

    fun getMenuHierarchy(): List<MenuItemDto> =
        buildMenuTree(menuItemRepository.findByParentIsNull(), "en")

    @Transactional
    fun createMenuItem(
        titleKey: String,
        icon: String? = null,
        route: String? = null,
        path: String? = null,
        orderIndex: Int = 0,
        roles: Set<UserRole>,
        parent: MenuItem? = null,
        requiredPermission: String? = null
    ): MenuItem {
        val menuItem = MenuItem().apply {
            this.titleKey = titleKey
            this.icon = icon
            this.route = route
            this.path = path
            this.orderIndex = orderIndex
            this.parent = parent
            this.requiredPermission = requiredPermission
            this.allowedRoles.addAll(roles.map { it.name })
        }
        menuItemRepository.persist(menuItem)
        return menuItem
    }

    @Transactional
    fun updateMenuItemOrder(menuItemId: Long, newOrder: Int): MenuItem? {
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.orderIndex = newOrder
        return menuItem
    }

    @Transactional
    fun addRoleToMenuItem(menuItemId: Long, role: UserRole): MenuItem? {
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.allowedRoles?.add(role.name)
        return menuItem
    }

    @Transactional
    fun removeRoleFromMenuItem(menuItemId: Long, role: UserRole): MenuItem? {
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.allowedRoles?.remove(role.name)
        return menuItem
    }

    fun hasAccessToMenuItem(menuItem: MenuItem, userRole: UserRole): Boolean =
        menuItem.allowedRoles.contains(userRole.name)

    fun getAccessibleRoutes(userRole: UserRole): List<String> =
        menuItemRepository.findByRole(userRole.name)
            .mapNotNull { it.route }

    private fun buildMenuTree(menuItems: List<MenuItem>, locale: String): List<MenuItemDto> {
        val rootItems = menuItems.filter { it.parent == null }.sortedBy { it.orderIndex }
        return rootItems.map { mapToDto(it, menuItems, locale) }
    }

    private fun mapToDto(menuItem: MenuItem, allItems: List<MenuItem>, locale: String): MenuItemDto {
        val children = allItems
            .filter { it.parent?.id == menuItem.id }
            .sortedBy { it.orderIndex }
            .map { mapToDto(it, allItems, locale) }

        val translatedTitle = translationService.translate(menuItem.titleKey, locale)

        return MenuItemDto(
            id = menuItem.id,
            titleKey = menuItem.titleKey,
            title = translatedTitle,
            icon = menuItem.icon,
            route = menuItem.route,
            path = menuItem.path,
            orderIndex = menuItem.orderIndex,
            children = children,
            requiredPermission = menuItem.requiredPermission,
            allowedRoles = menuItem.allowedRoles,
            parentId = menuItem.parent?.id,
            isRootItem = menuItem.parent == null,
            hasChildren = children.isNotEmpty()
        )
    }

    @Transactional
    fun createDefaultMenu() {
        menuItemRepository.deleteAll()

        // ==================== OWNER MENU ====================
        val ownerDashboard = createMenuItem("menu_dashboard", "layout-dashboard", "/owner/dashboard", "/owner/dashboard", 0, setOf(UserRole.OWNER))

        val ownerSchoolManagement = createMenuItem("menu_school_management", "school", null, null, 1, setOf(UserRole.OWNER))
        createMenuItem("menu_create_school", "plus", "/owner/schools/create", "/owner/schools/create", 0, setOf(UserRole.OWNER), ownerSchoolManagement, "school:create")
        createMenuItem("menu_list_schools", "list", "/owner/schools", "/owner/schools", 1, setOf(UserRole.OWNER), ownerSchoolManagement, "school:read")

        val ownerUserManagement = createMenuItem("menu_user_management", "users-round", null, null, 2, setOf(UserRole.OWNER))
        createMenuItem("menu_create_user", "user-plus", "/owner/users/create", "/owner/users/create", 0, setOf(UserRole.OWNER), ownerUserManagement, "user:create")
        createMenuItem("menu_list_users", "users", "/owner/users", "/owner/users", 1, setOf(UserRole.OWNER), ownerUserManagement, "user:read")

        val ownerFinance = createMenuItem("menu_finance", "dollar-sign", null, null, 3, setOf(UserRole.OWNER))
        createMenuItem("menu_billing", "credit-card", "/owner/billing", "/owner/billing", 0, setOf(UserRole.OWNER), ownerFinance, "billing:manage")
        createMenuItem("menu_financial_reports", "chart-bar", "/owner/financial-reports", "/owner/financial-reports", 1, setOf(UserRole.OWNER), ownerFinance, "reports:generate")

        // ==================== ADMIN MENU ====================
        val adminDashboard = createMenuItem("menu_dashboard", "layout-dashboard", "/admin/dashboard", "/admin/dashboard", 0, setOf(UserRole.ADMIN))

        val adminUserManagement = createMenuItem("menu_user_management", "users-round", null, null, 1, setOf(UserRole.ADMIN))
        createMenuItem("menu_create_user", "user-plus", "/admin/users/create", "/admin/users/create", 0, setOf(UserRole.ADMIN), adminUserManagement, "user:create")
        createMenuItem("menu_list_users", "users", "/admin/users", "/admin/users", 1, setOf(UserRole.ADMIN), adminUserManagement, "user:read")

        // Teacher Management with children
        val adminTeacherManagement = createMenuItem("menu_teacher_management", "contact", null, null, 2, setOf(UserRole.ADMIN))
        createMenuItem("menu_list_teachers", "list", "/admin/teachers", "/admin/teachers", 0, setOf(UserRole.ADMIN), adminTeacherManagement, "teacher:read")
        createMenuItem("menu_create_teacher", "user-round-plus", "/admin/teachers/create", "/admin/teachers/create", 1, setOf(UserRole.ADMIN), adminTeacherManagement, "teacher:create")

        // Class Management with children
        val adminClassManagement = createMenuItem("menu_class_management", "grid-2x2", null, null, 3, setOf(UserRole.ADMIN))
        createMenuItem("menu_list_classes", "list", "/admin/classes", "/admin/classes", 0, setOf(UserRole.ADMIN), adminClassManagement, "class:read")
        createMenuItem("menu_create_class", "grid-2x2-plus", "/admin/classes/create", "/admin/classes/create", 1, setOf(UserRole.ADMIN), adminClassManagement, "class:create")

        // Student Management with children
        val adminStudentManagement = createMenuItem("menu_student_management", "graduation-cap", null, null, 4, setOf(UserRole.ADMIN))
        createMenuItem("menu_list_students", "list", "/admin/students", "/admin/students", 0, setOf(UserRole.ADMIN), adminStudentManagement, "student:read")
        createMenuItem("menu_create_student", "user-plus", "/admin/students/create", "/admin/students/create", 1, setOf(UserRole.ADMIN), adminStudentManagement, "student:create")

        // Parent Management with children
        val adminParentManagement = createMenuItem("menu_parent_management", "file-user", null, null, 5, setOf(UserRole.ADMIN))
        createMenuItem("menu_list_parents", "list", "/admin/parents", "/admin/parents", 0, setOf(UserRole.ADMIN), adminParentManagement, "parent:read")
        createMenuItem("menu_create_parent", "user-plus", "/admin/parents/create", "/admin/parents/create", 1, setOf(UserRole.ADMIN), adminParentManagement, "parent:create")

        // Finance with children
        val adminFinance = createMenuItem("menu_finance", "dollar-sign", null, null, 6, setOf(UserRole.ADMIN))
        createMenuItem("menu_finance_tuition", "credit-card", "/admin/finance/tuition", "/admin/finance/tuition", 0, setOf(UserRole.ADMIN), adminFinance, "finance:tuition:read")
        createMenuItem("menu_finance_invoice", "file-text", "/admin/finance/invoice", "/admin/finance/invoice", 1, setOf(UserRole.ADMIN), adminFinance, "finance:invoice:read")
        createMenuItem("menu_finance_reports", "chart-bar", "/admin/finance/reports", "/admin/finance/reports", 2, setOf(UserRole.ADMIN), adminFinance, "finance:reports:read")

        // Lessons Management with children
        val adminLessonsManagement = createMenuItem("menu_lessons_management", "book-open", null, null, 7, setOf(UserRole.ADMIN))
        createMenuItem("menu_create_lesson", "book-plus", "/admin/lessons/create", "/admin/lessons/create", 0, setOf(UserRole.ADMIN), adminLessonsManagement, "lesson:create")
        createMenuItem("menu_list_lessons", "library", "/admin/lessons", "/admin/lessons", 1, setOf(UserRole.ADMIN), adminLessonsManagement, "lesson:read")

        // School Settings
        val adminSchoolSettings = createMenuItem("menu_school_settings", "settings", "/admin/school-settings", "/admin/school-settings", 8, setOf(UserRole.ADMIN), requiredPermission = "school:settings:read")
        createMenuItem("menu_timestamp", "alarm-clock", "/admin/school-settings/timestamp", "/admin/school-settings/timestamp", 0, setOf(UserRole.SUPERVISOR), adminSchoolSettings, "school:settings:create")

        // ==================== SUPERVISOR MENU ====================

        // Supervisor has same access as ADMIN but without user management
        val supervisorDashboard = createMenuItem("menu_dashboard", "layout-dashboard", "/supervisor/dashboard", "/supervisor/dashboard", 0, setOf(UserRole.SUPERVISOR))

        // Teacher Management with children (same as ADMIN)
        val supervisorTeacherManagement = createMenuItem("menu_teacher_management", "contact", null, null, 1, setOf(UserRole.SUPERVISOR))
        createMenuItem("menu_list_teachers", "list", "/supervisor/teachers", "/supervisor/teachers", 0, setOf(UserRole.SUPERVISOR), supervisorTeacherManagement, "teacher:read")
        createMenuItem("menu_create_teacher", "user-round-plus", "/supervisor/teachers/create", "/supervisor/teachers/create", 1, setOf(UserRole.SUPERVISOR), supervisorTeacherManagement, "teacher:create")

        // Class Management with children (same as ADMIN)
        val supervisorClassManagement = createMenuItem("menu_class_management", "grid-2x2", null, null, 2, setOf(UserRole.SUPERVISOR))
        createMenuItem("menu_list_classes", "list", "/supervisor/classes", "/supervisor/classes", 0, setOf(UserRole.SUPERVISOR), supervisorClassManagement, "class:read")
        createMenuItem("menu_create_class", "grid-2x2-plus", "/supervisor/classes/create", "/supervisor/classes/create", 1, setOf(UserRole.SUPERVISOR), supervisorClassManagement, "class:create")

        // Student Management with children (same as ADMIN)
        val supervisorStudentManagement = createMenuItem("menu_student_management", "graduation-cap", null, null, 3, setOf(UserRole.SUPERVISOR))
        createMenuItem("menu_list_students", "list", "/supervisor/students", "/supervisor/students", 0, setOf(UserRole.SUPERVISOR), supervisorStudentManagement, "student:read")
        createMenuItem("menu_create_student", "user-plus", "/supervisor/students/create", "/supervisor/students/create", 1, setOf(UserRole.SUPERVISOR), supervisorStudentManagement, "student:create")

        // Parent Management with children (same as ADMIN)
        val supervisorParentManagement = createMenuItem("menu_parent_management", "user", null, null, 4, setOf(UserRole.SUPERVISOR))
        createMenuItem("menu_list_parents", "list", "/supervisor/parents", "/supervisor/parents", 0, setOf(UserRole.SUPERVISOR), supervisorParentManagement, "parent:read")
        createMenuItem("menu_create_parent", "user-plus", "/supervisor/parents/create", "/supervisor/parents/create", 1, setOf(UserRole.SUPERVISOR), supervisorParentManagement, "parent:create")

        // Finance with children (same as ADMIN)
        val supervisorFinance = createMenuItem("menu_finance", "dollar-sign", null, null, 5, setOf(UserRole.SUPERVISOR))
        createMenuItem("menu_finance_tuition", "credit-card", "/supervisor/finance/tuition", "/supervisor/finance/tuition", 0, setOf(UserRole.SUPERVISOR), supervisorFinance, "finance:tuition:read")
        createMenuItem("menu_finance_invoice", "file-text", "/supervisor/finance/invoice", "/supervisor/finance/invoice", 1, setOf(UserRole.SUPERVISOR), supervisorFinance, "finance:invoice:read")
        createMenuItem("menu_finance_reports", "chart-bar", "/supervisor/finance/reports", "/supervisor/finance/reports", 2, setOf(UserRole.SUPERVISOR), supervisorFinance, "finance:reports:read")

        // Additional supervisor-specific menus
        createMenuItem("menu_attendance_reports", "clipboard-check", "/supervisor/attendance", "/supervisor/attendance", 6, setOf(UserRole.SUPERVISOR), requiredPermission = "attendance:view")
        createMenuItem("menu_academic_calendar", "calendar", "/supervisor/calendar", "/supervisor/calendar", 7, setOf(UserRole.SUPERVISOR), requiredPermission = "calendar:view")

        // ==================== TEACHER MENU ====================
        createMenuItem("menu_dashboard", "layout-dashboard", "/teacher/dashboard", "/teacher/dashboard", 0, setOf(UserRole.TEACHER))
        createMenuItem("menu_my_classes", "book-open", "/teacher/my-classes", "/teacher/my-classes", 1, setOf(UserRole.TEACHER), requiredPermission = "class:read")
        createMenuItem("menu_my_students", "users", "/teacher/my-students", "/teacher/my-students", 2, setOf(UserRole.TEACHER), requiredPermission = "student:read")
        createMenuItem("menu_attendance", "clipboard-check", "/teacher/attendance", "/teacher/attendance", 3, setOf(UserRole.TEACHER), requiredPermission = "attendance:manage")
        createMenuItem("menu_assignments", "file-text", "/teacher/assignments", "/teacher/assignments", 4, setOf(UserRole.TEACHER), requiredPermission = "assignments:create")
        createMenuItem("menu_grades", "award", "/teacher/grades", "/teacher/grades", 5, setOf(UserRole.TEACHER), requiredPermission = "grades:manage")
        createMenuItem("menu_lesson_plans", "book", "/teacher/lesson-plans", "/teacher/lesson-plans", 6, setOf(UserRole.TEACHER), requiredPermission = "lesson_plans:manage")

        // ==================== STUDENT MENU ====================
        createMenuItem("menu_dashboard", "layout-dashboard", "/student/dashboard", "/student/dashboard", 0, setOf(UserRole.STUDENT))
        createMenuItem("menu_my_profile", "user", "/student/profile", "/student/profile", 1, setOf(UserRole.STUDENT), requiredPermission = "profile:read")
        createMenuItem("menu_my_classes", "book-open", "/student/my-classes", "/student/my-classes", 2, setOf(UserRole.STUDENT), requiredPermission = "classes:read")
        createMenuItem("menu_my_grades", "award", "/student/grades", "/student/grades", 3, setOf(UserRole.STUDENT), requiredPermission = "grades:read")
        createMenuItem("menu_my_assignments", "file-text", "/student/assignments", "/student/assignments", 4, setOf(UserRole.STUDENT), requiredPermission = "assignments:read")
        createMenuItem("menu_my_attendance", "clipboard-check", "/student/attendance", "/student/attendance", 5, setOf(UserRole.STUDENT), requiredPermission = "attendance:read")
        createMenuItem("menu_schedule", "calendar", "/student/schedule", "/student/schedule", 6, setOf(UserRole.STUDENT), requiredPermission = "schedule:view")

        // ==================== PARENT MENU ====================
        createMenuItem("menu_dashboard", "layout-dashboard", "/parent/dashboard", "/parent/dashboard", 0, setOf(UserRole.PARENT))
        createMenuItem("menu_my_children", "users", "/parent/my-children", "/parent/my-children", 1, setOf(UserRole.PARENT), requiredPermission = "children:read")
        createMenuItem("menu_children_grades", "award", "/parent/children-grades", "/parent/children-grades", 2, setOf(UserRole.PARENT), requiredPermission = "children_grades:read")
        createMenuItem("menu_children_attendance", "clipboard-check", "/parent/children-attendance", "/parent/children-attendance", 3, setOf(UserRole.PARENT), requiredPermission = "children_attendance:read")
        createMenuItem("menu_payments", "credit-card", "/parent/payments", "/parent/payments", 4, setOf(UserRole.PARENT), requiredPermission = "payments:view")
        createMenuItem("menu_notifications", "bell", "/parent/notifications", "/parent/notifications", 5, setOf(UserRole.PARENT), requiredPermission = "notifications:receive")

        // ==================== FINANCE TEAM MENU ====================
        createMenuItem("menu_dashboard", "layout-dashboard", "/finance/dashboard", "/finance/dashboard", 0, setOf(UserRole.FINANCE_TEAM))
        createMenuItem("menu_fee_management", "credit-card", "/finance/fees", "/finance/fees", 1, setOf(UserRole.FINANCE_TEAM), requiredPermission = "payments:manage")
        createMenuItem("menu_payment_tracking", "dollar-sign", "/finance/payments", "/finance/payments", 2, setOf(UserRole.FINANCE_TEAM), requiredPermission = "payments:manage")
        createMenuItem("menu_invoices", "file-text", "/finance/invoices", "/finance/invoices", 3, setOf(UserRole.FINANCE_TEAM), requiredPermission = "invoices:read")
        createMenuItem("menu_financial_reports", "bar-chart", "/finance/financial-reports", "/finance/financial-reports", 4, setOf(UserRole.FINANCE_TEAM), requiredPermission = "financial_reports:view")
        createMenuItem("menu_expense_management", "trending-down", "/finance/expenses", "/finance/expenses", 5, setOf(UserRole.FINANCE_TEAM), requiredPermission = "expenses:manage")

        // ==================== CANTEEN OPERATOR MENU ====================
        createMenuItem("menu_dashboard", "layout-dashboard", "/canteen/dashboard", "/canteen/dashboard", 0, setOf(UserRole.CANTEEN_OPERATOR))
        createMenuItem("menu_food_menu", "utensils", "/canteen/food-menu", "/canteen/food-menu", 1, setOf(UserRole.CANTEEN_OPERATOR), requiredPermission = "menu:read")
        createMenuItem("menu_orders", "shopping-cart", "/canteen/orders", "/canteen/orders", 2, setOf(UserRole.CANTEEN_OPERATOR), requiredPermission = "orders:manage")
        createMenuItem("menu_inventory", "package", "/canteen/inventory", "/canteen/inventory", 3, setOf(UserRole.CANTEEN_OPERATOR), requiredPermission = "inventory:manage")
        createMenuItem("menu_sales_reports", "bar-chart", "/canteen/sales-reports", "/canteen/sales-reports", 4, setOf(UserRole.CANTEEN_OPERATOR), requiredPermission = "reports:generate")
    }
}