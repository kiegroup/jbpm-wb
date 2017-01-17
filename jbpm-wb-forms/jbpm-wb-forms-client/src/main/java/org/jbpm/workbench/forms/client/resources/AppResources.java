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

package org.jbpm.workbench.forms.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface AppResources extends ClientBundle {

    AppResources INSTANCE = GWT.create(AppResources.class);

    interface StyleResources extends CssResource {

        @ClassName("task-buttons")
        String taskButtons();

        @ClassName("task-form-panel")
        String taskFormPanel();

        @ClassName("task-form-container")
        String taskFormContainer();

    }

    @Source("css/forms.css")
    StyleResources style();
}
