package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDoc extends CouchDbDocument {

    private String type;
    private String businessKey;
    private String executionId;
    private String definitionId;
    private String entityId;
    private String created_on;
    private Date closed_on;
    private String created_by;
    private String reason_closed;
    private  Map<Object, Object> flow_variables;
    private List<String> fragments;
    private String processInstanceId;
    

    public FlowDoc() {
        flow_variables = new HashMap<>();

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
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

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public Date getClosed_on() {
        return closed_on;
    }

    public void setClosed_on(Date closed_on) {
        this.closed_on = closed_on;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getReason_closed() {
        return reason_closed;
    }

    public void setReason_closed(String reason_closed) {
        this.reason_closed = reason_closed;
    }

    public Map<Object, Object> getFlow_variables() {
        return flow_variables;
    }

    public void setFlow_variables(Map<Object, Object> flow_variables) {
        this.flow_variables = flow_variables;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }


}
