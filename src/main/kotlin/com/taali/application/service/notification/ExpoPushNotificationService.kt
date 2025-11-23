package com.taali.application.service.notification

import com.taali.api.dto.notification.ExpoPushMessage
import com.taali.domain.model.notification.DeviceToken
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import jakarta.transaction.Transactional

@ApplicationScoped
class ExpoPushNotificationService {

    @Inject
    @RestClient
    lateinit var expoClient: ExpoPushClient

    private val logger = LoggerFactory.getLogger(ExpoPushNotificationService::class.java)

    fun sendMessageNotification(
        recipientTokens: List<String>, senderName: String, messagePreview: String, conversationId: Long, messageId: Long
    ) {
        if (recipientTokens.isEmpty()) {
            logger.debug("No recipient tokens provided for push notification")
            return
        }

        try {
            val messages = recipientTokens.map { token ->
                ExpoPushMessage(
                    to = token, title = "New message from $senderName", body = if (messagePreview.length > 100) {
                        "${messagePreview.take(100)}..."
                    } else {
                        messagePreview
                    }, data = mapOf(
                        "type" to "MESSAGE",
                        "conversationId" to conversationId,
                        "messageId" to messageId,
                        "senderName" to senderName
                    ), sound = "default", badge = 1
                )
            }

            val response = expoClient.sendPushNotifications(messages)

            response.data.forEachIndexed { index, ticket ->
                if (ticket.status == "error") {
                    logger.warn("Failed to send push notification to token ${recipientTokens[index]}: ${ticket.message}")

                    if (ticket.details?.get("error") == "DeviceNotRegistered") {
                        deactivateToken(recipientTokens[index])
                    }
                } else {
                    logger.info("Push notification sent successfully to ${recipientTokens[index]}")
                }
            }

        } catch (e: Exception) {
            logger.error("Failed to send push notifications", e)
        }
    }

    @Transactional
    fun deactivateToken(token: String) {
        val deviceToken = DeviceToken.findByExpoToken(token)
        if (deviceToken != null) {
            deviceToken.isActive = false
            logger.info("Deactivated device token for user ${deviceToken.user.id}")
        }
    }
}
