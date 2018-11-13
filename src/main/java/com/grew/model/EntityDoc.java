/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDoc extends CouchDbDocument {

    private String processInstanceId; //is the id of the last process executed on this Entity. TODO remove after process end
    private String type;
    private String dictionary;
    private String site;
    private String version;
    private Map<Object, Object> values;
    private String entityType;
    private String _name;

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public EntityDoc() {
        values = new HashMap<>();
    }

    public String getEntityType() {
        return entityType;
    }

    public String getDictionary() {
        return dictionary;
    }

    public Map<Object, Object> getValues() {
        return values;
    }

    public void setValues(Map<Object, Object> values) {
        this.values = values;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<Object, Object> getParsedEntityValues(Map<Object, Object> v) {
        if (v == null) {
            return new HashMap<>();
        }
        Map<Object, Object> parsedvalues = new HashMap<>();

        for (Map.Entry<Object, Object> entry : v.entrySet()) {
            Object key = entry.getKey();
            Map value = (Map) entry.getValue();
            parsedvalues.put(key, value.get("value"));

        }
        return parsedvalues;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

}
