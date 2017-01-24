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

package org.jbpm.workbench.forms.client.display.views;

import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.forms.display.view.FormContentResizeListener;
import org.jbpm.workbench.common.service.ItemKey;
import org.uberfire.mvp.Command;

public interface FormDisplayerView {

    void display(GenericFormDisplayer<? extends ItemKey, ? extends FormRenderingSettings> display);

    Command getOnCloseCommand();

    void setOnCloseCommand(Command onCloseCommand);

    FormContentResizeListener getResizeListener();

    void setResizeListener(FormContentResizeListener resizeListener);

    GenericFormDisplayer getCurrentDisplayer();

}
