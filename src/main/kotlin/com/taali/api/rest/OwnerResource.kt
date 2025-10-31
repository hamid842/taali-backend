package com.taali.api.rest

import com.taali.domain.model.Owner
import com.taali.domain.service.OwnerService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OwnerResource(private val ownerService: OwnerService) {

    @GET
    fun getAll(): List<Owner> = ownerService.getAllOwners()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Owner? = ownerService.getOwner(id)

    @POST
    fun create(owner: Owner): Unit = ownerService.createOwner(owner)

    @PUT
    fun update(owner: Owner): Unit = ownerService.updateOwner(owner)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = ownerService.deleteOwner(id)
}
