package org.jbpm.console.ng.bd.dd.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ItemObjectModel {

    public static final String REFLECTION_RESOLVER = "reflection";
    public static final String MVEL_RESOLVER = "mvel";

    private String name;
    private String value;
    private String resolver;

    private List<Parameter> parameters;

    public ItemObjectModel() {

    }

    public ItemObjectModel(String name, String value, String resolver, List<Parameter> parameters) {
        this.name = name;
        this.value = value;
        this.resolver = resolver;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<Parameter>();
        }
        this.parameters.add(parameter);
    }
}
