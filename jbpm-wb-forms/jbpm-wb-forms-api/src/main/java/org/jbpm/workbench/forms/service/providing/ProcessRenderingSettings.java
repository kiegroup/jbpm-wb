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

import org.jbpm.workbench.forms.service.providing.model.ProcessDefinition;
import org.kie.internal.task.api.ContentMarshallerContext;

public class ProcessRenderingSettings implements RenderingSettings {

    private ProcessDefinition process;
    private Map<String, String> processData;
    private String serverTemplateId;
    private String formContent;
    private ContentMarshallerContext marshallerContext;

    public ProcessRenderingSettings(ProcessDefinition process,
                                    Map<String, String> processData,
                                    String serverTemplateId,
                                    String formContent,
                                    ContentMarshallerContext marshallerContext) {
        this.process = process;
        this.processData = processData;
        this.serverTemplateId = serverTemplateId;
        this.formContent = formContent;
        this.marshallerContext = marshallerContext;
    }

    public ProcessDefinition getProcess() {
        return process;
    }

    public void setProcess(ProcessDefinition process) {
        this.process = process;
    }

    public Map<String, String> getProcessData() {
        return processData;
    }

    public void setProcessData(Map<String, String> processData) {
        this.processData = processData;
    }

    @Override
    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    public String getFormContent() {
        return formContent;
    }

    @Override
    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    @Override
    public ContentMarshallerContext getMarshallerContext() {
        return marshallerContext;
    }

    @Override
    public void setMarshallerContext(ContentMarshallerContext marshallerContext) {
        this.marshallerContext = marshallerContext;
    }
}
