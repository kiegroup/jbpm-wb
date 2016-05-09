/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.backend.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

public class RemoteCorrelationKey implements CorrelationKey, Serializable {

    private static final long serialVersionUID = 4469298702447675428L;
    private String name;

    private List<CorrelationProperty<?>> properties = new ArrayList<CorrelationProperty<?>>();

    public RemoteCorrelationKey(String value) {
        this.properties.add(new RemoteCorrelationProperty(value));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<CorrelationProperty<?>> getProperties() {
        return this.properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RemoteCorrelationKey [name=" + name + ", properties="
                + properties + "]";
    }

    @Override
    public String toExternalForm() {
        return CorrelationKeyXmlAdapter.marshalCorrelationKey(this);
    }

}
