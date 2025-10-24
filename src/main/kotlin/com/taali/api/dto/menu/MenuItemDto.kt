package com.taali.api.dto.menu

import com.taali.domain.model.menu.MenuItem

data class MenuItemDto(
    val id: Long? = null,
    val titleKey: String,
    val title: String? = null, // Make this nullable since it will be populated later
    val icon: String? = null,
    val route: String? = null,
    val path: String? = null,
    val orderIndex: Int,
    val children: List<MenuItemDto> = emptyList(),
    val requiredPermission: String? = null,
    val allowedRoles: Set<String> = emptySet(), // Added this field
    val parentId: Long? = null, // Added this field
    val isRootItem: Boolean = false, // Added this field
    val hasChildren: Boolean = false // Added this field
)

data class UserMenuResponse(
    val menu: List<MenuItemDto>,
    val userRole: String,
    val permissions: List<String>,
    val accessibleRoutes: List<String> = emptyList() // Added for frontend routing
)

// Extension functions for conversion
fun MenuItem.toDto(includeChildren: Boolean = true): MenuItemDto {
    return MenuItemDto(
        id = this.id,
        titleKey = this.titleKey,
        icon = this.icon,
        route = this.route,
        path = this.path,
        orderIndex = this.orderIndex,
        requiredPermission = this.requiredPermission,
        allowedRoles = this.allowedRoles,
        parentId = this.parent?.id,
        isRootItem = this.parent == null,
        hasChildren = this.children.isNotEmpty(),
        children = if (includeChildren) this.children.map { it.toDto(true) } else emptyList()
    )
}

fun List<MenuItem>.toDtoList(includeChildren: Boolean = true): List<MenuItemDto> {
    return this.map { it.toDto(includeChildren) }
}