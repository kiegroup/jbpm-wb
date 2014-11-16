package org.jbpm.console.ng.wi.dd.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Parameter {

    private String type;
    private String value;

    public Parameter() {

    }

    public Parameter(String type, String value) {
        this.type = type;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
