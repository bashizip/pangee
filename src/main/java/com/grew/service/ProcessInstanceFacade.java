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

import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import com.grew.couchdb.DataBaseMaintenance;
import com.grew.process.ProcessEngineManager;
import com.grew.security.Secured;

/**
 *
 * @author bashizip
 */
@Api(value = "Process Instances")
@Path("processInstances")
@Stateless
public class ProcessInstanceFacade {

    @EJB
    ProcessEngineManager pm;

    @EJB
    DataBaseMaintenance dbm;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("running/{processDefinitionID}")
    public List<ProcessInstance> getAllRunningProcessInstances(@PathParam("processDefinitionID") String processDefinitionID) {
        // get process engine and services
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // query for latest process definition with given name

        // list all running/unsuspended instances of the process
        List<ProcessInstance> processInstances
                = runtimeService.createProcessInstanceQuery()
                .processDefinitionId(processDefinitionID)
                .active() // we only want the unsuspended process instances
                .list();

        return processInstances;
    }

    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("onEntity/{processDefinitionId}/{entityId}")
    public boolean isExectutionOnEntity(String processDefinitionID , String entityId) {
        RuntimeService rs = pm.getProcessEngine().getRuntimeService();
        
        List<ProcessInstance> processInstances = getAllRunningProcessInstances(processDefinitionID);
        List<Execution> executions = new ArrayList();
        for(ProcessInstance pi: processInstances){
            
         executions = rs.createExecutionQuery()
                .processInstanceId(pi.getProcessInstanceId()).variableValueLike("_id", entityId).active().list();
         if(!executions.isEmpty()){
             return true;
         }
        }
        
        return false;
    }

    
    
    
    @ApiOperation(value = "Maintenance : Delete all deployed processes ")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deleteAll")
    public List deleteAll() {
        return pm.deleteAllDeployments();
    }

    @ApiOperation(value = "Maintenance : Delete all NON design docs in database ")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cleanDB")
    public JsonObject deleteAllData() {

        JsonObject ct = dbm.deleteAllNonDesignDoc();

        return ct;

    }

}
