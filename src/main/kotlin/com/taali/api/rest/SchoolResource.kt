package com.taali.api.rest

import com.taali.domain.model.School
import com.taali.domain.service.SchoolService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/schools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SchoolResource(private val schoolService: SchoolService) {

    @GET
    fun getAll(): List<School> = schoolService.getAllSchools()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): School? = schoolService.getSchool(id)

    @POST
    fun create(school: School): Unit = schoolService.createSchool(school)

    @PUT
    fun update(school: School): Unit = schoolService.updateSchool(school)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = schoolService.deleteSchool(id)
}
