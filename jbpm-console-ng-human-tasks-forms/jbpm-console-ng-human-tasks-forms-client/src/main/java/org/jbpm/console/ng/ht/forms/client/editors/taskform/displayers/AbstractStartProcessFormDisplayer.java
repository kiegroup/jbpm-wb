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

import com.google.gwt.user.client.ui.FlowPanel;
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
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.api.FormRefreshCallback;
import org.jbpm.console.ng.ht.forms.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;

/**
 *
 * @author salaboy
 */
public abstract class AbstractStartProcessFormDisplayer implements StartProcessFormDisplayer {

  final protected FlowPanel container = new FlowPanel();
  final protected FlowPanel buttonsContainer = new FlowPanel();
  final protected VerticalPanel formContainer = new VerticalPanel();
  protected List<FormRefreshCallback> refreshCallbacks = new ArrayList<FormRefreshCallback>();

  protected String formContent;

  protected String deploymentId;
  protected String processDefId;
  protected String processName;
  
  @Inject
  protected Event<NewProcessInstanceEvent> newProcessInstanceEvent;

  @Inject
  private Caller<KieSessionEntryPoint> sessionServices;

  protected abstract void initDisplayer();
  
  public abstract void close();

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
