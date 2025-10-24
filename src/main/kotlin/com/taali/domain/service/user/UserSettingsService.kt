package com.taali.domain.service.user

import com.taali.api.dto.menu.MenuItemDto
import com.taali.api.dto.menu.UserMenuResponse
import com.taali.api.dto.user.UpdateUserSettingsRequest
import com.taali.api.dto.user.UserSettingsDto
import com.taali.domain.model.user.User
import com.taali.domain.model.user.UserSettings
import com.taali.domain.service.menu.MenuService
import com.taali.domain.service.shared.TranslationService
import com.taali.infrastructure.persistence.repository.user.UserSettingsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.Locale

@ApplicationScoped
class UserSettingsService(
    private val userSettingsRepository: UserSettingsRepository,
    private val menuService: MenuService,
    private val translationService: TranslationService
) {

    fun getUserSettings(user: User): UserSettingsDto {
        val settings = userSettingsRepository.findOrCreateDefault(user)
        return mapToDto(settings)
    }

    @Transactional
    fun updateUserSettings(user: User, request: UpdateUserSettingsRequest): UserSettingsDto {
        val settings = userSettingsRepository.findOrCreateDefault(user)

        request.preferredLanguage?.let { settings.preferredLanguage = it }
        request.fontSize?.let { settings.fontSize = it }
        request.theme?.let { settings.theme = it }
        request.primaryColor?.let { settings.primaryColor = it }
        request.notificationsEnabled?.let { settings.notificationsEnabled = it }
        request.emailNotifications?.let { settings.emailNotifications = it }
        request.smsNotifications?.let { settings.smsNotifications = it }

        userSettingsRepository.persist(settings)
        return mapToDto(settings)
    }

    @Transactional
    fun resetToDefaultSettings(user: User): UserSettingsDto {
        // Delete existing settings
        userSettingsRepository.findByUserId(user.id!!)?.let {
            userSettingsRepository.delete(it)
        }

        // Create new default settings
        val defaultSettings = UserSettings.createDefault(user)
        userSettingsRepository.persist(defaultSettings)

        return mapToDto(defaultSettings)
    }

    fun getUserMenu(user: User): UserMenuResponse {
        val userSettings = userSettingsRepository.findOrCreateDefault(user)
        val locale = userSettings.preferredLanguage

        val menuItems = menuService.getMenuForRole(user.role)
        val translatedMenu = menuItems.map { menuItem ->
            translateMenuItem(menuItem, locale)
        }

        val permissions = menuService.getPermissionsForRole(user.role)

        return UserMenuResponse(
            menu = translatedMenu,
            userRole = user.role.name,
            permissions = permissions
        )
    }

    private fun translateMenuItem(menuItem: MenuItemDto, locale: Locale): MenuItemDto {
        return menuItem.copy(
            title = translationService.getMessage(menuItem.titleKey, locale),
            children = menuItem.children.map { translateMenuItem(it, locale) }
        )
    }

    private fun mapToDto(settings: UserSettings): UserSettingsDto {
        return UserSettingsDto(
            id = settings.id,
            preferredLanguage = settings.preferredLanguage,
            fontSize = settings.fontSize,
            theme = settings.theme,
            primaryColor = settings.primaryColor,
            notificationsEnabled = settings.notificationsEnabled,
            emailNotifications = settings.emailNotifications,
            smsNotifications = settings.smsNotifications,
            updatedAt = settings.updatedAt
        )
    }
}