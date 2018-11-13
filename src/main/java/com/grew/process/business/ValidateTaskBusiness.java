/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.process.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.ektorp.Attachment;
import com.grew.control.DataFlowManager;
import com.grew.control.DictonnaryManager;
import com.grew.control.ProcessManager;
import com.grew.control.LocalSettingsManager;
import com.grew.control.TaskManager;
import com.grew.formserver.FormSchemaServer;
import com.grew.model.DataFlowDoc;
import com.grew.model.ProcessDoc;
import com.grew.model.TaskDoc;
import com.grew.process.ProcessCustomResponse;
import com.grew.process.ProcessEngineManager;
import com.grew.process.ProcessErrorResponse;
import com.grew.utils.DateUtils;
import com.grew.utils.IdGenerator;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.SettingsUtils;

/**
 *
 * @author pbashizi
 */
@Stateless
public class ValidateTaskBusiness {

    @EJB
    private ProcessEngineManager processEngineManager;

    TaskManager tm;
    DataFlowManager dfm;

    @EJB
    FormSchemaServer formSchemaServer;
    
    @EJB
    SaveTaskBusiness saveTaskBusiness;

    /**
     *
     * @param task
     * @param username
     * @param values
     * @return
     */
    public Response execute(Task task, String username, String values) throws IOException {
//
//        Response badResponse1 = Response.status(Response.Status.BAD_REQUEST)
//                .entity(new ProcessErrorResponse("BAD request", "Iput data not match JsonSchema"))
//                .build();
//        Response badResponse2 = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .entity(new ProcessErrorResponse("Internal Error", "JsonSchama Parse error"))
//                .build();
//        try {
//            if (!formSchemaServer.validatateJson(values, task)) {
//                return badResponse1;
//            }
//        } catch (ProcessingException ex) {
//            ex.printStackTrace();
//            return badResponse2;
//        }

        tm = new TaskManager();
        dfm = new DataFlowManager();

        Response response = Response.ok(new ProcessCustomResponse("info", "task validated")).build();
        //create or update the TaskDoc
        TaskDoc taskDoc = null;

        try {

            taskDoc = tm.findByTaskId(task.getId());

        } catch (Exception e) {
            taskDoc = null;
        }

        if (taskDoc == null) {
            taskDoc = buildTaskDoc(task, username, values);
            tm.create(taskDoc);
        }

        //update the taskDoc
        taskDoc.setValidated_by(username);
        taskDoc.setValidated_on(DateUtils.getNowGMT());

       
        DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());

        Map dataString = ExecutionUtils.jsonStringToMapValues(values);
        Map filtered = dataString;

        if (flowDoc != null) {
            Map valuesOfCurrentEntity = (Map) flowDoc.getCurrent_entity().get("values");
            filtered = getFilteredData(dataString, valuesOfCurrentEntity);
        }

        taskDoc.setValues(filtered);

        tm.edit(taskDoc);
        task.setAssignee(username);

        HashMap<Object, Object> valuesMap = (HashMap) taskDoc.getValues();
        String pid = task.getProcessInstanceId();

        createOrUpdateDataFlowDoc(task.getId(), pid, valuesMap, username);

