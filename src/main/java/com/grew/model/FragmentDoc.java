package com.grew.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FragmentDoc extends CouchDbDocument {

    private String businessKey;
    private String type;
    private String executionId;
    private String definitionId;
    private String entityId;
    private Map<Object, Object> values;
    private Map<Object, Object> signature;
    private String processInstanceId;
    private String entityType;
    private String created_on;
    private String created_by;

    public FragmentDoc() {
        values = new HashMap<>();
        signature = new HashMap<>();
    }

    public void addValue(String key, String value) {
        this.values.put(key, value);
    }

    public String getEntityType() {
        return entityType;
    }

    public void addSignatureValue(String key, String value) {
        this.signature.put(key, value);
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

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
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

    public Map<Object, Object> getSignature() {
        return signature;
    }

    public void setSignature(Map<Object, Object> signature) {
        this.signature = signature;
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

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

 
    public Map<Object, Object> getValues() {
        return values;
    }

    public void setValues(Map<Object, Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Fragment{" + '}';
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

}
