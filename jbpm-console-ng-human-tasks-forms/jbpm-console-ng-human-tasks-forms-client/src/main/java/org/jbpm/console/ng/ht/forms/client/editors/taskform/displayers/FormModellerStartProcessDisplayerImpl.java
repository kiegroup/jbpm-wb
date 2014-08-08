/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.forms.api.FormRefreshCallback;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

/**
 *
 * @author salaboy
 */
@Dependent
public class FormModellerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

  protected Constants constants = GWT.create(Constants.class);

  private static final String ACTION_START_PROCESS = "startProcess";

  @Inject
  private Caller<DataServiceEntryPoint> dataServices;

  @Inject
  private FormRendererWidget formRenderer;

  @Inject
  private Event<BeforeClosePlaceEvent> closePlaceEvent;

  @Inject
  private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

  protected String action;

  @Override
  public void init(ProcessDefinitionKey key, String formContent) {
    this.deploymentId = key.getDeploymentId();
    this.processDefId = key.getProcessId();
    this.formContent = formContent;
    container.add(formContainer);
    container.add(buttonsContainer);

    dataServices.call(new RemoteCallback<ProcessSummary>() {
      @Override
      public void callback(ProcessSummary summary) {
        FormModellerStartProcessDisplayerImpl.this.processName = summary.getProcessDefName();
        FocusPanel wrapperFlowPanel = new FocusPanel();
        wrapperFlowPanel.setStyleName("wrapper form-actions");

        ClickHandler start = new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            startProcessFromDisplayer();
          }
        };

        Button startButton = new Button();
        startButton.setText(constants.Start());
        startButton.addClickHandler(start);

        wrapperFlowPanel.add(startButton);
        buttonsContainer.add(wrapperFlowPanel);
        initDisplayer();
      }
    }).getProcessDesc(deploymentId, processDefId);
  }

  protected void initDisplayer() {
    formRenderer.loadContext(formContent);

    formRenderer.setVisible(true);

    formContainer.add(formRenderer.asWidget());
  }

  protected void startProcessFromDisplayer() {
    submitForm(ACTION_START_PROCESS);
  }

  protected void submitForm(String action) {
    this.action = action;
    formRenderer.submitFormAndPersist();
  }

  @Override
  public boolean supportsContent(String content) {
    return formRenderer.isValidContextUID(content);
  }

  @Override
  public FlowPanel getContainer() {
    return container;
  }

  public void close() {
    renderContextServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void response) {
        formContent = null;
        for (FormRefreshCallback callback : refreshCallbacks) {
          callback.close();
        }
      }
    }).clearContext(formContent);
  }

  @Override
  public int getPriority() {
    return 1;
  }

  public void onFormSubmitted(@Observes FormSubmittedEvent event) {
    if (event.isMine(formContent)) {
      if (event.getContext().getErrors() == 0) {
        if (ACTION_START_PROCESS.equals(action)) {
          renderContextServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                  .startProcessFromRenderContext(formContent, deploymentId, processDefId);
        }
      }
    }
  }

}
