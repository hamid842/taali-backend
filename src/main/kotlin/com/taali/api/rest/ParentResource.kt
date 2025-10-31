package com.taali.api.rest

import com.taali.domain.model.Parents
import com.taali.domain.service.ParentService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/parents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ParentResource(private val parentService: ParentService) {

    @GET
    fun getAll(): List<Parents> = parentService.getAllParents()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Parents? = parentService.getParent(id)

    @POST
    fun create(parents: Parents): Unit = parentService.createParent(parents)

    @PUT
    fun update(parents: Parents): Unit = parentService.updateParent(parents)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = parentService.deleteParent(id)
}
