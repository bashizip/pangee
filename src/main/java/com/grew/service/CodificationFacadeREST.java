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
package com.grew.service;

import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.grew.control.FieldManager;
import com.grew.model.codification.FieldDoc;

/**
 *
 * @author bashizip
 */
@Path("codes")
public class CodificationFacadeREST {

    FieldManager cm = new FieldManager();

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createCodes() {
        List<FieldDoc> list = cm.findAll();
        return Response.ok(list).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("init")
    public Response createCodes(List<FieldDoc> list) {

        for (FieldDoc c : list) {
            cm.create(c);
        }
        return Response.created(URI.create("/codes")).build();

    }

}
