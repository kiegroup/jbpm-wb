/*
 * Copyright 2011 JBoss Inc 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.client.editors.tasks.fb.display.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.client.i18n.Constants;

/**
 * Main view.
 */
@Dependent
@Templated(value = "FormDisplayPopupViewImpl.html")
public class FormDisplayPopupViewImpl extends Composite
        implements
        FormDisplayPopupPresenter.FormBuilderView {

    private FormDisplayPopupPresenter presenter;
    @Inject
    @DataField
    public VerticalPanel formView;
    @Inject
    @DataField
    public Label taskNameText;
    @Inject
    @DataField
    public Label taskDescriptionText;
    @Inject
    @DataField
    public Button closeButton;
    public long taskId;
    @Inject
    private Event<NotificationEvent> notification;
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(FormDisplayPopupPresenter presenter) {
        this.presenter = presenter;

    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public VerticalPanel getFormView() {
        return formView;
    }

    public Label getTaskNameText() {
        return taskNameText;
    }

    public Label getTaskDescriptionText() {
        return taskDescriptionText;
    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        presenter.close();
    }
    
    
}
