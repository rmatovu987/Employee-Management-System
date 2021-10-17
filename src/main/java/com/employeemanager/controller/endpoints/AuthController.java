package com.employeemanager.controller.endpoints;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.employeemanager.configuration.handler.ResponseMessage;
import com.employeemanager.controller.services.AuthService;
import com.employeemanager.controller.services.payload.LoginRequest;
import com.employeemanager.controller.services.payload.SignupRequest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.http.HttpServerRequest;

@Path("authentication")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Authentication", description = "Auth portal")
@SecurityRequirement(name = "Authorization")
public class AuthController {
    @Inject
    AuthService authService;

    @POST
    @Path("signup")
    @Transactional
    @Operation(summary = "Sign up as a manager", description = "This will create a manager.")
    @APIResponse(description = "Success", responseCode = "200")
    public Response create(SignupRequest request, @Context HttpServerRequest httprequest) {
        authService.signup(request, httprequest);
        return Response.ok(new ResponseMessage("Sucessfully signed up. Login to continue.")).build();
    }

    @POST
    @Path("login")
    @Transactional
    @Operation(summary = "Login into the system.", description = "Gateway for the manager")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    public Response login(@Valid LoginRequest request, @Context HttpServerRequest httprequest) {
        return Response.ok(new ResponseMessage("Login successful.", authService.login(request, httprequest))).build();
    }

    @PUT
    @Path("reset-password/{email}")
    @Transactional
    @Operation(summary = "Reset your password.", description = "Forgot password, no worries. Just reset it")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    public Response resetPassword(@PathParam("email") String email, @Context HttpServerRequest httprequest) {
        return Response.ok(new ResponseMessage( "Password reset successful.", authService.resetPasswordToken(email, httprequest))).build();
    }
}
