package com.employeemanager.controller.endpoints;

import java.security.Principal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.employeemanager.configuration.handler.ResponseMessage;
import com.employeemanager.controller.services.EmployeeService;
import com.employeemanager.controller.services.payload.EmployeeRequest;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.PositionEnum;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("employee")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Employees", description = "Manage employees")
@SecurityRequirement(name = "jwt", scopes ={})
@SecurityScheme(securitySchemeName = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat="jwt")
public class EmployeeController {

    @Inject
    EmployeeService employeeService;

    @POST
    @Transactional
    @Operation(summary = "Save a new employee", description = "This will create a new employee.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class)))
    @APIResponse(description = "Employee with the same emails already exists", responseCode = "409")
    public Response create(EmployeeRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response
                    .ok(new ResponseMessage("Employee saved successfully", employeeService.create(request, employer)))
                    .build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

    @PUT
    @Path("id")
    @Transactional
    @Operation(summary = "Update an employee's details", description = "This will update an employee's details.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class)))
    @APIResponse(description = "Employee with the provided id does not exist", responseCode = "404")
    public Response update(@PathParam("id") Long id, EmployeeRequest request, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response.ok(
                    new ResponseMessage("Employee updated successfully", employeeService.update(id, request, employer)))
                    .build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

    @PUT
    @Path("id")
    @Transactional
    @Operation(summary = "Suspend an employee", description = "This will suspend an employee.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class)))
    @APIResponse(description = "Employee with the provided id does not exist", responseCode = "404")
    public Response suspend(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response
                    .ok(new ResponseMessage("Employee suspended successfully", employeeService.suspend(id, employer)))
                    .build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

    @PUT
    @Path("id")
    @Transactional
    @Operation(summary = "Activate an employee", description = "This will activate an employee.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class)))
    @APIResponse(description = "Employee with the provided id does not exist", responseCode = "404")
    public Response activate(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response
                    .ok(new ResponseMessage("Employee activated successfully", employeeService.activate(id, employer)))
                    .build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

    @GET
    @Transactional
    @Operation(summary = "Search all employees", description = "This will search all employees.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = Employee.class)))
    public Response search(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname, @QueryParam("email") String email,
            @QueryParam("phoneNumber") String phoneNumber, @QueryParam("code") String code, @QueryParam("position") String position,
            @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String emailEmp = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(emailEmp);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response.ok(new ResponseMessage("Employees fetched successfully",
                    employeeService.search(firstname, lastname, email, position, phoneNumber, code))).build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

    @DELETE
    @Path("id")
    @Transactional
    @Operation(summary = "Delete an employee", description = "This will delete an employee.")
    @APIResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class)))
    @APIResponse(description = "Employee with the provided id does not exist", responseCode = "404")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String email = caller == null ? "anonymous" : caller.getName();

        Employee employer = Employee.findByEmail(email);
        if (employer.position.equals(PositionEnum.MANAGER.toString())) {
            return Response
                    .ok(new ResponseMessage("Employee deleted successfully", employeeService.delete(id, employer)))
                    .build();
        }
        throw new WebApplicationException("Unauthorized", 403);
    }

}
