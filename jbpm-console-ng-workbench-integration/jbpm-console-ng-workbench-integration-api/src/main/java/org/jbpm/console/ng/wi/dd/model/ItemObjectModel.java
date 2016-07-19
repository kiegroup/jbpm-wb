/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.wi.dd.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemObjectModel that = (ItemObjectModel) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) {
            return false;
        }
        if (resolver != null ? !resolver.equals(that.resolver) : that.resolver != null) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (resolver != null ? resolver.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = ~~result;
        return result;
    }
}