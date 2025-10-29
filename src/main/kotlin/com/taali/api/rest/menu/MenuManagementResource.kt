package com.taali.api.rest.menu

import com.taali.api.dto.menu.*
import com.taali.domain.enum.UserRole
import com.taali.application.service.menu.MenuService
import com.taali.shared.RequestContext
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MenuResource {

    @Inject
    lateinit var menuService: MenuService

    @Inject
    lateinit var requestContext: RequestContext

    @GET
    @Path("/user")
    @PermitAll
    fun getUserMenu(
        @QueryParam("role") role: String,
    ): Response {
        try {
            // Parse user role
            val userRole = UserRole.valueOf(role.uppercase())

            val menuItems = menuService.getMenuForRole(userRole)

            return Response.ok(menuItems).build()
        } catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                    mapOf(
                        "error" to "Invalid role: $role. Valid roles: ${
                            UserRole.entries.joinToString { it.name }
                        }"
                    )
                )
                .build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to fetch menu: ${e.message}"))
                .build()
        }
    }
}

@Path("/api/admin/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
class MenuManagementResource {

    @Inject
    lateinit var menuService: MenuService

    @GET
    fun getAllMenuItems(): Response {
        val menuItems = menuService.getAllMenuItems()
        return Response.ok(menuItems).build()
    }

    @GET
    @Path("/hierarchy")
    fun getMenuHierarchy(): Response {
        val hierarchy = menuService.getMenuHierarchy()
        return Response.ok(hierarchy).build()
    }

    @POST
    fun createMenuItem(request: CreateMenuItemRequest): Response {
        try {
            val parent = request.parentId?.let { menuService.menuItemRepository.findById(it) }
            val menuItem = menuService.createMenuItem(
                titleKey = request.titleKey,
                icon = request.icon,
                route = request.route,
                orderIndex = request.orderIndex,
                roles = request.allowedRoles,
                parent = parent,
                requiredPermission = request.requiredPermission
            )
            return Response.status(Response.Status.CREATED).entity(menuItem).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Failed to create menu item: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{id}")
    fun updateMenuItem(@PathParam("id") id: Long, request: UpdateMenuItemRequest): Response {
        try {
            val menuItem = menuService.menuItemRepository.findById(id)
                ?: return Response.status(Response.Status.NOT_FOUND).build()

            request.titleKey?.let { menuItem.titleKey = it }
            request.icon?.let { menuItem.icon = it }
            request.route?.let { menuItem.route = it }
            request.path?.let { menuItem.path = it }
            request.orderIndex?.let { menuItem.orderIndex = it }
            request.requiredPermission?.let { menuItem.requiredPermission = it }

            menuService.menuItemRepository.persist(menuItem)
            return Response.ok(menuItem).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Failed to update menu item: ${e.message}"))
                .build()
        }
    }

    @PUT
    @Path("/{id}/order")
    fun updateMenuItemOrder(@PathParam("id") id: Long, @QueryParam("order") order: Int): Response {
        val menuItem = menuService.updateMenuItemOrder(id, order)
            ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(menuItem).build()
    }

    @PUT
    @Path("/{id}/roles")
    fun updateMenuItemRoles(@PathParam("id") id: Long, request: MenuItemRoleUpdateRequest): Response {
        val menuItem = when (request.action.uppercase()) {
            "ADD" -> menuService.addRoleToMenuItem(id, request.role)
            "REMOVE" -> menuService.removeRoleFromMenuItem(id, request.role)
            else -> return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Invalid action. Use 'ADD' or 'REMOVE'"))
                .build()
        }

        return menuItem?.let {
            Response.ok(it).build()
        } ?: Response.status(Response.Status.NOT_FOUND).build()
    }

    @GET
    @Path("/permissions/{permission}")
    fun getMenuItemsByPermission(@PathParam("permission") permission: String): Response {
        val menuItems = menuService.getMenuItemsByPermission(permission)
        return Response.ok(menuItems).build()
    }

//    @GET
//    @Path("/roles/{role}")
//    fun getMenuItemsByRole(@PathParam("role") role: String): Response {
//        try {
//            val userRole = UserRole.valueOf(role.uppercase())
//            val menuItems = menuService.getMenuForRole(userRole)
//            return Response.ok(menuItems).build()
//        } catch (e: IllegalArgumentException) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                .entity(mapOf("error" to "Invalid role: $role"))
//                .build()
//        }
//    }

    @DELETE
    @Path("/{id}")
    fun deleteMenuItem(@PathParam("id") id: Long): Response {
        val deleted = menuService.menuItemRepository.deleteById(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @POST
    @Path("/initialize")
    fun initializeDefaultMenu(): Response {
        try {
            menuService.createDefaultMenu()
            return Response.ok(mapOf("message" to "Default menu initialized successfully")).build()
        } catch (e: Exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to initialize menu: ${e.message}"))
                .build()
        }
    }
}