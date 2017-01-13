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

package org.jbpm.console.ng.ht.forms.display.ht.api;

import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ga.forms.display.FormRenderingSettings;
import org.jbpm.console.ng.ht.model.TaskKey;

public class HumanTaskDisplayerConfig<S extends FormRenderingSettings> implements FormDisplayerConfig<TaskKey, S> {
    private TaskKey key;
    private String formOpener;
    private S renderingSettings;

    public HumanTaskDisplayerConfig(TaskKey key) {
        this.key = key;
    }

    @Override
    public TaskKey getKey() {
        return key;
    }

    @Override
    public S getRenderingSettings() {
        return renderingSettings;
    }

    public void setRenderingSettings( S renderingSettings ) {
        this.renderingSettings = renderingSettings;
    }

    @Override
    public String getFormOpener() {
        return formOpener;
    }

    public void setFormOpener(String formOpener) {
        this.formOpener = formOpener;
    }
}
