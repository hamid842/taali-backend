package com.taali.api.dto.menu

import com.taali.domain.enum.UserRole

data class CreateMenuItemRequest(
    val titleKey: String,
    val icon: String? = null,
    val route: String? = null,
    val path: String? = null,
    val orderIndex: Int = 0,
    val allowedRoles: Set<UserRole> = emptySet(),
    val parentId: Long? = null,
    val requiredPermission: String? = null
)

data class UpdateMenuItemRequest(
    val titleKey: String? = null,
    val icon: String? = null,
    val route: String? = null,
    val path: String? = null,
    val orderIndex: Int? = null,
    val requiredPermission: String? = null
)

data class MenuItemRoleUpdateRequest(
    val role: UserRole,
    val action: String // "ADD" or "REMOVE"
)