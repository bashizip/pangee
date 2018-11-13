/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.grew.control.TaskManager;
import com.grew.model.DataFlowDoc;
import com.grew.model.TaskDoc;

/**
 *
 * @author bashizip
 */
public class DataFlowDocWrapper implements Serializable {

    private String businessKey;
    private String type;
    private String executionId;
    private String definitionId;
    private String created_on;
    private String created_by;
    private String last_modified_on;
    private String last_modified_by;
    private String last_task;
    private String processInstanceId;

    private Map<Object, Object> values;

    public DataFlowDocWrapper() {

        values = new HashMap<>();

    }

    public DataFlowDocWrapper(DataFlowDoc from) {

        this();

        this.processInstanceId = from.getProcessInstanceId();
        this.businessKey = from.getBusinessKey();
        this.type = from.getType();
        this.executionId = from.getExecutionId();
        this.definitionId = from.getDefinitionId();
        this.created_on = from.getCreated_on();
        this.created_by = from.getCreated_by();
        this.last_modified_on = from.getLast_modified_on();
        this.last_modified_by = from.getLast_modified_by();
        this.last_task = from.getLastTaskId();

        Map valuesOfCurrentEntity = (Map) from.getCurrent_entity().get("values");

        if (valuesOfCurrentEntity != null) {
            this.values.putAll(valuesOfCurrentEntity); //Apres validation, pending_values est vide; les données sont dans current entity

        }


          this.values.putAll(from.getPending_values()); //TODO put only values not in 'current_entity'
        this.values.putAll(from.getFlow_variables()); // ajouter aussi les flow variables

        //Todo add data from last task
        TaskManager tm = new TaskManager();
        String lastTaskId = from.getLastTaskId();

        TaskDoc tdoc = tm.findByTaskId(lastTaskId);

        Map taskValues = new HashMap();

        if (tdoc != null) {
            taskValues = tdoc.getValues();
        }

        this.values.putAll(taskValues); //ajouter les données de la dernière task ( non encore validée )

    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void addPendingValue(String key, String value) {
        values.put(key, value);
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getLast_modified_on() {
        return last_modified_on;
    }

    public void setLast_modified_on(String last_modified_on) {
        this.last_modified_on = last_modified_on;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public String getLast_task() {
        return last_task;
    }

    public void setLast_task(String last_task) {
        this.last_task = last_task;
    }

    public void setValues(Map<Object, Object> values) {
        this.values = values;
    }

    public Map<Object, Object> getValues() {
        return this.values;
    }

}
