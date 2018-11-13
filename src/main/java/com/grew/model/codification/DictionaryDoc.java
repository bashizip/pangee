/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.model.codification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author administrateur
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryDoc extends CouchDbDocument {

    private String type;
    private String key;
    private String name;
    private String title;
    private String composeName;
    private String[] fields;

    public DictionaryDoc() {
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposeName() {
        return composeName;
    }

    public void setComposeName(String composeName) {
        this.composeName = composeName;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return name;
    }

}
