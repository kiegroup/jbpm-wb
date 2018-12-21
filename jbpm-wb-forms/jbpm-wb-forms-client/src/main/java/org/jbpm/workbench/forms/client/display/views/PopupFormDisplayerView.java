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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

@Dependent
public class PopupFormDisplayerView extends BaseModal implements FormDisplayerView {

    private Command onCloseCommand;

    private Command childCloseCommand;

    private boolean initialized = false;

    private FlowPanel body = GWT.create(FlowPanel.class);

    private ModalFooter footer = GWT.create(ModalFooter.class);

    private GenericFormDisplayer currentDisplayer;

    @PostConstruct
    protected void init() {
        onCloseCommand = new Command() {
            @Override
            public void execute() {
                closePopup();
            }
        };
        setSize(ModalSize.LARGE);
        setBody(body);
        add(footer);
        this.addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent hiddenEvent) {
                if (initialized) {
                    closePopup();
                }
            }
        });
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        currentDisplayer = displayer;
        body.clear();
        footer.clear();
        body.add(displayer.getContainer());
        
        if (displayer.appendFooter()) {
            footer.add(displayer.getFooter());
        }
        initialized = true;
        show();
    }

    public void closePopup() {
        hide();
        if (childCloseCommand != null) {
            childCloseCommand.execute();
        }
        setWidth("");
        initialized = false;
    }

    @Override
    public void displayErrorMessage(String header, String errorMessage) {
        ErrorPopup.showMessage(header + "\n" + errorMessage);
    }

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.childCloseCommand = onCloseCommand;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }
}