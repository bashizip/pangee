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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.TaskAlreadyClaimedException;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import com.grew.control.DataFlowManager;
import com.grew.control.EntityManager;
import com.grew.control.ProcessManager;
import com.grew.control.RapportManager;
import com.grew.control.LocalSettingsManager;
import com.grew.control.SettingsHandler;
import com.grew.control.TaskManager;
import com.grew.control.UserManager;

import com.grew.formserver.FormServiceEntityFromTaskDoc;
import com.grew.formserver.FormSchemaServer;
import com.grew.formserver.FormServiceEntityBase;
import com.grew.formserver.FormServiceEntityFromDataFlow;
import com.grew.model.DataFlowDoc;
import com.grew.model.JsonSchemaDoc;
import com.grew.model.ProcessDoc;
import com.grew.model.ReportDoc;
import com.grew.model.SettingsDoc;
import com.grew.model.TaskDoc;
import com.grew.model.UserDoc;
import com.grew.process.DataFlowDocWrapper;
import com.grew.process.ProcessCustomResponse;
import com.grew.process.ProcessEngineManager;
import com.grew.process.ProcessErrorResponse;
import com.grew.process.ProcessUploadResponse;
import com.grew.process.ReportDataWrapper;
import com.grew.process.TaskWrapper;
import com.grew.process.business.SaveTaskBusiness;
import com.grew.process.business.ValidateTaskBusiness;
import com.grew.security.Secured;
import com.grew.utils.UUIDGenerator;
import com.grew.utils.UploadsUtils;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.JasperCompiler;
import com.grew.utils.PrintingTools;
import com.grew.utils.SettingsUtils;
import com.grew.utils.StreamUtil;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author bashizip
 */
@Api(value = "Tasks")
@Path("usertasks")
@Stateless
public class TaskListFacadeREST {

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders headers;

    @EJB
    ProcessEngineManager processManager;

    @EJB
    FormSchemaServer jsonSchemaServer;

    @EJB
    EntityDocFacadeREST entityDocFacadeREST;

    @EJB
    ValidateTaskBusiness validateTaskBusiness;

    @EJB
    SaveTaskBusiness saveTaskBusiness;

    TaskManager tm = new TaskManager();
    DataFlowManager dfm = new DataFlowManager();
    EntityManager em = new EntityManager();

    public TaskListFacadeREST() {
    }

    public String getBaseUri() {
        String serverURI = uriInfo.getBaseUri().toString();
        return serverURI;
    }

