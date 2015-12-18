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

package org.jbpm.console.ng.pr.forms.display.process.api;

import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;

public class ProcessDisplayerConfig implements FormDisplayerConfig<ProcessDefinitionKey> {

    private ProcessDefinitionKey key;
    private String processName;
    private String formContent;
    private String formOpener;

    public ProcessDisplayerConfig(ProcessDefinitionKey key, String processName) {
        this.key = key;
        this.processName = processName;
    }

    @Override
    public ProcessDefinitionKey getKey() {
        return key;
    }

    public String getProcessName() {
        return processName;
    }

    @Override
    public String getFormContent() {
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    @Override
    public String getFormOpener() {
        return formOpener;
    }

    public void setFormOpener(String formOpener) {
        this.formOpener = formOpener;
    }
}
