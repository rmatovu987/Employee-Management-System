package com.employeemanager.controller.services;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.employeemanager.configuration.security.JwtUtils;
import com.employeemanager.controller.services.payload.LoginRequest;
import com.employeemanager.controller.services.payload.SignupRequest;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.EmailServices;
import com.employeemanager.utilities.PositionEnum;

import io.quarkus.elytron.security.common.BcryptUtil;

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
    public void signup(SignupRequest request) {

        String password = BcryptUtil.bcryptHash(request.password);

        Employee employee = new Employee(request.firstname, request.lastname, request.othername,
                request.nationalIdNumber, request.phoneNumber, request.dateOfBirth, request.email,
                PositionEnum.MANAGER.toString());
        employee.password = password;
        employee.persist();

    }

    public String login(LoginRequest request) {

        Employee employee = Employee.findByEmail(request.email);
        if (employee == null)
            throw new WebApplicationException("Invalid credentials", 404);

        Employee login = Employee.login(request.email, request.password);
        if (login == null)
            throw new WebApplicationException("Invalid credentials", 404);

        return jwtUtils.generateJwtToken(employee.email);
    }

    public String resetPasswordToken(String email) {
        Employee validUser = Employee.findByEmail(email);
        if (validUser == null) {
            throw new WebApplicationException("User doesn't exist!", 404);
        }

        String password = randomString(6);
        String genpassword = BcryptUtil.bcryptHash(password);

        validUser.password = genpassword;

        emailService.passwordreset(validUser.email,
                validUser.firstname + " " + validUser.lastname, password);

        return "Please check your email for new credentials!";
    }

    private String randomString(int length) {

		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomLimitedInt = leftLimit + (new Random().nextInt() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		return buffer.toString();
	}
}
