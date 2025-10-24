package com.taali.domain.service.menu

import com.taali.api.dto.menu.MenuItemDto
import com.taali.domain.enum.UserRole
import com.taali.domain.model.menu.MenuItem
import com.taali.infrastructure.persistence.repository.menu.MenuItemRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional

@ApplicationScoped
class MenuService(
    @Inject val menuItemRepository: MenuItemRepository
) {

    @PostConstruct
    @Transactional
    fun initializeDefaultMenu() {
        if (menuItemRepository.count() == 0L) {
            createDefaultMenu()
        }
    }

    fun getMenuForRole(role: UserRole): List<MenuItemDto> {
        val menuItems = menuItemRepository.findByRole(role.name)
        return buildMenuTree(menuItems)
    }

    fun getPermissionsForRole(role: UserRole): List<String> {
        return when (role) {
            UserRole.ADMIN -> listOf(
                "school:create", "school:read", "school:update", "school:delete",
                "user:create", "user:read", "user:update", "user:delete",
                "finance:read", "reports:generate", "system:manage"
            )

            UserRole.SUPERVISOR -> listOf(
                "teacher:create", "teacher:read", "teacher:update",
                "class:create", "class:read", "class:update",
                "student:create", "student:read", "student:update",
                "parent:create", "parent:read", "parent:update",
                "attendance:manage", "grades:view"
            )

            UserRole.TEACHER -> listOf(
                "class:read", "student:read", "attendance:manage",
                "grades:manage", "assignments:create", "assignments:read",
                "assignments:update", "student:progress:view"
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
                "financial_reports:view"
            )
        }
    }

    fun getAllMenuItems(): List<MenuItemDto> {
        val allItems = menuItemRepository.listAll()
        return buildMenuTree(allItems)
    }

    fun getMenuItemsByPermission(permission: String): List<MenuItemDto> {
        val menuItems = menuItemRepository.findByRequiredPermission(permission)
        return buildMenuTree(menuItems)
    }

    fun getMenuHierarchy(): List<MenuItemDto> {
        val rootItems = menuItemRepository.findByParentIsNull()
        return buildMenuTree(rootItems)
    }


    @Transactional
    fun createMenuItem(
        titleKey: String,
        icon: String? = null,
        route: String? = null,
        orderIndex: Int = 0,
        roles: Set<UserRole>,
        parent: MenuItem? = null, // Changed from MenuItemDto to MenuItem
        requiredPermission: String? = null
    ): MenuItem { // Changed return type to MenuItem
        return MenuItem().apply {
            this.titleKey = titleKey
            this.icon = icon
            this.route = route
            this.orderIndex = orderIndex
            this.parent = parent
            this.requiredPermission = requiredPermission
            this.allowedRoles.addAll(roles.map { it.name })
        }.also { menuItemRepository.persist(it) }
    }

    @Transactional
    fun updateMenuItemOrder(menuItemId: Long, newOrder: Int): MenuItem? { // Changed return type
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.orderIndex = newOrder
        return menuItem
    }

    @Transactional
    fun addRoleToMenuItem(menuItemId: Long, role: UserRole): MenuItem? { // Changed return type
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.allowedRoles?.add(role.name)
        return menuItem
    }

    @Transactional
    fun removeRoleFromMenuItem(menuItemId: Long, role: UserRole): MenuItem? { // Changed return type
        val menuItem = menuItemRepository.findById(menuItemId)
        menuItem?.allowedRoles?.remove(role.name)
        return menuItem
    }

    fun hasAccessToMenuItem(menuItem: MenuItem, userRole: UserRole): Boolean { // Changed parameter type
        return menuItem.allowedRoles.contains(userRole.name)
    }

    fun getAccessibleRoutes(userRole: UserRole): List<String> {
        return menuItemRepository.findByRole(userRole.name)
            .filter { it.route != null }
            .map { it.route!! }
    }

    private fun buildMenuTree(menuItems: List<MenuItem>): List<MenuItemDto> {
        val rootItems = menuItems.filter { it.parent == null }
            .sortedBy { it.orderIndex }

        return rootItems.map { mapToDto(it, menuItems) }
    }

    private fun mapToDto(menuItem: MenuItem, allItems: List<MenuItem>): MenuItemDto {
        val children = allItems
            .filter { it.parent?.id == menuItem.id }
            .sortedBy { it.orderIndex }
            .map { mapToDto(it, allItems) }

        return MenuItemDto(
            id = menuItem.id,
            titleKey = menuItem.titleKey,
            icon = menuItem.icon,
            route = menuItem.route,
            path = menuItem.path,
            orderIndex = menuItem.orderIndex,
            children = children,
            requiredPermission = menuItem.requiredPermission,
            allowedRoles = menuItem.allowedRoles,
            parentId = menuItem.parent?.id,
            isRootItem = menuItem.parent == null,
            hasChildren = menuItem.children.isNotEmpty()
        )
    }

    @Transactional
    fun createDefaultMenu() {
        // Clear existing menu items
        menuItemRepository.deleteAll()

        // Admin Menu
        val adminDashboard = createMenuItem("menu.dashboard", "home", "/admin/dashboard", 0, setOf(UserRole.ADMIN))
        val schoolManagement = createMenuItem("menu.schoolManagement", "building", null, 1, setOf(UserRole.ADMIN))
        createMenuItem(
            "menu.createSchool",
            "plus",
            "/admin/schools/create",
            0,
            setOf(UserRole.ADMIN),
            schoolManagement,
            "school:create"
        )
        createMenuItem(
            "menu.listSchools",
            "list",
            "/admin/schools",
            1,
            setOf(UserRole.ADMIN),
            schoolManagement,
            "school:read"
        )

        val userManagement = createMenuItem("menu.userManagement", "users", null, 2, setOf(UserRole.ADMIN))
        createMenuItem(
            "menu.createUser",
            "user-plus",
            "/admin/users/create",
            0,
            setOf(UserRole.ADMIN),
            userManagement,
            "user:create"
        )
        createMenuItem("menu.listUsers", "users", "/admin/users", 1, setOf(UserRole.ADMIN), userManagement, "user:read")

        createMenuItem(
            "menu.finance",
            "dollar-sign",
            "/admin/finance",
            3,
            setOf(UserRole.ADMIN),
            requiredPermission = "finance:read"
        )

        // Supervisor Menu
        val supervisorDashboard =
            createMenuItem("menu.dashboard", "home", "/supervisor/dashboard", 0, setOf(UserRole.SUPERVISOR))
        createMenuItem(
            "menu.teacherManagement",
            "user-check",
            "/supervisor/teachers",
            1,
            setOf(UserRole.SUPERVISOR),
            requiredPermission = "teacher:read"
        )
        createMenuItem(
            "menu.classManagement",
            "users",
            "/supervisor/classes",
            2,
            setOf(UserRole.SUPERVISOR),
            requiredPermission = "class:read"
        )
        createMenuItem(
            "menu.studentManagement",
            "graduation-cap",
            "/supervisor/students",
            3,
            setOf(UserRole.SUPERVISOR),
            requiredPermission = "student:read"
        )
        createMenuItem(
            "menu.parentManagement",
            "user",
            "/supervisor/parents",
            4,
            setOf(UserRole.SUPERVISOR),
            requiredPermission = "parent:read"
        )

        // Teacher Menu
        val teacherDashboard =
            createMenuItem("menu.dashboard", "home", "/teacher/dashboard", 0, setOf(UserRole.TEACHER))
        createMenuItem(
            "menu.myClasses",
            "book-open",
            "/teacher/my-classes",
            1,
            setOf(UserRole.TEACHER),
            requiredPermission = "class:read"
        )
        createMenuItem(
            "menu.myStudents",
            "users",
            "/teacher/my-students",
            2,
            setOf(UserRole.TEACHER),
            requiredPermission = "student:read"
        )
        createMenuItem(
            "menu.attendance",
            "clipboard-check",
            "/teacher/attendance",
            3,
            setOf(UserRole.TEACHER),
            requiredPermission = "attendance:manage"
        )
        createMenuItem(
            "menu.assignments",
            "file-text",
            "/teacher/assignments",
            4,
            setOf(UserRole.TEACHER),
            requiredPermission = "assignments:create"
        )

        // Student Menu
        val studentDashboard =
            createMenuItem("menu.dashboard", "home", "/student/dashboard", 0, setOf(UserRole.STUDENT))
        createMenuItem(
            "menu.myProfile",
            "user",
            "/student/profile",
            1,
            setOf(UserRole.STUDENT),
            requiredPermission = "profile:read"
        )
        createMenuItem(
            "menu.myClasses",
            "book-open",
            "/student/my-classes",
            2,
            setOf(UserRole.STUDENT),
            requiredPermission = "classes:read"
        )
        createMenuItem(
            "menu.myGrades",
            "award",
            "/student/grades",
            3,
            setOf(UserRole.STUDENT),
            requiredPermission = "grades:read"
        )

        // Parent Menu
        val parentDashboard = createMenuItem("menu.dashboard", "home", "/parent/dashboard", 0, setOf(UserRole.PARENT))
        createMenuItem(
            "menu.myChildren",
            "users",
            "/parent/my-children",
            1,
            setOf(UserRole.PARENT),
            requiredPermission = "children:read"
        )
        createMenuItem(
            "menu.childrenGrades",
            "award",
            "/parent/children-grades",
            2,
            setOf(UserRole.PARENT),
            requiredPermission = "children_grades:read"
        )
        createMenuItem(
            "menu.childrenAttendance",
            "clipboard-check",
            "/parent/children-attendance",
            3,
            setOf(UserRole.PARENT),
            requiredPermission = "children_attendance:read"
        )
        createMenuItem(
            "menu.payments",
            "credit-card",
            "/parent/payments",
            4,
            setOf(UserRole.PARENT),
            requiredPermission = "payments:view"
        )

        // Canteen Operator Menu
        val canteenDashboard =
            createMenuItem("menu.dashboard", "home", "/canteen/dashboard", 0, setOf(UserRole.CANTEEN_OPERATOR))
        createMenuItem(
            "menu.foodMenu",
            "utensils",
            "/canteen/food-menu",
            1,
            setOf(UserRole.CANTEEN_OPERATOR),
            requiredPermission = "menu:read"
        )
        createMenuItem(
            "menu.orders",
            "shopping-cart",
            "/canteen/orders",
            2,
            setOf(UserRole.CANTEEN_OPERATOR),
            requiredPermission = "orders:manage"
        )
        createMenuItem(
            "menu.inventory",
            "package",
            "/canteen/inventory",
            3,
            setOf(UserRole.CANTEEN_OPERATOR),
            requiredPermission = "inventory:manage"
        )

        // Finance Team Menu
        val financeDashboard =
            createMenuItem("menu.dashboard", "home", "/finance/dashboard", 0, setOf(UserRole.FINANCE_TEAM))
        createMenuItem(
            "menu.financialReports",
            "bar-chart",
            "/finance/financial-reports",
            1,
            setOf(UserRole.FINANCE_TEAM),
            requiredPermission = "financial_reports:view"
        )
        createMenuItem(
            "menu.paymentManagement",
            "credit-card",
            "/finance/payments",
            2,
            setOf(UserRole.FINANCE_TEAM),
            requiredPermission = "payments:manage"
        )
        createMenuItem(
            "menu.invoices",
            "file-text",
            "/finance/invoices",
            3,
            setOf(UserRole.FINANCE_TEAM),
            requiredPermission = "invoices:read"
        )
    }
}