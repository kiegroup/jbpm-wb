package org.jbpm.console.ng.ht.forms.display.ht.api;

import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ht.model.TaskKey;

public class HumanTaskDisplayerConfig implements FormDisplayerConfig<TaskKey> {
    private TaskKey key;
    private String formContent;
    private String formOpener;

    public HumanTaskDisplayerConfig(TaskKey key) {
        this.key = key;
    }

    @Override
    public TaskKey getKey() {
        return key;
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
