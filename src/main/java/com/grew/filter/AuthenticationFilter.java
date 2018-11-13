/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import com.grew.security.Secured;
import com.grew.security.SecuritySettings;

/**
 *
 * @author bashizip
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Context
    HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the Authorization header from the request
        String authorizationHeader
                = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                        .build());
    }

    private void validateToken(String token) throws Exception {

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(SecuritySettings.getTokenSigningKey())
                .parseClaimsJws(token);

        //String[] scope = (String[]) claims.getBody().get("scope");
        //perform authorization if needed
        System.out.println("ID: " + claims.getBody().getId());
        System.out.println("Subject: " + claims.getBody().getSubject());
        System.out.println("Issuer: " + claims.getBody().getIssuer());
        System.out.println("Expiration: " + claims.getBody().getExpiration());
        System.out.println("Scope: " + claims.getBody().get("scope"));
        
        headers.getRequestHeaders().add("usergroups", claims.getBody().get("scopes").toString());
        headers.getRequestHeaders().add("username", claims.getBody().getSubject());
    }
}
