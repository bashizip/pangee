/*
 * Copyright 2017 bashizip.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grew.filter;

import java.io.IOException;
import javax.ejb.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author bashizip
 */
@Singleton
@Provider
public class CorsFilter implements ContainerResponseFilter {

    final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        final int ACCESS_CONTROL_MAX_AGE_IN_SECONDS = 12 * 60 * 60;
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, Content-Type, Accept, Authorization");
        headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_IN_SECONDS);

    }

}
