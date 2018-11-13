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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonSchemaDoc extends CouchDbDocument {

    private String formKey;
    private Map<Object, Object> schema;
    private Map options;
    private String title;
    private String settings;

    public JsonSchemaDoc() {
    }

    public JsonSchemaDoc(String formKey) {
        this.formKey = formKey;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public Map getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOptions(Map options) {
        this.options = options;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public Map<Object, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<Object, Object> schema) {
        this.schema = schema;
    }

}
