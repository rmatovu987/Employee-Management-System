package com.employeemanager.controller.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.employeemanager.auditTrail.AuthTrail;
import com.employeemanager.configuration.security.JwtUtils;
import com.employeemanager.controller.services.payload.LoginRequest;
import com.employeemanager.controller.services.payload.SignupRequest;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.EmailServices;
import com.employeemanager.utilities.PositionEnum;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vertx.core.http.HttpServerRequest;

@ApplicationScoped
public class AuthService {

    @Inject
    JwtUtils jwtUtils;

    @Inject
    EmailServices emailService;

    /**
     * Signup creates a new employee of type Manager
     * 
     * @param request
     */
    public void signup(SignupRequest request, HttpServerRequest httprequest) {
        Employee exists = Employee.findByEmail(request.email);
        if (exists != null)
            throw new WebApplicationException("Employee with email " + request.email + " already exists", 409);

        Employee exists1 = Employee.findByNationalIdNumber(request.nationalIdNumber);
        if (exists1 != null)
            throw new WebApplicationException("Employee with that national id number already exists", 409);

        Employee exists2 = Employee.findByPhoneNumber(request.phoneNumber);
        if (exists2 != null)
            throw new WebApplicationException("Employee with phone number " + request.phoneNumber + " already exists",
                    409);

        if (!Employee.checkPhoneNumberFormat(request.phoneNumber))
            throw new WebApplicationException("Invalid phone number format", 403);

        if (request.nationalIdNumber.length() != 16)
            throw new WebApplicationException("National ID Number must be 16 characters", 403);

        if (ChronoUnit.YEARS.between(LocalDateTime.now(), request.dateOfBirth.atStartOfDay()) >= 18)
            throw new WebApplicationException("You are below the minimum age of 18 years", 403);

        String password = BcryptUtil.bcryptHash(request.password);

        Employee employee = new Employee(request.firstname, request.lastname, request.othername,
                request.nationalIdNumber, request.phoneNumber, request.dateOfBirth, request.email,
                PositionEnum.MANAGER.toString());
        employee.password = password;
        employee.persist();

        emailService.signup(employee.firstname + " " + employee.lastname, request.password, employee.email);

        AuthTrail auth = new AuthTrail(httprequest.remoteAddress().toString(), request.email,
                httprequest.getHeader("User-Agent"), "SIGNUP");
        auth.persist();
    }

    public String login(LoginRequest request, HttpServerRequest httprequest) {

        Employee employee = Employee.findByEmail(request.email);
        if (employee == null)
            throw new WebApplicationException("Invalid credentials", 404);

        Employee login = Employee.login(request.email, request.password);
        if (login == null)
            throw new WebApplicationException("Invalid credentials", 404);

        AuthTrail auth = new AuthTrail(httprequest.remoteAddress().toString(), request.email,
                httprequest.getHeader("User-Agent"), "LOGIN");
        auth.persist();

        return jwtUtils.generateJwtToken(employee.email);
    }

    public String resetPasswordToken(String email, HttpServerRequest httprequest) {
        Employee validUser = Employee.findByEmail(email);
        if (validUser == null) {
            throw new WebApplicationException("User doesn't exist!", 404);
        }

        String password = randomString(6);
        String genpassword = BcryptUtil.bcryptHash(password);

        validUser.password = genpassword;

        emailService.passwordreset(validUser.email, validUser.firstname + " " + validUser.lastname, password);

        AuthTrail auth = new AuthTrail(httprequest.remoteAddress().toString(), email, httprequest.getHeader("User-Agent"), "RESET-PASSWORD");
        auth.persist();

        return "Please check your email for new credentials!";
    }

    private String randomString(int length) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
