package com.taali.api.rest


import com.taali.domain.model.SchoolAdmin
import com.taali.domain.service.SchoolAdminService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/school-admins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SchoolAdminResource(private val schoolAdminService: SchoolAdminService) {

    @GET
    fun getAll(): List<SchoolAdmin> = schoolAdminService.getAllSchoolAdmins()

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): SchoolAdmin? = schoolAdminService.getSchoolAdmin(id)

    @POST
    fun create(admin: SchoolAdmin): Unit = schoolAdminService.createSchoolAdmin(admin)

    @PUT
    fun update(admin: SchoolAdmin): Unit = schoolAdminService.updateSchoolAdmin(admin)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) = schoolAdminService.deleteSchoolAdmin(id)
}
