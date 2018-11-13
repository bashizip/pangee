/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;
import org.ektorp.support.CouchDbDocument;
import com.grew.control.DataFlowManager;
import com.grew.couchdb.CoushDB;
import com.grew.couchdb.DBRefs;
import com.grew.dao.DataFlowRepository;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFlowDoc extends CouchDbDocument {

    private String processInstanceId;
    private String businessKey;
    private String type;
    private String executionId;
    private String definitionId;
    private String created_on;
    private String created_by;
    private String last_modified_on;
    private String last_modified_by;
    private String lastTaskId;
    private String lastTaskKey;
    private String lastTaskName;

    private Map<Object, Object> flow_variables;
    private Map<Object, Object> pending_values;
    private Map<Object, Object> current_entity;

    public DataFlowDoc() {

        flow_variables = new HashMap<>();
        pending_values = new HashMap<>();
        current_entity = new HashMap<>();

    }

    public void attachDoc(String docId,String key, Attachment attachment) {
      
        CoushDB cdb = new CoushDB(DBRefs.localDB());
        AttachmentInputStream data = cdb.getDB().getAttachment(docId, key);
        Attachment att = null;

        try {
            
            byte[] bytes = IOUtils.toByteArray(data);
            String encoded = Base64.getEncoder().encodeToString(bytes);

            att = new Attachment(key, encoded, attachment.getContentType());
            
            addInlineAttachment(att);

            data.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

   

    public String getLastTaskName() {
        return lastTaskName;
    }

    public void setLastTaskName(String lastTaskName) {
        this.lastTaskName = lastTaskName;
    }

    public String getLastTaskId() {
        return lastTaskId;
    }

    public void setLastTaskId(String lastTaskId) {
        this.lastTaskId = lastTaskId;
    }

    public String getLastTaskKey() {
        return lastTaskKey;
    }

    public void setLastTaskKey(String lastTaskKey) {
        this.lastTaskKey = lastTaskKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void addFlowVariable(String key, String value) {
        flow_variables.put(key, value);
    }

    public void addPendingValue(String key, String value) {
        pending_values.put(key, value);
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

    public Map<Object, Object> getFlow_variables() {
        return flow_variables;
    }

    public void setFlow_variables(Map<Object, Object> flow_variables) {
        this.flow_variables = flow_variables;
    }

    public Map<Object, Object> getPending_values() {
        return pending_values;
    }

    public void setPending_values(Map<Object, Object> pending_values) {
        this.pending_values = pending_values;
    }

    public Map<Object, Object> getCurrent_entity() {
        return current_entity;
    }

    public void setCurrent_entity(Map<Object, Object> current_entity) {
        this.current_entity = current_entity;
    }

    public void setProcessInstanceId(String processInstanceId) {

        this.processInstanceId = processInstanceId;
    }
}
