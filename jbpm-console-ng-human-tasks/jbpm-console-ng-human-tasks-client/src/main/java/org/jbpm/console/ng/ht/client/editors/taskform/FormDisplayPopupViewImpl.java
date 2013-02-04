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
package org.jbpm.console.ng.ht.client.editors.taskform;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;


import org.uberfire.client.mvp.PlaceManager;

/**
 * Main view.
 */
@Dependent
@Templated(value = "FormDisplayPopupViewImpl.html")
public class FormDisplayPopupViewImpl extends Composite
        implements
        FormDisplayPopupPresenter.FormDisplayView {

    private FormDisplayPopupPresenter presenter;
    @Inject
    @DataField
    public VerticalPanel formView;
    @Inject
    @DataField
    public Label nameText;

    @Inject
    @DataField
    public Label taskIdText;

    @Inject
    @DataField
    public FlowPanel optionsDiv;
   
    @Inject
    @DataField
    public Button closeButton;
    @Inject
    @DataField
    public UnorderedList navBarUL;
   
    private long taskId;
    private int sessionId;
    private String processId;
    
    @Inject
    private PlaceManager placeManager;
    
    @Inject
    private Event<NotificationEvent> notification;
    

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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public VerticalPanel getFormView() {
        return formView;
    }

    public Label getNameText() {
        return nameText;
    }

    public Label getTaskIdText() {
        return taskIdText;
    }

    public FlowPanel getOptionsDiv() {
        return optionsDiv;
    }
    
    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        presenter.close();
    }

    public UnorderedList getNavBarUL() {
      return navBarUL;
    }

    public int getSessionId() {
      return sessionId;
    }

    public void setSessionId(int sessionId) {
      this.sessionId = sessionId;
    }

   
 
    
 
}
