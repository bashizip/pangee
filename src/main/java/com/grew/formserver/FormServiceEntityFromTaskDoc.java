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
package com.grew.formserver;

import com.grew.model.JsonSchemaDoc;
import com.grew.model.TaskDoc;

/**
 *
 * @author bashizip
 */
public class FormServiceEntityFromTaskDoc extends FormServiceEntityBase {

    private TaskDoc data;

    public FormServiceEntityFromTaskDoc() {

    }

    public FormServiceEntityFromTaskDoc(JsonSchemaDoc schema, TaskDoc data) {
        super(schema);
        this.data = data;

    }

    public TaskDoc getData() {
        return data;
    }

    public void setData(TaskDoc data) {
        this.data = data;
    }

}
