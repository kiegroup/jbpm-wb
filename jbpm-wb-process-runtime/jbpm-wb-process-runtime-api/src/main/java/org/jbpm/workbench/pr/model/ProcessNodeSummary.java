/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
@Bindable
public class ProcessNodeSummary extends GenericSummary<Long> {

    private String type;

    public ProcessNodeSummary() {
    }

    public ProcessNodeSummary(final Long id,
                              final String name,
                              final String type) {
        super(id,
              name);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel(){
        return getName() == null || getName().trim().isEmpty() ? getType() + "-" + getId() : getName();
    }

    @Override
    public String toString() {
        return "ProcessNodeSummary{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
