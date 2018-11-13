package com.grew.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import com.grew.control.ProcessManager;
import com.grew.model.ProcessDoc;
import com.grew.model.RollbackBean;
import com.grew.process.HistoricTaskWrapper;
import com.grew.process.ProcessCustomResponse;
import com.grew.process.ProcessEngineManager;
import com.grew.process.ProcessErrorResponse;
import com.grew.process.TaskWrapper;
import com.grew.security.Secured;
import com.grew.utils.StringUtilities;
import com.grew.utils.UUIDGenerator;

/**
 *
 * @author bashizip
 */
@Api(value = "Processes")
@Stateless
@Path("processes")
public class ProcessFacadeREST {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @EJB
    ProcessEngineManager processManager;

    @EJB
    TaskListFacadeREST taskListFacadeREST;

    static boolean DEBUG = true;

    @GET
    @Secured
    @ApiOperation(value = "Demarrer un processus")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("start/{processKey}")
    public Response startNewProcess(@PathParam("processKey") String processKey) throws URISyntaxException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        String execId = null;

        try {
            //TODO check if here is a process running on entity
            execId = processManager.start(processKey, "BusinessKeyTest." + UUIDGenerator.nextID());

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessErrorResponse("Not found", "Process Definition not found")).build();
        }
        Response res = taskListFacadeREST.findByExectionId(execId);

        return res;
    }

    @GET
    @Secured
    @ApiOperation(value = "Liste de toutes le procedures disponibles")
    @Authorization("Bearer <Token>")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listProcess() throws URISyntaxException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        ProcessManager pm = new ProcessManager();

        groups = groups.replace("[", "").replace("]", "");

        String[] groupArr = groups.split(",");
        String[] trimmedArray = new String[groupArr.length];
        for (int i = 0; i < groupArr.length; i++) {
            trimmedArray[i] = groupArr[i].trim();
        }

        List<ProcessDoc> list = pm.findAllByGroup(trimmedArray);

        List<ResourceLink> links;

        for (ProcessDoc pdoc : list) {

            links = new ArrayList<>();
            ResourceLink rs1 = new ResourceLink("self", "GET", uriInfo.getBaseUri() + "processes/" + pdoc.getId());
            ResourceLink rs2 = new ResourceLink("start-instance", "GET", uriInfo.getBaseUri() + "processes/start/" + pdoc.getDefinitionKey());

            links.add(rs1);
            links.add(rs2);

            pdoc.setLinks(links);
        }

        return Response.ok(list).build();
    }

    @PUT
    @Secured
    @ApiOperation(value = "Clôturer un processus manuellement")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cancel/{processIntanceId}")
    public Response abortProcessInstanceExecution(@PathParam("processIntanceId") String processIntanceId, String reason) throws URISyntaxException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        String reasonComp = "Aborted by user @" + username + "\nReason: " + reason;

        try {

            processManager.getProcessEngine().getRuntimeService().deleteProcessInstance(processIntanceId, reasonComp);

            //TODO suppression des données temporaires (task; dataflow, etc..)
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessErrorResponse("Not found", "Process instance not found")).build();
        }
        Response res = Response.ok().entity(new ProcessCustomResponse(reasonComp, "User aborted the process execution")).build();

        return res;
    }

    @GET
    @Secured
    @ApiOperation(value = "Lister les activités d'un processus")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{processIntanceId}/history/tasks")
    public Response activities(@PathParam("processIntanceId") String processIntanceId) {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");
        List<HistoricTaskWrapper> tasksWrappers = new ArrayList<>();

        if (!groups.contains("wf_admin")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ProcessErrorResponse("Interdit", "L'utilisateur doit avoir le role d'adminstrateur de Workflow"))
                    .build();
        }

        try {

            // List<Task> tasks = processManager.getProcessEngine().getTaskService().createTaskQuery().processInstanceId(processIntanceId).list();
            List<HistoricTaskInstance> htasks = processManager.getProcessEngine().getHistoryService()
                    .createHistoricTaskInstanceQuery().finished().orderByHistoricTaskInstanceEndTime().desc()
                    .processInstanceId(processIntanceId).list();

            for (HistoricTaskInstance t : htasks) {
                tasksWrappers.add(new HistoricTaskWrapper(t));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessErrorResponse("Error", e.getMessage())).build();
        }
        Response res = Response.ok().entity(tasksWrappers).build();

        return res;
    }

    @PUT
    @Secured
    @ApiOperation(value = "Modifier le flow d'execution d'un processus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("exec/rollback/{processIntanceId}")
    public Response rollback(@PathParam("processIntanceId") String processIntanceId, RollbackBean rbb) throws URISyntaxException {

        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        if (!groups.contains("wf_admins")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ProcessErrorResponse("Interdit", "L'utilisateur doit avoir le role d'adminstrateur de Workflow"))
                    .build();
        }

        String reasonComp = "Rolled back by user @" + username;

        try {

            processManager.getProcessEngine().getRuntimeService().createProcessInstanceModification(processIntanceId)
                    .startBeforeActivity(rbb.getStartBefore())
                    .cancelAllForActivity(rbb.getCancelFrom())
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity(new ProcessErrorResponse("Not found", "Process instance not found")).build();
        }

        Response res = Response.ok().entity(new ProcessCustomResponse(reasonComp, "Reason: " + rbb.getReason())).build();

        return res;
    }

    @GET
    @Secured
    @ApiOperation(value = "Trouver une procedures par son nom unique")
    @Authorization("Bearer <Token>")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("byname/{name}")
    public Response findByName(@PathParam("name") String name) {

        ProcessManager pm = new ProcessManager();
        ProcessDoc pd = pm.findByDefinitionKey(name);

        if (pd == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(pd).build();

    }

}
