package com.taali.resource

import com.taali.dto.otp.OtpVerificationRequest
import com.taali.dto.otp.ResendOtpRequest
import com.taali.dto.register.RegisterRequest
import com.taali.service.AuthService
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "User authentication and registration")
class AuthResource {

        @Inject lateinit var authService: AuthService

        @POST
        @Path("/register")
        @Operation(summary = "Register a new user with OTP verification")
        fun register(@Valid request: RegisterRequest): Response {
                val result = authService.register(request)

                return if (result.success) {
                        if (result.requiresVerification) {
                                Response.status(Response.Status.OK).entity(result).build()
                        } else {
                                Response.status(Response.Status.CREATED).entity(result).build()
                        }
                } else {
                        Response.status(Response.Status.BAD_REQUEST).entity(result).build()
                }
        }

        @POST
        @Path("/verify-otp")
        @Operation(summary = "Verify OTP code for registration")
        fun verifyOtp(@Valid request: OtpVerificationRequest): Response {
                val result = authService.verifyOtp(request)

                return if (result.success) {
                        Response.status(Response.Status.OK).entity(result).build()
                } else {
                        Response.status(Response.Status.BAD_REQUEST).entity(result).build()
                }
        }

        @POST
        @Path("/resend-otp")
        @Operation(summary = "Resend OTP code")
        fun resendOtp(@Valid request: ResendOtpRequest): Response {
                val result = authService.resendOtp(request)

                return if (result.success) {
                        Response.status(Response.Status.OK).entity(result).build()
                } else {
                        Response.status(Response.Status.BAD_REQUEST).entity(result).build()
                }
        }
}
