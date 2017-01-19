/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.forms.service.providing;

import java.util.Map;

import org.jbpm.workbench.forms.service.providing.model.TaskDefinition;
import org.kie.internal.task.api.ContentMarshallerContext;

public class TaskRenderingSettings implements RenderingSettings {
    private TaskDefinition task;
    private Map<String, Object> inputs;
    private Map<String, Object> outputs;
    private String formContent;
    private ContentMarshallerContext marshallerContext;

    public TaskRenderingSettings( TaskDefinition task,
                                  Map<String, Object> inputs,
                                  Map<String, Object> outputs,
                                  String formContent,
                                  ContentMarshallerContext marshallerContext ) {
        this.task = task;
        this.inputs = inputs;
        this.outputs = outputs;
        this.formContent = formContent;
        this.marshallerContext = marshallerContext;
    }

    public TaskDefinition getTask() {
        return task;
    }

    public void setTask( TaskDefinition task ) {
        this.task = task;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs( Map<String, Object> inputs ) {
        this.inputs = inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs( Map<String, Object> outputs ) {
        this.outputs = outputs;
    }

    @Override
    public String getFormContent() {
        return formContent;
    }

    @Override
    public void setFormContent( String formContent ) {
        this.formContent = formContent;
    }

    @Override
    public ContentMarshallerContext getMarshallerContext() {
        return marshallerContext;
    }

    @Override
    public void setMarshallerContext( ContentMarshallerContext marshallerContext ) {
        this.marshallerContext = marshallerContext;
    }
}
