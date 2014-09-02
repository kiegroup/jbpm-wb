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
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.api.FormRefreshCallback;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;

/**
 *
 * @author salaboy
 */
public abstract class AbstractStartProcessFormDisplayer implements StartProcessFormDisplayer {

  protected Constants constants = GWT.create(Constants.class);
  
  final protected FlowPanel container = new FlowPanel();
  final protected FlowPanel buttonsContainer = new FlowPanel();
  final protected VerticalPanel formContainer = new VerticalPanel();
  protected List<FormRefreshCallback> refreshCallbacks = new ArrayList<FormRefreshCallback>();

  protected String formContent;

  protected String deploymentId;
  protected String processDefId;
  protected String processName;
  
  @Inject
  private Caller<DataServiceEntryPoint> dataServices;
  
  @Inject
  protected Event<NewProcessInstanceEvent> newProcessInstanceEvent;

  @Inject
  private Caller<KieSessionEntryPoint> sessionServices;
  
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
        processName = summary.getProcessDefName();
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
  
  protected abstract void startProcessFromDisplayer();

  protected abstract void initDisplayer();
  
  

  @Override
  public void addFormRefreshCallback(FormRefreshCallback callback) {
    refreshCallbacks.add(callback);
  }

  protected ErrorCallback<Message> getUnexpectedErrorCallback() {
    return new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
        return true;
      }
    };
  }

  @Override
  public FlowPanel getContainer() {
    return container;
  }

  @Override
  public void startProcess(Map<String, Object> params) {
    sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
            .startProcess(deploymentId, processDefId, params);
  }

  protected RemoteCallback<Long> getStartProcessRemoteCallback() {
    return new RemoteCallback<Long>() {
      @Override
      public void callback(Long processInstanceId) {

        newProcessInstanceEvent.fire(new NewProcessInstanceEvent(deploymentId, processInstanceId, processDefId, processName, 1));
        close();

      }
    };
  }
}
