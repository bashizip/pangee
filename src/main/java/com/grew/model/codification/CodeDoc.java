
package com.grew.model.codification;

import java.util.ArrayList;
import java.util.List;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author pbash
 */
public class CodeDoc extends CouchDbDocument{
    
    private String name;
    private String type;
    private String description;
    private String depends;
    private List<CodeDocItem> items;

    public CodeDoc() {
        items=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepends() {
        return depends;
    }

    public void setDepends(String depends) {
        this.depends = depends;
    }

    public List<CodeDocItem> getItems() {
        return items;
    }

    public void setItems(List<CodeDocItem> items) {
        this.items = items;
    }

 
      
    
}
