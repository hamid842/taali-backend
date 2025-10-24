package com.taali.domain.model.menu

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import jakarta.transaction.Transactional
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "menu_items",
    indexes = [
        Index(name = "idx_menu_items_parent_id", columnList = "parent_id"),
        Index(name = "idx_menu_items_order_index", columnList = "order_index"),
        Index(name = "idx_menu_items_route", columnList = "route"),
        Index(name = "idx_menu_items_title_key", columnList = "title_key")
    ]
)
class MenuItem : PanacheEntity() {

    @Column(name = "title_key", nullable = false, length = 100)
    lateinit var titleKey: String

    @Column(name = "icon", length = 50)
    var icon: String? = null

    @Column(name = "route", length = 200)
    var route: String? = null

    @Column(name = "path", length = 200)
    var path: String? = null

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int = 0

    @Column(name = "required_permission", length = 100)
    var requiredPermission: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = ForeignKey(name = "fk_menu_items_parent"))
    var parent: MenuItem? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    var children: MutableList<MenuItem> = mutableListOf()

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "menu_item_roles",
        joinColumns = [JoinColumn(name = "menu_item_id")],
        foreignKey = ForeignKey(name = "fk_menu_item_roles_menu_item")
    )
    @Column(name = "role", nullable = false, length = 50)
    var allowedRoles: MutableSet<String> = mutableSetOf()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    // Business logic methods
    fun isRootItem(): Boolean = parent == null

    fun hasChildren(): Boolean = children.isNotEmpty()

    fun isAccessibleByRole(role: String): Boolean = allowedRoles.contains(role)

    fun addChild(child: MenuItem) {
        child.parent = this
        children.add(child)
    }

    fun removeChild(child: MenuItem) {
        child.parent = null
        children.remove(child)
    }

    fun addRole(role: String) {
        allowedRoles.add(role)
    }

    fun removeRole(role: String) {
        allowedRoles.remove(role)
    }

    override fun toString(): String {
        return "MenuItem(id=$id, titleKey='$titleKey', route=$route, orderIndex=$orderIndex, parent=${parent?.id})"
    }

    companion object : PanacheCompanion<MenuItem> {
        fun findByTitleKey(titleKey: String): MenuItem? {
            return find("titleKey", titleKey).firstResult()
        }

        fun existsByTitleKey(titleKey: String): Boolean {
            return count("titleKey", titleKey) > 0
        }

        fun findByRoute(route: String): MenuItem? {
            return find("route", route).firstResult()
        }

        fun findRootItems(): List<MenuItem> {
            return find("parent is null ORDER BY orderIndex").list()
        }

        fun findByRole(role: String): List<MenuItem> {
            return find("?1 MEMBER OF allowedRoles ORDER BY orderIndex", role).list()
        }

        fun findLeafItems(): List<MenuItem> {
            return find("children is empty ORDER BY orderIndex").list()
        }

        fun findMenuItemsWithPermission(): List<MenuItem> {
            return find("requiredPermission is not null ORDER BY orderIndex").list()
        }

        fun findMaxOrderIndex(): Int? =
            find("SELECT COALESCE(MAX(m.orderIndex), 0) FROM MenuItem m")
                .project(Int::class.java)
                .firstResult()

        @Transactional
        fun createRootItem(
            titleKey: String,
            icon: String? = null,
            route: String? = null,
            orderIndex: Int? = null,
            allowedRoles: Set<String> = emptySet(),
            requiredPermission: String? = null
        ): MenuItem {
            val actualOrderIndex = orderIndex ?: (findMaxOrderIndex()?.plus(1))

            return MenuItem().apply {
                this.titleKey = titleKey
                this.icon = icon
                this.route = route
                if (actualOrderIndex != null) {
                    this.orderIndex = actualOrderIndex
                }
                this.allowedRoles.addAll(allowedRoles)
                this.requiredPermission = requiredPermission
            }.also { it.persist() }
        }

        @Transactional
        fun createChildItem(
            parent: MenuItem,
            titleKey: String,
            icon: String? = null,
            route: String? = null,
            orderIndex: Int? = null,
            allowedRoles: Set<String> = emptySet(),
            requiredPermission: String? = null
        ): MenuItem {
            val actualOrderIndex = orderIndex ?: (parent.children.size + 1)

            return MenuItem().apply {
                this.titleKey = titleKey
                this.icon = icon
                this.route = route
                this.orderIndex = actualOrderIndex
                this.parent = parent
                this.allowedRoles.addAll(allowedRoles)
                this.requiredPermission = requiredPermission
            }.also {
                it.persist()
                parent.children.add(it)
            }
        }
    }
}