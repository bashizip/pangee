/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grew.formserver;

import com.grew.model.EntityDoc;
import com.grew.model.JsonSchemaDoc;

/**
 *
 * @author Administrateur
 */
public class FormServiceEntityFromEntity extends FormServiceEntityBase {

    EntityDoc data;

    public FormServiceEntityFromEntity() {
    }

    public FormServiceEntityFromEntity(JsonSchemaDoc schema, EntityDoc data) {
        super(schema);
         data.setValues(data.getParsedEntityValues(data.getValues()));  
        this.data = data;
    }

    public EntityDoc getData() {
        return data;
    }

    public void setData(EntityDoc data) {
        this.data = data;
    }
    
    
}
