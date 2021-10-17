package com.employeemanager.controller.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.employeemanager.auditTrail.AuditTrail;
import com.employeemanager.configuration.handler.ResponseMessage;
import com.employeemanager.controller.services.AuditService;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.PositionEnum;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("audit")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Audit", description = "Audit")
@SecurityRequirement(name = "Authorization")
public class AuditController {

    @Inject
    AuditService auditService;
    
    @GET
    @Path("auditTrail")
    @Transactional
    @Operation(summary = "Get all audit trails.", description = "Returns all audit trails")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type=SchemaType.ARRAY,implementation = AuditTrail.class)))
    public Response audit(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response.ok(new ResponseMessage("Success.", auditService.getAllAuditTrails())).build();
        }
        throw new WebApplicationException("Unauthorized", 403);
        
    }

    @GET
    @Path("authTrail")
    @Transactional
    @Operation(summary = "Get all auth trails.", description = "Returns all auth trails")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type=SchemaType.ARRAY,implementation = AuditTrail.class)))
    public Response auth(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response.ok(new ResponseMessage("Success.", auditService.getAllAuthTrails())).build();
        }
        throw new WebApplicationException("Unauthorized", 403);
        
    }

}
