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

package org.jbpm.console.ng.ht.client.editors.taskform.modeler;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.event.dom.client.ClickEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

import org.uberfire.client.mvp.PlaceManager;

/**
 * Main view.
 */
@Dependent
@Templated(value = "FormDisplayModelerPopupViewImpl.html")
public class FormDisplayModelerPopupViewImpl extends Composite implements FormDisplayModelerPopupPresenter.FormDisplayModelerView {

    private FormDisplayModelerPopupPresenter presenter;

    
    @Inject
    @DataField
    public FormRendererWidget formRenderer;

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
    public UnorderedList navBarUL;
    
    @Inject
    @DataField
    public Button startTestButton;

    private long taskId;
    private String domainId;
    private String processId;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(FormDisplayModelerPopupPresenter presenter) {
        this.presenter = presenter;
        
        startTestButton.setText("Start");

    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public long getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public void setProcessId(String processId) {
        this.processId = processId;
    }

   

    @Override
    public Label getNameText() {
        return nameText;
    }

    @Override
    public Label getTaskIdText() {
        return taskIdText;
    }

    @Override
    public FlowPanel getOptionsDiv() {
        return optionsDiv;
    }

    @Override
    public UnorderedList getNavBarUL() {
        return navBarUL;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    @Override
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }
    
    @EventHandler("startTestButton")
    public void startTest(ClickEvent event) {
        presenter.renderTaskForm(1);
        
    }
    
    @Override
    public void loadContext(FormRenderContextTO ctx) {
        if (ctx != null) {
            formRenderer.loadContext(ctx);
        }
    }

}
