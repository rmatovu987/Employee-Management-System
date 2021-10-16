package com.employeemanager.configuration.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * An AuthenticationMechanism that validates a caller based on a MicroProfile
 * JWT bearer token
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class CustomAwareJWTAuthMechanism implements HttpAuthenticationMechanism {

    protected static final String COOKIE_HEADER = "Cookie";
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String BEARER = "Bearer";

    @Inject
    JWTAuthContextInfo authContextInfo;

    @Inject
    JwtUtils jwtUtils;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {

        HttpServerRequest request = context.request();

        if (request.path().startsWith("/server-api/auth") 
            || request.path().contains("/swagger")
            || request.path().contains("/file")) {
            return Uni.createFrom().optional(Optional.empty());

        } else {

            String jwt = null;

            String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

            if (headerAuth.startsWith("Bearer")) {
                jwt = headerAuth.replaceFirst("Bearer ", "");
            }

            if (jwt != null) {
                if (jwtUtils.validateJwtToken(jwt)) {

                    String username = jwtUtils.getEmployeeFromJwtToken(jwt);

                    Set<String> userroles = new HashSet<>();
                    
                    QuarkusSecurityIdentity identity = QuarkusSecurityIdentity.builder()
                            .setPrincipal(new QuarkusPrincipal(username)).addRoles(userroles).build();

                    return Uni.createFrom().item(identity);

                }

                return Uni.createFrom().failure(new AuthenticationFailedException());

            }

            return Uni.createFrom().failure(new AuthenticationFailedException());

        }
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        ChallengeData result = new ChallengeData(HttpResponseStatus.UNAUTHORIZED.code(),
                HttpHeaderNames.WWW_AUTHENTICATE, "Bearer {token}");
        return Uni.createFrom().item(result);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.singleton(TokenAuthenticationRequest.class);
    }

    @Override
    public HttpCredentialTransport getCredentialTransport() {

        final String tokenHeaderName = authContextInfo.getTokenHeader();

        if (AUTHORIZATION_HEADER.equals(tokenHeaderName)) {
            return new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, BEARER);

        }

        return null;
    }

}
