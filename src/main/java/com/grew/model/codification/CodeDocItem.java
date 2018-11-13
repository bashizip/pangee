
package com.grew.model.codification;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author pbash
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeDocItem {

    private String description;
    private String depends;
    private String text;
    private String value;
    private String visibility;

    public CodeDocItem() {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}

