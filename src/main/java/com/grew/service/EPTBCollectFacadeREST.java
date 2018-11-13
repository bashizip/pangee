/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.service;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.grew.process.ProcessEngineManager;
import com.grew.process.ProcessErrorResponse;
import com.grew.process.TaskWrapper;
import com.grew.security.Secured;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.UUIDGenerator;

/**
 *
 * @author bash
 */
@Api(value = "Custom")
@Stateless
@Path("eptb")
public class EPTBCollectFacadeREST {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @EJB
    ProcessEngineManager processManager;

    @EJB
    TaskListFacadeREST taskListFacadeREST;

    @ApiOperation(value = "Collecter des données mobiles et demarrer un workflow")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("collect")
    public Response collectDataFromMobiles(String values) throws URISyntaxException, IOException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        String execId = null;
        
        //TODO check duplicate localId

        try {
            //TODO check if here is a process running on entity
            execId = processManager.start("PTB", "BusinessKeyInternal." + UUIDGenerator.nextID());

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessErrorResponse("Not found", "Process Definition not found")).build();
        }

        Response res = taskListFacadeREST.findByExectionId(execId);

        List<TaskWrapper> tasks = (List<TaskWrapper>) res.getEntity();

        TaskWrapper task = tasks.get(0);

        HashMap valuesMap = ExecutionUtils.jsonStringToMapValues(values);
        if (valuesMap == null) {
            valuesMap = new HashMap();
        }

        valuesMap.put("synced", true);
        taskListFacadeREST.getFormData(task.getId()); //user is assigned
        taskListFacadeREST.validateTask(values, task.getId()); // validate siasie

        if (values == null) {
            values = "{}";
        }

        valuesMap.put("cbPrinted", "OUI");
        res = taskListFacadeREST.findByExectionId(execId);
        tasks = (List<TaskWrapper>) res.getEntity();
        task = tasks.get(0);  // next task ( cb printed )
        taskListFacadeREST.getFormData(task.getId()); //user is assigned
        res = taskListFacadeREST.validateTask(new Gson().toJson(valuesMap), task.getId()); // validate cb printed

        return res;
    }

}
