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
package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.ektorp.support.CouchDbDocument;
import com.grew.service.ResourceLink;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDoc extends CouchDbDocument {

    private int version;
    private String type;
    public String definitionKey;
    protected String dictionary;
    private String displayName;
    private String diagramId;
    private String description;
    private String[] groups;
    private List<ResourceLink> links;
    private String[] flow_variables;
    private String[] entityKeys;
    private String[] subEntitiesKeys;
    public String mainEntityKey;

    public ProcessDoc() {

    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String[] getSubEntitiesKeys() {
        return subEntitiesKeys;
    }

    public void setSubEntitiesKeys(String[] subEntitiesKeys) {
        this.subEntitiesKeys = subEntitiesKeys;
    }

    public void setLinks(List<ResourceLink> links) {
        this.links = links;
    }

    public List<ResourceLink> getLinks() {
        return links;
    }

    public String getType() {
        return type;
    }

    public String[] getFlow_variables() {
        if (flow_variables == null) {
            flow_variables = new String[1000];
        }
        return flow_variables;
    }

    public void setFlow_variables(String[] flow_variables) {
        this.flow_variables = flow_variables;
    }

    public String[] getEntityKeys() {
        return entityKeys;
    }

    public void setEntityKeys(String[] entityKeys) {
        this.entityKeys = entityKeys;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDefinitionKey() {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public String getDiagramId() {
        return diagramId;
    }

    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainEntityKey() {
        return mainEntityKey;
    }

    public void setMainEntityKey(String mainEntityKey) {
        this.mainEntityKey = mainEntityKey;
    }

}
