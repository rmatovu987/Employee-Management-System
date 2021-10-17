package com.employeemanager.controller.services.payload;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class LoginRequest {

    @Schema(required = true, example = "admin@gmail.com",description = "Must be a valid email")
    public String email;

    @Schema(required = true, example = "123")
    public String password;

}
