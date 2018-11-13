/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import io.swagger.annotations.ApiOperation;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.grew.formserver.FormSchemaServer;
import com.grew.formserver.FormServiceEntityBase;
import com.grew.model.JsonSchemaDoc;
import com.grew.process.ProcessCustomResponse;

/**
 *
 * @author pbash
 */
@Path("forms")
@Stateless
public class FormsFacadeREST {

    @EJB
    FormSchemaServer jsonSchemaServer;

    @GET
    @ApiOperation(value = "Formulaire par sa formKey")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{formKey}")
    public Response getFormSchemaByKey(@PathParam("formKey") String formKey) {

        Response response = null;

        JsonSchemaDoc taskformSchema = jsonSchemaServer.getJsonSchemaByFormKey(formKey);
        if (taskformSchema == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessCustomResponse("Form key not found", formKey)).build();
        }
        response = Response.ok(taskformSchema).build();

        return response;
    }

}
