package com.grew.process.business;

import java.util.Iterator;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import org.camunda.bpm.engine.task.Task;
import com.grew.control.DataFlowManager;
import com.grew.control.LocalSettingsManager;
import com.grew.control.TaskManager;
import com.grew.formserver.FormSchemaServer;
import com.grew.model.DataFlowDoc;
import com.grew.model.TaskDoc;
import com.grew.process.ProcessCustomResponse;
import com.grew.process.ProcessEngineManager;
import com.grew.utils.DateUtils;
import com.grew.utils.IdGenerator;
import com.grew.utils.ExecutionUtils;
import com.grew.utils.SettingsUtils;

/**
 *
 * @author pbashizi
 */
@Stateless
public class SaveTaskBusiness {

    TaskManager tm;
    DataFlowManager dfm;

    @EJB
    ProcessEngineManager processEngineManager;

    @EJB
    FormSchemaServer formSchemaServer;

    public Response createOrUpdateTaskDoc(Task task, String username, String valuesString) {
//
//        Response badResponse1 = Response.status(Response.Status.BAD_REQUEST)
//                .entity(new ProcessErrorResponse("BAD request", "Iput data not match JsonSchema"))
//                .build();
//        Response badResponse2 = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .entity(new ProcessErrorResponse("Internal Error", "JsonSchama Parse error"))
//                .build();
//        try {
//            if (!formSchemaServer.validatateJson(valuesString, task)) {
//                return badResponse1;
//            }
//        } catch (ProcessingException | IOException ex) {
//            ex.printStackTrace();
//            return badResponse2;
//        }

        tm = new TaskManager();
        dfm = new DataFlowManager();

        Response res = Response.ok(new ProcessCustomResponse("info", "task data updated successfuly")).build();
        //create or update the TaskDoc
        TaskDoc taskDoc = null;

        try {

            taskDoc = tm.findByTaskId(task.getId());

        } catch (Exception e) {
            e.printStackTrace();
           // res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (taskDoc == null) {

            taskDoc = buildTaskDoc(task, username, valuesString);
            tm.create(taskDoc);

        } else {

            //TODO filter data
            DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());
            Map dataString = ExecutionUtils.jsonStringToMapValues(valuesString);
            Map filtered = dataString;

            if (flowDoc != null) {
                Map valuesOfCurrentEntity = (Map) flowDoc.getCurrent_entity().get("values");
                filtered = getFilteredData(dataString, valuesOfCurrentEntity);
            }

            taskDoc.setValues(filtered);
            tm.edit(taskDoc);

            if (flowDoc != null) {
                flowDoc.setLastTaskId(task.getId());
                dfm.edit(flowDoc);
                res = Response.accepted().build();

            }

        }
        return res;
    }

    /**
     *
     * @param dataString
     * @param valuesOfCurrentEntity
     * @return
     */
     Map getFilteredData(Map dataString, Map valuesOfCurrentEntity) {
        Map filtered = dataString;

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

        String taskId = "flw." + taskDoc.getBusinessKey()
                .
                replace("/", ".") + ":task." + task.getId();

        String now = DateUtils.getNowGMT();

        taskDoc.setId(taskId.toLowerCase());
        taskDoc.setTaskKey(task.getTaskDefinitionKey());
        taskDoc.setCreated_by(username);
        taskDoc.setCreated_on(now);
        taskDoc.setDefinitionId(processEngineManager.getProcessDefinition(task).getId());
        taskDoc.setExecutionId(task.getExecutionId());
        taskDoc.setProcessInstanceId(task.getProcessInstanceId());
        taskDoc.setType(taskDoc.getClass().getSimpleName());
        taskDoc.setTaskName(task.getName());

        Map dataString = ExecutionUtils.jsonStringToMapValues(values);
        DataFlowDoc flowDoc = dfm.findByProcessInstanceId(task.getProcessInstanceId());
        Map filtered = dataString;
        
        if (flowDoc != null) {
            filtered = getFilteredData(dataString, flowDoc.getCurrent_entity());

        }

        taskDoc.setValues(filtered);
        return taskDoc;

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
