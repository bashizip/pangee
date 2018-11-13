package com.grew.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import jdk.nashorn.internal.ir.ObjectNode;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

/**
 *
 * @author bashizip
 */
@Stateless
public class ProcessEngineManager {

    ProcessEngine pe;

    public ProcessEngineManager() {

    }

    /**
     *
     * @param processKey
     * @param businessKey
     * @return
     */
    public String start(String processKey,String businessKey) {

        ProcessInstance instance = getProcessEngine().
                getRuntimeService()
                .startProcessInstanceByKey(processKey,businessKey);

        return instance.getProcessInstanceId();
    }

    public void pause(String processName) {

        getProcessEngine().getRuntimeService()
                .suspendProcessInstanceById(processName);
    }

    public ProcessEngine getProcessEngine() {

        if (pe == null) {
            pe = ProcessEngines.getDefaultProcessEngine();
            pe.getProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL);
        }
        return pe;
    }

    public ProcessDefinition getProcessDefinition(Task task) {
        ProcessDefinition myProcessDefinition
                = getProcessEngine()
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .list().get(0);
        return myProcessDefinition;
    }

    public ProcessDefinition getProcessDefinition(String processDefinitionId) {

        ProcessDefinition myProcessDefinition;
        List<ProcessDefinition> pdef = getProcessEngine()
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .list();
     
        if (pdef.isEmpty()) {
            return null;
        }
        
        myProcessDefinition = pdef.get(0);

        return myProcessDefinition;
    }

    /**
     * Delete all deployments
     *
     * @return
     */
    public List<String> deleteAllDeployments() {

        List<Deployment> deployments = getProcessEngine().getRepositoryService()
                .createDeploymentQuery()
                .list();

        List<String> deploymentIds = new ArrayList<>();

        for (Deployment deployment : deployments) {
            deploymentIds.add(deployment.getId());
        }

        for (String deploymentId : deploymentIds) {
            getProcessEngine().getRepositoryService().deleteDeployment(deploymentId, true);

        }

        return deploymentIds;

    }

    public static void main(String[] args) {
        new ProcessEngineManager().deleteAllDeployments();
    }

}
