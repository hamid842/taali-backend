package com.taali.infrastructure.persistence.repository.menu

import com.taali.domain.model.menu.MenuItem
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class MenuItemRepository : PanacheRepository<MenuItem> {

    fun findByRole(role: String): List<MenuItem> {
        return find("SELECT m FROM MenuItem m WHERE ?1 MEMBER OF m.allowedRoles ORDER BY m.orderIndex", role).list()
    }

    fun findByParentIsNull(): List<MenuItem> {
        return find("parent is null ORDER BY orderIndex").list()
    }

    fun findByParentId(parentId: Long): List<MenuItem> {
        return find("parent.id = ?1 ORDER BY orderIndex", parentId).list()
    }

    fun findByRoute(route: String): MenuItem? {
        return find("route", route).firstResult()
    }

    fun findByRequiredPermission(permission: String): List<MenuItem> {
        return find("requiredPermission = ?1 ORDER BY orderIndex", permission).list()
    }

    fun findMenuItemsWithPermission(): List<MenuItem> {
        return find("requiredPermission is not null ORDER BY orderIndex").list()
    }

    fun findByOrderIndexGreaterThan(orderIndex: Int): List<MenuItem> {
        return find("orderIndex > ?1 ORDER BY orderIndex", orderIndex).list()
    }

    fun findByOrderIndexBetween(start: Int, end: Int): List<MenuItem> {
        return find("orderIndex between ?1 and ?2 ORDER BY orderIndex", start, end).list()
    }

    fun findByRolesIn(roles: Set<String>): List<MenuItem> {
        return if (roles.isEmpty()) {
            emptyList()
        } else {
            find(
                """
                SELECT m FROM MenuItem m 
                WHERE EXISTS (
                    SELECT r FROM m.allowedRoles r 
                    WHERE r IN (?1)
                ) 
                ORDER BY m.orderIndex
            """, roles
            ).list()
        }
    }

    fun findRootMenuItems(): List<MenuItem> {
        return find("parent is null ORDER BY orderIndex").list()
    }

    fun findLeafMenuItems(): List<MenuItem> {
        return find("children is empty ORDER BY orderIndex").list()
    }

    fun findByIcon(icon: String): List<MenuItem> {
        return find("icon = ?1 ORDER BY orderIndex", icon).list()
    }

    fun findMenuItemsWithoutRoute(): List<MenuItem> {
        return find("route is null ORDER BY orderIndex").list()
    }

    fun findMenuItemsWithRoute(): List<MenuItem> {
        return find("route is not null ORDER BY orderIndex").list()
    }

    @Transactional
    fun updateOrderIndex(menuItemId: Long, newOrderIndex: Int): Int {
        return update("orderIndex = ?1 where id = ?2", newOrderIndex, menuItemId)
    }

    @Transactional
    fun updateParent(menuItemId: Long, parentId: Long?): Int {
        return if (parentId == null) {
            update("parent = null where id = ?1", menuItemId)
        } else {
            update("parent.id = ?1 where id = ?2", parentId, menuItemId)
        }
    }

    @Transactional
    fun addRoleToMenuItem(menuItemId: Long, role: String): Boolean {
        val menuItem = findById(menuItemId)
        return menuItem?.let {
            it.allowedRoles.add(role)
            true
        } ?: false
    }

    @Transactional
    fun removeRoleFromMenuItem(menuItemId: Long, role: String): Boolean {
        val menuItem = findById(menuItemId)
        return menuItem?.let {
            it.allowedRoles.remove(role)
            true
        } ?: false
    }

    fun countByParent(parentId: Long): Long {
        return count("parent.id", parentId)
    }

    fun findMaxOrderIndex(): Int {
        return find("SELECT COALESCE(MAX(m.orderIndex), 0) FROM MenuItem m").firstResult() as Int? ?: 0
    }

    fun findNextOrderIndex(): Int {
        return findMaxOrderIndex() + 1
    }
}