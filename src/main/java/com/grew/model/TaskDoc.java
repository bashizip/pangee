package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;

import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDoc extends CouchDbDocument {

    //id and revision herited
    private String taskId;
    private String taskKey;
    private String taskName;
    private String businessKey;
    private String type;
    private String executionId;
    private String processInstanceId;
    private String definitionId;
    private String created_on;
    private String created_by;
    private String validated_on;
    private String validated_by;

    private Map<Object, Object> values;

    public TaskDoc() {

        values = new HashMap<>();
    }

    public void attachDoc(String id, String mimeType, InputStream is) {

        Attachment att = null;

        try {
            
            byte[] bytes = IOUtils.toByteArray(is);
            String encoded = Base64.getEncoder().encodeToString(bytes);

            att = new Attachment(id, encoded, mimeType);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
      
        addInlineAttachment(att);
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<Object, Object> getValues() {
        return values == null ? Collections.EMPTY_MAP : values;
    }

    public void setValues(Map<Object, Object> values) {
        this.values = values;
    }

    public void putValue(String key, String value) {
        this.values.put(key, value);

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getValidated_on() {
        return validated_on;
    }

    public void setValidated_on(String validated_on) {
        this.validated_on = validated_on;
    }

    public String getValidated_by() {
        return validated_by;
    }

    public void setValidated_by(String validated_by) {
        this.validated_by = validated_by;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

}