        return response;

    }

    /**
     *
     * @param dataString
     * @param valuesOfCurrentEntity
     * @return
     */
    Map getFilteredData(Map dataString, Map valuesOfCurrentEntity) {

        if (valuesOfCurrentEntity != null) {
            for (Iterator iterator = dataString.keySet().iterator(); iterator.hasNext();) {

                Object v = iterator.next();
                Object key = v.toString();
                Object value = dataString.get(key);

                if (valuesOfCurrentEntity.containsKey(key)) {
                    if (valuesOfCurrentEntity.get(key).equals(value)) {

                        iterator.remove(); //remove les valeurs en double qui n'ont pas chagé dans les pending values ( venues en lecture seule)
                    }
                }
            }

        }
        return dataString;
    }

    /**
     *
     * @param taskId
     * @param executionId
     * @param variables
     * @param user
     * @return
     */
    public DataFlowDoc createOrUpdateDataFlowDoc(
            String taskId,
            String executionId,
            HashMap<Object, Object> variables, String user) {

        //update DataFlowDoc
        dfm = new DataFlowManager();

        DataFlowDoc dataFlowDoc = dfm.findByProcessInstanceId(executionId);

        if (dataFlowDoc == null) {
            //create it
            DataFlowDoc fd = createDataFlowDoc(taskId, variables);
            return fd;
        }

        Map<Object, Object> currentValues = dataFlowDoc.getPending_values();

        // TODO Manage flow variables : separate from currentValues
        Map flowVariables = dataFlowDoc.getFlow_variables();

        if (variables == null) {
            variables = new HashMap<>(); //Defensive programmation
        }

        //put all exec variables inside
        for (Map.Entry<Object, Object> entry : variables.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (isFlowVariable(dataFlowDoc.getDefinitionId(), key.toString())) {
                flowVariables.put(key, value);
            } else {
                currentValues.put(key, value);
            }

        }

        //---------- set this task as the last one
        dataFlowDoc.setLastTaskId(taskId);
        //-------------------------------------

        String now = DateUtils.getNowGMT();

        dataFlowDoc.setLast_modified_on(now);
        dataFlowDoc.setLast_modified_by(user);

        TaskManager tdocm = new TaskManager();
        TaskDoc taskDoc = tdocm.findByTaskId(taskId);

        dataFlowDoc.setLast_modified_by(taskDoc.getValidated_by());

        dataFlowDoc.setLastTaskId(taskDoc.getTaskId());
        dataFlowDoc.setLastTaskKey(taskDoc.getTaskKey());
        dataFlowDoc.setLastTaskName(taskDoc.getTaskName());

        dataFlowDoc.setPending_values(currentValues);
        dataFlowDoc.setFlow_variables(flowVariables);
        Map<String, Attachment> taskAttachments = taskDoc.getAttachments();

        if (taskAttachments != null) {
            for (Map.Entry<String, Attachment> entry : taskAttachments.entrySet()) {
                String attName = entry.getKey();
                Attachment attachment = entry.getValue();
                dataFlowDoc.attachDoc(taskDoc.getId(), attName, attachment);
            }
        }
        
        updateCurrentEntity(taskDoc, dataFlowDoc);
        dfm.edit(dataFlowDoc);

        return dataFlowDoc;
    }

    public DataFlowDoc createDataFlowDoc(String taskId, HashMap<Object, Object> values){

        TaskDoc t = null;

        //String taskId = values.get(ExecutionUtils.LAST_TASK_ID).toString();
        TaskManager tdocm = new TaskManager();
        t = tdocm.findByTaskId(taskId);

        //create dataFlow  here
        DataFlowManager dfm = new DataFlowManager();
        DataFlowDoc dfd = buildDataFlowDoc(t);
        dfm.getRepo().add(dfd);

        return dfd;

    }

    /**
     *
     * @param taskDoc
     * @return
     */
    DataFlowDoc buildDataFlowDoc(TaskDoc taskDoc) {

        DataFlowDoc doc = new DataFlowDoc();

        String dfwId = "flw." + taskDoc.getBusinessKey().toLowerCase().replace("/", ".");

        doc.setId(dfwId);

        doc.setBusinessKey(taskDoc.getBusinessKey());

        doc.setCreated_by(taskDoc.getCreated_by());

        doc.setCreated_on(DateUtils.getNowGMT());
        doc.setDefinitionId(taskDoc.getDefinitionId());
        doc.setExecutionId(taskDoc.getExecutionId());
        doc.setType(doc.getClass().getSimpleName());
        doc.setProcessInstanceId(taskDoc.getProcessInstanceId());

        //--------update last task --------------
        doc.setLastTaskId(taskDoc.getTaskId());
        //----------------------------------------

        doc.setLast_modified_on(DateUtils.getNowGMT());
        doc.setLast_modified_by(taskDoc.getCreated_by());
        doc.setLastTaskId(taskDoc.getTaskId());
        doc.setLastTaskKey(taskDoc.getTaskKey());
        doc.setLastTaskName(taskDoc.getTaskName());

        // TODO Manage flow variables : separate from currentValues
        Map flowVariables = doc.getFlow_variables();
        Map<Object, Object> currentValues = doc.getPending_values();

        //put all exec variables inside
        for (Map.Entry<Object, Object> entry : taskDoc.getValues().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (isFlowVariable(doc.getDefinitionId(), key.toString())) {
                flowVariables.put(key, value);
            } else {
                currentValues.put(key, value);
            }

        }

        doc.setPending_values(currentValues);
        if (flowVariables != null) {
            doc.setFlow_variables(flowVariables);
        }

        Map<String, Attachment> taskAttachments = taskDoc.getAttachments();
        if (taskAttachments != null) {
            for (Map.Entry<String, Attachment> entry : taskAttachments.entrySet()) {
                String attName = entry.getKey();

                Attachment attachment = entry.getValue();

                doc.attachDoc(taskDoc.getId(), attName, attachment);
            }
        }
        updateCurrentEntity(taskDoc, doc);

        return doc;

    }

    void updateCurrentEntity(TaskDoc tdoc, DataFlowDoc dfd) {

        Map<Object, Object> currentUnvalitedValues = dfd.getCurrent_entity();
        Map<Object, Object> newValues = tdoc.getValues();

        if (currentUnvalitedValues.isEmpty()) {
            currentUnvalitedValues.put("_id", null);
        }
        ProcessManager pm = new ProcessManager();
        ProcessDoc pdoc = pm.findByDefinitionKey(processEngineManager.getProcessDefinition(tdoc.getDefinitionId()).getKey());
        String mainEntityTypeName = pdoc.getMainEntityKey();

        currentUnvalitedValues.put("dictionary", getProcessDictionary(mainEntityTypeName));

        Object valuesObj = currentUnvalitedValues.get("values");

        if (valuesObj == null) {
            currentUnvalitedValues.put("values", new HashMap()); //defensive 
        }

        Map values = (Map) currentUnvalitedValues.get("values");

        for (Map.Entry<Object, Object> entry : newValues.entrySet()) {
            Object key = entry.getKey();
            if (!isFlowVariable(tdoc.getDefinitionId(), key.toString())) {
                values.put(key, newValues.get(key));
            }
        }

        currentUnvalitedValues.put("values", values);

        dfd.setCurrent_entity(currentUnvalitedValues);
    }

    String getProcessDictionary(String entyTyType) {

        DictonnaryManager dm = new DictonnaryManager();

        String dic = dm.findByName(entyTyType).get(0).getId();
        return dic;
    }

    /**
     *
     * @param task
     * @param username
     * @param values
     * @return
     */
    TaskDoc buildTaskDoc(Task task, String username, String values) {

        TaskDoc taskDoc = new TaskDoc();
        taskDoc.setTaskId(task.getId());

        Object businessKeyObject = processEngineManager.getProcessEngine()
                .getRuntimeService()
                .getVariable(task.getExecutionId(), ExecutionUtils.BUSINESS_KEY_VAR);

        String businessKey;

        if (businessKeyObject == null || businessKeyObject.toString().equals("")) {

            String busString = getBusinessKey(task);
            taskDoc.setBusinessKey(busString);

            processEngineManager.getProcessEngine()
                    .getRuntimeService().
                    setVariable(task.getExecutionId(), ExecutionUtils.BUSINESS_KEY_VAR, busString);

        } else {
            businessKey = businessKeyObject.toString();
            taskDoc.setBusinessKey(businessKey);

        }

        String taskId = "flw." + taskDoc.getBusinessKey().toLowerCase().replace("/", ".") + ":task." + task.getProcessInstanceId();

        taskDoc.setId(taskId);
        String now = DateUtils.getNowGMT();
        taskDoc.setCreated_by(username);
        taskDoc.setCreated_on(now);
        taskDoc.setDefinitionId(processEngineManager.getProcessDefinition(task).getId());
        taskDoc.setExecutionId(task.getExecutionId());
        taskDoc.setType(taskDoc.getClass().getSimpleName());
        taskDoc.setTaskName(task.getName());
        taskDoc.setValues(ExecutionUtils.jsonStringToMapValues(values));

        return taskDoc;

    }

    /**
     * checks if a variable is a flow variable @see processDoc
     *
     * @param pdid
     * @param key
     * @return
     */
    boolean isFlowVariable(String pdid, String key) {

        ProcessDefinition pdef = processEngineManager.getProcessEngine().
                getRepositoryService().
                createProcessDefinitionQuery().
                processDefinitionId(pdid).
                singleResult();

        String pDefKey = pdef.getKey();

        int processVersion = pdef.getVersion();

        ProcessManager pm = new ProcessManager();
        ProcessDoc pdoc = pm.findByDefinitionKeyAndVersion(pDefKey, processVersion);
        if (pdoc == null) {
            return false;
        }
        return Arrays.asList(pdoc.getFlow_variables()).contains(key);
    }

    //TODO refactor
    String getBusinessKey(Task t) {

        String businessKey;
        String processKey = processEngineManager.getProcessDefinition(t).getKey();

        LocalSettingsManager sm = new LocalSettingsManager();
        Object indexObj = sm.get(SettingsUtils.businessKeyIndex);
        int index = 0;

        if (indexObj == null) {
            index = 0;
        } else {
            index = Integer.valueOf(indexObj.toString());
        }

        businessKey = IdGenerator.getNextBusinessKey(processKey, index);

        return businessKey;
    }
}
