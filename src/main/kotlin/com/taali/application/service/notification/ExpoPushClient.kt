package com.taali.application.service.notification

import com.taali.api.dto.notification.ExpoPushMessage
import com.taali.api.dto.notification.ExpoPushResponse
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "expo")
@Path("/push")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface ExpoPushClient {

    @POST
    @Path("/send")
    fun sendPushNotifications(messages: List<ExpoPushMessage>): ExpoPushResponse
}