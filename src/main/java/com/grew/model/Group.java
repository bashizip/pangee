
package com.grew.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group extends CouchDbDocument implements org.camunda.bpm.engine.identity.Group{
    
    private String name;
    private String type;

    public Group() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
     }

    @Override
    public void setType(String type) {
        this.type = type;
     }

    @Override
    public String toString() {
        return name;
    }
     
    
}
