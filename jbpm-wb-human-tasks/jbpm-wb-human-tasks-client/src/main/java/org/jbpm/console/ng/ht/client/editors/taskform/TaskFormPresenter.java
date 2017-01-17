/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ht.client.editors.taskform;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.forms.client.display.views.FormDisplayerView;

@Dependent
public class TaskFormPresenter {
    public interface TaskFormView extends IsWidget {
        FormDisplayerView getDisplayerView();
    }

    @Inject
    TaskFormView view;

    public TaskFormView getTaskFormView() {
        return view;
    }

    public IsWidget getView() {
        return view;
    }
}