    @GET
    @ApiOperation(value = "Trouver une tâche par son Id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Task records not found"),
        @ApiResponse(code = 500, message = "Internal serve rerror")
    })
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("byId/{taskId}")
    public Response findById(@PathParam("taskId") String taskId) throws URISyntaxException {

        Response response = null;
        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        groups = groups.replace("[", "").replace("]", "");

        System.out.println("Candidate Groups: " + groups);

        List<Task> tasks = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .list();

        if (tasks == null || tasks.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found."))
                    .build();

        }

        boolean assignee = false;
        if (tasks.get(0).getAssignee().equals(username)) {
            assignee = true;
        }

        List<TaskWrapper> tasksWrappers = buildWrapper(tasks, groups, username, assignee);

        response = Response.ok(tasksWrappers).build();

        return response;
    }

    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("byExecution/{executionId}")
    public Response findByExectionId(@PathParam("executionId") String executionId) throws URISyntaxException {

        Response response = null;
        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        System.out.println("Candidate Groups: " + groups);

        if (groups != null) {
            groups = groups.replace("[", "").replace("]", "");
        }

        System.out.println("Candidate Groups: " + groups);

        List<Task> tasks = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .executionId(executionId)
                .list();

        if (tasks == null || tasks.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found."))
                    .build();
        }

        boolean assignee = false;

        String ass = tasks.get(0).getAssignee();

        if (ass != null) {
            if (ass.equals(username)) {
                assignee = true;
            }
        }
        List<TaskWrapper> tasksWrappers = buildWrapper(tasks, groups, username, assignee);

        response = Response.ok(tasksWrappers).build();

        return response;
    }

    /**
     *
     * @return @throws URISyntaxException
     */
    @GET
    @ApiOperation(value = "Liste des tâches dont l'utilisateur est candidate")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "User's token", required = true, dataType = "string", paramType = "header"),})
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/candidate")
    public Response candidates() throws URISyntaxException {

        Response response = null;
        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        System.out.println("Candidate Groups: " + groups);

        groups = groups.replace("[", "").replace("]", "");

        System.out.println("Candidate Groups: " + groups);

        List<String> groupsList = new ArrayList<>();
        groupsList.addAll(Arrays.asList(groups.split(",")));
        List<Task> allTasks = new ArrayList<>();

        for (String group : groupsList) {

            List<Task> tasks = processManager.getProcessEngine()
                    .getTaskService()
                    .createTaskQuery()
                    .taskCandidateGroup(group.trim())
                    .initializeFormKeys()
                    .list();

            allTasks.addAll(tasks);

        }

        List<TaskWrapper> tasksWrappers = buildWrapper(allTasks, groups, username, false);

        response = Response.ok(tasksWrappers).build();

        return response;
    }

    @GET
    @ApiOperation(value = "Liste des tâches dont l'utilisateur est assignee")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/assignee")
    public Response assigees() throws URISyntaxException {

        Response response = null;
        String username = headers.getHeaderString("username");
        String groups = headers.getHeaderString("usergroups");

        groups = groups.replace("[", "").replace("]", "");

        System.out.println("Candidate Groups: " + groups);

        List<Task> tasks = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskAssignee(username)
                .initializeFormKeys() //f*cking important!
                .list();

        List<TaskWrapper> tasksWrappers = buildWrapper(tasks, groups, username, true);

        response = Response.ok(tasksWrappers).build();

        return response;
    }

    /**
     *
     * @param taskId
     * @return
     */
    @GET
    @ApiOperation(value = "Formulaire associé a une tâche")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("formSchema/{taskId}")
    public Response getFormData(@PathParam("taskId") String taskId) {

        Response response = null;
        String username = headers.getHeaderString("username");

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .initializeFormKeys()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }

        FormServiceEntityBase fse = null;

        String formKey = task.getFormKey();

        JsonSchemaDoc taskformSchema = jsonSchemaServer.getJsonSchemaByFormKey(formKey);

        // EntityDoc ed = null;
        //ed = em.findByProcessInstanceId(task.getExecutionId());
        DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());
        TaskDoc taskformData = tm.findByTaskId(taskId);

        /**
         * Si le wf n'a pas encore avancé d'un seul pas, le dataFlow n'existe
         * pas, retourner les données du TaskDoc Si le wf a avancé et que la
         * derniere task du flow est la meme que la task actuelle, retourner
         * toujours les données de la TaskDoc Si le wf a avancé d'un pas de plus
         * avant validation explicite, retourner les données du dataFlow Si le
         * wf a avancé et après validation, retourner les données du dflow
         */
        if (flowDoc == null) {

            if (taskformData == null) {
                taskformData = new TaskDoc();
            }

            fse = new FormServiceEntityFromTaskDoc(taskformSchema, taskformData);

        } else {
            fse = new FormServiceEntityFromDataFlow(taskformSchema, new DataFlowDocWrapper(flowDoc));
        }

        if (task.getAssignee() == null) {

            try {

                processManager.getProcessEngine()
                        .getTaskService().claim(taskId, username);

            } catch (TaskAlreadyClaimedException e) {

                response = Response.status(Response.Status.FORBIDDEN).
                        entity(new ProcessCustomResponse("Forbiden", "Task Already Claimed"))
                        .build();

                return response;
            }

        }

        response = Response.ok(fse).build();

        return response;
    }

    /**
     *
     * @param taskId
     * @return
     */
    @PUT
    @ApiOperation(value = "Se desassigner une tâche. Consequence : La tache rentre dans la zone candidate pour tous les autres utilisateurs du group")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("unclaim/{taskId}")
    public Response unclaim(@PathParam("taskId") String taskId) {

        Response response = null;
        String username = headers.getHeaderString("username");

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }

        if (task.getAssignee() == null) {
            return Response.status(Response.Status.NOT_MODIFIED)
                    .entity(new ProcessErrorResponse("Not modified", "The task has no assignee")).build();
        }

        if (!task.getAssignee().equals(username)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ProcessErrorResponse("UNAUTHORIZED", "The user is not the assignee")).build();
        }

        processManager.getProcessEngine()
                .getTaskService().claim(taskId, null);

        response = Response.ok(new ProcessCustomResponse("Done", "Task no longer assigned to " + username)).build();

        return response;
    }

    @PUT
    @ApiOperation(value = "Se 'desassigner' de toutes les taches")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("unclaim/all")
    public Response unclaimAall() {

        Response response = null;
        String username = headers.getHeaderString("username");
        List<Task> tasks = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskAssignee(username).list();

        if (tasks == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }
        for (Task task : tasks) {
            if (task.getAssignee() == null) {
                continue;
            }

            if (!task.getAssignee().equals(username)) {
                continue;
            }

            processManager.getProcessEngine()
                    .getTaskService().claim(task.getId(), null);
        }
        response = Response.ok(new ProcessCustomResponse("Done", "Task successfully unclaimed by " + username)).build();

        return response;
    }

    /**
     *
     * @param valuesString
     * @param taskId
     * @return
     */
    @POST
    @ApiOperation(value = "Enregistrer les données d'une tâche")
    @Authorization("Bearer <Token>")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("submit/save/{taskId}")
    public Response saveTaskState(String valuesString, @PathParam("taskId") String taskId) {

        //values is a json Object containig all the values
        Response response = null;
        String username = headers.getHeaderString("username");

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId).singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found."))
                    .build();
        }

        if (!task.getAssignee().equals(username)) {
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity(new ProcessErrorResponse("Not found", "User is not assignee"))
                    .build();
        }

        if (valuesString == null) {
            valuesString = new String("{}");
        }
        //create or update the TaskDoc
        HashMap valuesMap = ExecutionUtils.jsonStringToMapValues(valuesString);
        if (valuesMap == null) {
            valuesMap = new HashMap();
        }

        valuesMap.put(ExecutionUtils.BUSINESS_KEY_VAR, getBusinessKey(task));

        processManager.getProcessEngine()
                .getRuntimeService()
                .setVariables(task.getProcessInstanceId(), valuesMap);

        Response resX = saveTaskBusiness.createOrUpdateTaskDoc(task, username, valuesString);

        if (resX.getStatus() == 500 || resX.getStatus() == 400) {
            return resX;
        }

        response = Response.ok(new ProcessCustomResponse("info", "task data updated successfuly")).build();

        return response;
    }

    /**
     *
     * @param values
     * @param taskId
     * @return
     * @throws IOException
     */
    @POST
    @ApiOperation(value = "Valider la tâche et faire avancer le workflow")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("submit/validate/{taskId}")
    public Response validateTask(String values, @PathParam("taskId") String taskId) throws IOException {

        Response response = null;
        String username = headers.getHeaderString("username");

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId).singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }

        if (task.getAssignee() == null || !task.getAssignee().equals(username)) {
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity(new ProcessErrorResponse("Precodition failed", "User is not assignee")).build();
        }

        if (values == null) {
            values = new String("{}");
        }
        //create or update the TaskDoc
        HashMap valuesMap = ExecutionUtils.jsonStringToMapValues(values);
        if (valuesMap == null) {
            valuesMap = new HashMap();
        }
        valuesMap.put(ExecutionUtils.BUSINESS_KEY_VAR, getBusinessKey(task));

        processManager.getProcessEngine()
                .getRuntimeService()
                .setVariables(task.getProcessInstanceId(), valuesMap);

        //save then validate
        saveTaskBusiness.createOrUpdateTaskDoc(task, username, values);

        Response resX = validateTaskBusiness.execute(task, username, values);

        if (resX.getStatus() == 500 || resX.getStatus() == 400) {
            return resX;
        }

        //complete the task
        processManager.getProcessEngine().getTaskService().complete(taskId);

        response = Response.ok(new ProcessCustomResponse("info", "task validated")).build();

        return response;
    }

    
    /**
     * 
     * @param tasks
     * @param groups
     * @param user
     * @param isAssignee
     * @return 
     */
    private List<TaskWrapper> buildWrapper(List<Task> tasks, String groups, String user, boolean isAssignee) {

        List<TaskWrapper> tasksWrappers = new ArrayList<>();

        for (Task t : tasks) {

            ProcessDefinition pdef
                    = processManager.getProcessEngine()
                    .getRepositoryService()
                    .createProcessDefinitionQuery()
                    .processDefinitionId(t.getProcessDefinitionId())
                    .list().get(0);

            HistoricProcessInstance hpi = processManager.
                    getProcessEngine().
                    getHistoryService().
                    createHistoricProcessInstanceQuery().
                    processInstanceId(t.getProcessInstanceId())
                    .singleResult();

            Date d = hpi.getStartTime();

            try {

                ResourceLink rs0 = new ResourceLink("self", "GET", uriInfo.getBaseUri() + "usertasks/" + t.getId());
                ResourceLink rs1 = new ResourceLink("saveTask", "POST", uriInfo.getBaseUri() + "usertasks/submit/save/" + URLEncoder.encode(t.getId(), "UTF-8"));
                ResourceLink rs2 = new ResourceLink("validateTask", "POST", uriInfo.getBaseUri() + "usertasks/submit/validate/" + URLEncoder.encode(t.getId(), "UTF-8"));
                ResourceLink rs3 = new ResourceLink("formSchema", "GET", uriInfo.getBaseUri() + "usertasks/formSchema/" + t.getId());

                TaskWrapper tw = new TaskWrapper(t);
                tw.setProcessDisplayName(pdef.getName());
                tw.setBusinessKey(getBusinessKey(t));
                tw.setCandidateGroups(groups);
                tw.setProcessInstanceCreateTime(d);

                if (isAssignee) {
                    tw.setAssignee(user);
                }

                String processKey = pdef.getKey();

                tw.setProcessDefinitionKey(processKey);
                ProcessManager pm = new ProcessManager();

                ProcessDoc pdoc = pm.findByDefinitionKey(processKey);

                String[] keyPropos = pdoc.getEntityKeys();
                Map values = null;

                TaskDoc taskDoc = null;
                DataFlowDoc dataFlowDoc = null;

                if (keyPropos == null) {
                    keyPropos = new String[]{};
                }

                dataFlowDoc = dfm.findByProcessInstanceId(tw.getProcessInstanceId());

                taskDoc = tm.findByTaskId(tw.getId());

                if (dataFlowDoc != null) {
                    values = dataFlowDoc.getCurrent_entity();
                } else if (taskDoc != null) {
                    values = taskDoc.getValues();
                }

                for (String s : keyPropos) {
                    String key = s;
                    if (values == null) {
                        values = new HashMap();
                    }
                    Object value = ((Map) values.get("values"));
                    if (value == null | values.isEmpty()) {
                        value = values.get(key);
                    } else {

                        value = ((Map) value).get(key);
                    }
                    tw.getKeyEntityValues().put(key, value);
                    tw.setKeyEntityValues(tw.getKeyEntityValues());
                }

                List<ResourceLink> links = new ArrayList<>();

                links.add(rs0);
                links.add(rs1);
                links.add(rs2);
                links.add(rs3);
                tw.setLinks(links);

                tasksWrappers.add(tw);

            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return tasksWrappers;
    }

    @GET
    @ApiOperation(value = "Retourne le fichier PDF complilé , pour impression, via sa clé {printKey}")
    @Secured
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("print/{taskId}/{printKey}")
    public Response getCurrentFileToPrint(
            @PathParam("taskId") String taskId,
            @PathParam("printKey") String printKey) throws FileNotFoundException {
        String username = headers.getHeaderString("username");
        //Todo : recuperer le json a imprimer dans le Task ou le dataflow s'il existe,
        //puis le balancer au jasper correspondant s'il existe aussi puis tout mettre en pdf
        //puis retourner le pdf
        //NB. Si cest la  premiere tache,le dataflow nexiste pas alors retourner le taskdoc s'il existe
        Map<Object, Object> currentValues = null;

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        TaskDoc tdoc = null;

        UserManager um = new UserManager();
        List<UserDoc> users = um.findByUsername(username);
        UserDoc userDoc = null;

        if (users != null) {
            userDoc = users.get(0);
        }

        SettingsHandler sh = new SettingsHandler();
        SettingsDoc sdoc = sh.getLocalnstance();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }

        DataFlowDoc df = dfm.findByProcessInstanceId(task.getProcessInstanceId());

        if (df == null) {
            tdoc = tm.findByTaskId(taskId);
            if (tdoc != null) {
                currentValues = tdoc.getValues();
            }
            // files = tdoc.getFiles();

        } else {
            currentValues = df.getPending_values();

        }

        if (currentValues == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ProcessErrorResponse("Bad request", "No values")).build();
        }

        Map data = new HashMap();
        Map<String, Object> params = new HashMap<>();

        for (Map.Entry<Object, Object> entry : currentValues.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            data.put(key.toString(), value);
        }

        Map<Object, Object> settings = new LocalSettingsManager().getValues();

        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            params.put(key.toString(), value);
        }

        params.put("user", userDoc);

        RapportManager rapportManager = new RapportManager();
        ReportDoc rdoc = rapportManager.findByReportKey(printKey);

        File pdfFile = null;

        try {

            pdfFile = JasperCompiler.getCompiledPdfFile(rdoc.getJasperFile(), data, params);

        } catch (JRException | IOException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "File not found.")).build();
        }

        String fileId = printKey.replace(":", "/");

        String fileName = null;
        try {
            fileName = UploadsUtils.uploadFile(new FileInputStream(pdfFile), taskId, getSite(), fileId, getFileSeq(), "pdf");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        String filePath = getCaptureURI(fileName);
        System.out.println(filePath);
        rdoc.setPdf(filePath);

        ReportDataWrapper dataWrapper = new ReportDataWrapper();

        dataWrapper.setRapportDoc(rdoc);
        dataWrapper.setUser(userDoc);
        dataWrapper.setAppSettings(sdoc);

        dataWrapper.setValues(params);
        String finalPath = rdoc.getPdf();

        return Response.ok(finalPath).build();

    }

    @GET
    @ApiOperation(value = "Retourne le fichier via son id")
    @Path("files/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response findFile(@PathParam("id") String fileName) {

        Response out = null;
        try {

            out = UploadsUtils.findFile(fileName);

        } catch (ParseException ex) {
            out = Response.status(Response.Status.BAD_REQUEST).entity(new ProcessUploadResponse("File name format invalid", "Bad Request")).build();
        } catch (FileNotFoundException ex) {
            out = Response.status(Response.Status.BAD_REQUEST).entity(new ProcessUploadResponse("File not found", fileName)).build();
        }
        return out;
    }

    @GET
    @ApiOperation(value = "Retourne le fichier encodé en base 64")
    @Path("files/{id}/base64")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response findFileBase64(@PathParam("id") String fileName) throws IOException {

        File f = null;
        Response res;
        try {

            f = UploadsUtils.findFile64(fileName);

            // encode to base64
            byte[] data = Base64.getEncoder().encode(IOUtils.toByteArray(new FileInputStream(f)));

            // prepare response
            return Response
                    .ok(data)
                    //.header("Content-Disposition", "inline; filename = \"" + fileName + "\"")
                    .build();

        } catch (ParseException ex) {
            res = Response.status(Response.Status.BAD_REQUEST).entity(new ProcessUploadResponse("File name format invalid", "Bad Request")).build();
        } catch (FileNotFoundException ex) {
            res = Response.status(Response.Status.BAD_REQUEST).entity(new ProcessUploadResponse("File not found", fileName)).build();
        }
        return res;
    }

    public String getSite() {
        LocalSettingsManager sm = new LocalSettingsManager();
        SettingsDoc sd = sm.findInstance();
        String siteId = sd.getValues().get(SettingsUtils.site).toString();
        return siteId;
    }

    private String getBusinessKey(Task t) {
        //get the business key from the execution
        Object businessKeyObject = processManager.getProcessEngine()
                .getRuntimeService()
                .getVariable(t.getExecutionId(), ExecutionUtils.BUSINESS_KEY_VAR);

        String businessKey = "";

        if (businessKeyObject != null) {
            businessKey = businessKeyObject.toString();

        }
        return businessKey;
    }

    /**
     *
     * @param input
     * @param taskId
     * @param values
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "Uploader un  ou plusieurs fichiers scannés")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{taskId}/upload/scan")
    @Secured
    public Response doUploadScan(MultipartFormDataInput input, @PathParam("taskId") String taskId) throws IOException {
        //aaaammjj_name_flwId_seq
        Response res = null;
        String username = headers.getHeaderString("username");
        Map<String, List<InputPart>> formParts = input.getFormDataMap();

        String fileBusinessName = null;
        String fileType = "jpg";//default
        String captUri = null;

        List<InputStream> inputStreams = new ArrayList<>();

        for (int i = 1; i < formParts.size(); i++) {

            List<InputPart> filesPart = formParts.get("file" + i);
            for (InputPart inputPart : filesPart) {
                InputStream isPart = inputPart.getBody(InputStream.class, null);
                inputStreams.add(isPart);
            }

        }

        List<InputPart> fNameBusinessPart = formParts.get("fieldName");

        for (InputPart inputPart : fNameBusinessPart) {
            try {
                MultivaluedMap<String, String> mheaders = inputPart.getHeaders();
                fileBusinessName = inputPart.getBody(String.class, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // File tmp = StreamUtil.stream2file(sis, ".pdf");
        // System.out.println(tmp.getAbsolutePath());
        String finalName = null;
        List<File> files = new ArrayList<>();

        for (InputStream isi : inputStreams) {

            File jpeg = StreamUtil.stream2file(isi, ".jpg");
            File tmp = PrintingTools.imageToPDF(jpeg);

            System.out.println(tmp.getAbsolutePath());

            files.add(tmp);

        }

        File merged = PrintingTools.mergePdfFiles(files);
        finalName = UploadsUtils.uploadFile(new FileInputStream(merged), taskId, getSite(), fileBusinessName, getFileSeq(), "pdf");

        //  sis.close();
        captUri = getCaptureURI(finalName);

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId).singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found."))
                    .build();
        }

        //Attach the doc to the instance of Task
        TaskDoc tdoc = tm.findByTaskId(taskId);

        if (tdoc == null) {
            saveTaskBusiness.createOrUpdateTaskDoc(task, username, "{}");
        }
        tdoc = tm.findByTaskId(taskId);

        String mimeType = StreamUtil.getFileMimeType(merged.getName());
        FileInputStream fis = new FileInputStream((File) findFile(finalName).getEntity());
        tdoc.attachDoc(finalName, mimeType, fis);
        tm.edit(tdoc);

        DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());

        if (flowDoc != null) {
            flowDoc.setLastTaskId(taskId);
            dfm.edit(flowDoc);
        }
        return Response.status(Response.Status.ACCEPTED).
                entity(new ProcessUploadResponse(finalName, captUri)).build();
    }

    @ApiOperation(value = "Uploader un fichier")
    @POST
    @Secured
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{taskId}/upload/")
    public Response doUploadSingle(MultipartFormDataInput input,
            @PathParam("taskId") String taskId) throws IOException {

        //aaaammjj_name_flwId_seq
        Response res = null;
        Response response = null;
        String username = headers.getHeaderString("username");

        Task task = processManager.getProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskId(taskId).singleResult();

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ProcessErrorResponse("Not found", "Task Not Found.")).build();
        }

        if (task.getAssignee() == null || !task.getAssignee().equals(username)) {
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity(new ProcessErrorResponse("Precodition failed: User is not assignee", "User is not assignee")).build();
        }

        Map<String, List<InputPart>> formParts = input.getFormDataMap();

        List<InputPart> inPart = formParts.get("file");
        List<InputPart> fNameBusinessPart = formParts.get("fieldName");

        String fileName = null;
        String fileBusinessName = null;
        String fileExtension = "pdf";//default
        InputStream is = null;

        String captUri = null;

        for (InputPart inputPart : inPart) {

            try {

                MultivaluedMap<String, String> mheaders = inputPart.getHeaders();
                fileName = parseFileName(mheaders);

                is = inputPart.getBody(InputStream.class, null);

            } catch (IOException e) {
                e.printStackTrace();
            }

            fileExtension = FilenameUtils.getExtension(fileName);
            //  String seq = UUIDGenerator.nextID();
        }

        for (InputPart inputPart : fNameBusinessPart) {
            try {
                // Retrieve headers, read the Content-Disposition header to obtain the original name of the file
                MultivaluedMap<String, String> mheaders = inputPart.getHeaders();
                // Handle the body of that part with an InputStream
                fileBusinessName = inputPart.getBody(String.class, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //TODO fully remove this part in production ( if needed )
        String finalName = UploadsUtils.uploadFile(is, taskId, getSite(), fileBusinessName, getFileSeq(), fileExtension);
        captUri = getCaptureURI(finalName);

        //String values = null;
        //Attach the doc to the instance of Task
        TaskDoc tdoc = tm.findByTaskId(taskId);

        if (tdoc == null) {
            saveTaskBusiness.createOrUpdateTaskDoc(task, username, "{}");
        }

        tdoc = tm.findByTaskId(taskId);

        String mimeType = StreamUtil.getFileMimeType(fileName);

        FileInputStream fis = new FileInputStream((File) findFile(finalName).getEntity());
        tdoc.attachDoc(finalName, mimeType, fis);

        tm.edit(tdoc);

        DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());

        if (flowDoc != null) {
            flowDoc.setLastTaskId(taskId);
            dfm.edit(flowDoc);
        }

        return Response.status(Response.Status.ACCEPTED).
                entity(new ProcessUploadResponse(finalName, captUri)).build();

    }

    // Parse Content-Disposition header to get the original file name
    private String parseFileName(MultivaluedMap<String, String> headers) {

        String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");

        for (String name : contentDispositionHeader) {

            if ((name.trim().startsWith("filename"))) {

                String[] tmp = name.split("=");

                String fileName = tmp[1].trim().replaceAll("\"", "");

                return fileName;
            }
        }
        return "WalterWhite";
    }

    String getCaptureURI(String docId) {
        String captureURI = getBaseUri() + "usertasks/files/" + docId;
        return captureURI;
    }

    private long getFileSeq() {

        LocalSettingsManager sm = new LocalSettingsManager();
        SettingsDoc sd = sm.findInstance();

        Object seq = sd.getValues().get("filesSeqIndex");

        if (seq == null) {
            seq = new Long(1);
        }
        String siteId = sd.getValues().get(SettingsUtils.site).toString();

        long seqLong = Long.valueOf(seq.toString()) + 1;

        Map m = new TreeMap(sd.getValues());
        m.put("filesSeqIndex", seqLong);
        sd.setValues(m);
        sm.edit(sd);

        return seqLong;
    }

}
