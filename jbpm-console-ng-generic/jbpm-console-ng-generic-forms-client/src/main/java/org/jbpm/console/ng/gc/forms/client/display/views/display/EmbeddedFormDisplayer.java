/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.gc.forms.client.display.views.display;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ga.forms.display.view.FormDisplayerView;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Dependent
public class EmbeddedFormDisplayer implements FormDisplayerView,
                                              IsWidget {

    private Command onCloseCommand;
    private FormContentResizeListener resizeListener;
    private GenericFormDisplayer currentDisplayer;

    private EmbeddedFormDisplayerView view;

    @Inject
    public EmbeddedFormDisplayer(EmbeddedFormDisplayerView view) {
        this.view = view;
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        PortablePreconditions.checkNotNull("displayer",
                                           displayer);
        this.currentDisplayer = displayer;
        view.display(displayer);
    }

    @Override
    public void displayErrorMessage(String errorHeader, String errorMessage) {
        view.showErrorMessage(errorHeader, errorMessage);
    }

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.onCloseCommand = onCloseCommand;
    }

    @Override
    public FormContentResizeListener getResizeListener() {
        return resizeListener;
    }

    @Override
    public void setResizeListener(FormContentResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
