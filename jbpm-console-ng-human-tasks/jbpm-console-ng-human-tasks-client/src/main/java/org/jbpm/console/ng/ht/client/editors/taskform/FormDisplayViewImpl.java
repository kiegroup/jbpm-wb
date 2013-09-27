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


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;


@Dependent
@Templated(value = "FormDisplayViewImpl.html")
public class FormDisplayViewImpl extends Composite implements FormDisplayPresenter.FormDisplayView {

    private FormDisplayPresenter presenter;

    @Inject
    @DataField
    public VerticalPanel formView;

    @Inject
    @DataField
    public FormRendererWidget formRenderer;

    @Inject
    @DataField
    public FlowPanel optionsDiv;

    private String action;
    private boolean formModeler;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(FormDisplayPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public FlowPanel getOptionsDiv() {
        return optionsDiv;
    }

   
    @Override
    public void loadContext(String ctxUID) {
        if (ctxUID != null) {
            formRenderer.loadContext(ctxUID);
        }
    }

    @Override
    public void submitStartProcessForm() {
        submitForm(FormDisplayPresenter.ACTION_START_PROCESS);
    }

    @Override
    public void submitChangeTab(String tab) {
        submitForm(tab);
    }

    @Override
    public void submitSaveTaskStateForm() {
        submitForm(FormDisplayPresenter.ACTION_SAVE_TASK);
    }

    @Override
    public void submitCompleteTaskForm() {
        submitForm(FormDisplayPresenter.ACTION_COMPLETE_TASK);
    }

    @Override
    public void submitForm() {
        formRenderer.submitForm();
    }

    protected void submitForm(String action) {
        this.action = action;
        formRenderer.submitFormAndPersist();
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public VerticalPanel getFormView() {
        return formView;
    }

    @Override
    public void loadForm(String form) {
        formModeler = formRenderer.isValidContextUID(form);
        if (formModeler) {
            loadContext(form);
            formView.setVisible(false);
            formRenderer.setVisible(true);
        } else {
            formView.clear();
            formView.add(new HTMLPanel(form));
            formView.setVisible(true);
            formRenderer.setVisible(false);
        }
    }

    @Override
    public boolean isFormModeler() {
        return formModeler;
    }
}
