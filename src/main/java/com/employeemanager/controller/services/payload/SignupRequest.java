package com.employeemanager.controller.services.payload;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class SignupRequest {

    @NotNull
    @Schema(type = SchemaType.STRING, required = true, example = "Richard")
    public String firstname;

    @NotNull
    @Schema(type = SchemaType.STRING, required = true, example = "Matovu")
    public String lastname;

    @NotNull
    @Schema(type = SchemaType.STRING, required = false, example = "Martin")
    public String othername;

    @NotNull
    @Schema(type = SchemaType.STRING, required = true, example = "CMN02458948284")
    public String nationalIdNumber;

    @NotNull
    @Schema(type = SchemaType.STRING, required = true, example = "+25479385949")
    public String phoneNumber;

    @NotNull
    @JsonbDateFormat("dd/MM/yyyy")
    @Schema(type = SchemaType.STRING, required = true, example = "06/12/1995")
    public LocalDate dateOfBirth;

    @NotNull
    @Email
    @Schema(type = SchemaType.STRING, required = true, example = "admin@gmail.com")
    public String email;

    @NotNull
    @Schema(type = SchemaType.STRING, required = true, example = "Manager")
    public String position;

    @NotNull
    @Schema(type = SchemaType.STRING, required = true)
    public String password;
}
