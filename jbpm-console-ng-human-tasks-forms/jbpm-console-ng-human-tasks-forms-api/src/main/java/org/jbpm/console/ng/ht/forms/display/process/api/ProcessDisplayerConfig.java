package org.jbpm.console.ng.ht.forms.display.process.api;

import org.jbpm.console.ng.ht.forms.display.FormDisplayerConfig;
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
