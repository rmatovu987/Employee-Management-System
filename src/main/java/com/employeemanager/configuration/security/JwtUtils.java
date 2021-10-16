package com.employeemanager.configuration.security;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.employeemanager.domain.Employee;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtSignatureException;

@ApplicationScoped
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Inject
	JWTParser parser;

	@Inject
	JsonWebToken jwt;

	private String jwtSecret = "ThWmZq3t6w9z$C&F)J@NcRfUjXn2r5u7x!A%D*G-KaPdSgVkYp3s6v9y/PDmdk#%Do20FosJ@NcRfUjXn2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$B&E)";

	private int jwtExpirationMs = 86400000;

	public String generateJwtToken(String email) {
		Employee user = Employee.findByEmail(email);

		return Jwt.subject(user.email).issuedAt(new Date().toInstant())
				.expiresAt(new Date((new Date()).getTime() + jwtExpirationMs).toInstant())
				.upn(user.firstname + " " + user.lastname).issuer("Employee Management Auth Server").signWithSecret(jwtSecret);

	}

	public String getEmployeeFromJwtToken(String token) {

		try {
			jwt = parser.verify(token, jwtSecret);

			return jwt.getSubject();

		} catch (ParseException e) {
			logger.error("Parse Exception: {}", e.getMessage());
			throw new WebApplicationException("Access Denied.", 401);

		} catch (Exception e) {
			logger.error("Null Exception: {}", e.getMessage());
			throw new WebApplicationException("Access Denied.", 401);
		}

	}

	public boolean validateJwtToken(String authToken) {

		try {
			jwt = parser.verify(authToken, jwtSecret);

			return true;

		} catch (JwtSignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
			return false;

		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
			return false;

		} catch (ParseException e) {
			logger.error("Parse Exception: {}", e.getMessage());
			return false;

		} catch (Exception e) {
			logger.error("Null Exception: {}", e.getMessage());
			return false;
		}

	}

}
