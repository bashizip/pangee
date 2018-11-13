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
package com.grew.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import com.grew.service.ResourceLink;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoricTaskWrapper implements Serializable {

    private String taskId;
    private String taskDefKey;
    private String owner;
    private String assignee;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private Long durationMillisec;
    private String executionId;
    private String candidateGroups;
    private String processDisplayName;
    private String businessKey;

    private List<ResourceLink> links;

    public HistoricTaskWrapper(HistoricTaskInstance task) {
        this.taskId = task.getId();
        this.taskDefKey = task.getTaskDefinitionKey();
        this.assignee = task.getAssignee();
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
        this.description = task.getDescription();
        this.executionId = task.getExecutionId();
        this.name = task.getName();
        this.owner = task.getOwner();
        this.durationMillisec = task.getDurationInMillis();

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getDurationMillisec() {
        return durationMillisec;
    }

    public void setDurationMillisec(Long durationMillisec) {
        this.durationMillisec = durationMillisec;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(String candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public String getProcessDisplayName() {
        return processDisplayName;
    }

    public void setProcessDisplayName(String processDisplayName) {
        this.processDisplayName = processDisplayName;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public List<ResourceLink> getLinks() {
        return links;
    }

    public void setLinks(List<ResourceLink> links) {
        this.links = links;
    }

   

}
