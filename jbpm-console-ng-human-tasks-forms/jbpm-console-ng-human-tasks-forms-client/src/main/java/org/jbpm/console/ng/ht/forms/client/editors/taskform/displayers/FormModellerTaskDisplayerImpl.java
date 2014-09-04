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

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.forms.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 *
 * @author salaboy
 */
@Dependent
public class FormModellerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

  
  @Inject
  private FormRendererWidget formRenderer;
  
  @Inject
  private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

  private static final String ACTION_SAVE_TASK = "saveTask";
  private static final String ACTION_COMPLETE_TASK = "completeTask";
  private String action;
  
  @Override
  protected void initDisplayer() {
      formRenderer.loadContext(formContent);
      
      formRenderer.setVisible(true);

      formContainer.add(formRenderer.asWidget());
  }

  @Override
  public boolean supportsContent(String content) {
    return content.startsWith("formRenderCtx_");
  }

  @Override
  protected void completeFromDisplayer() {
    submitForm(ACTION_COMPLETE_TASK);
  }

  @Override
  protected void saveStateFromDisplayer() {
    submitForm(ACTION_SAVE_TASK);
  }

  @Override
  protected void startFromDisplayer() {
    renderContextServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void response) {
        start();
      }
    }).clearContext(formContent);
  }

  @Override
  protected void claimFromDisplayer() {
    renderContextServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void response) {
        claim();
      }
    }).clearContext(formContent);
  }

  @Override
  protected void releaseFromDisplayer() {
    renderContextServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void response) {
        release();
      }
    }).clearContext(formContent);
  }

  protected void submitForm(String action) {
    this.action = action;
    formRenderer.submitFormAndPersist();
  }

   public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (event.isMine(formContent)) {
            if (event.getContext().getErrors() == 0) {
                if (ACTION_SAVE_TASK.equals(action)) {
                    renderContextServices.call(getSaveTaskStateCallback(), 
                            getUnexpectedErrorCallback()).saveTaskStateFromRenderContext(formContent, taskId);
                } else if (ACTION_COMPLETE_TASK.equals(action)) {
                    renderContextServices.call(getCompleteTaskRemoteCallback(), 
                            getUnexpectedErrorCallback()).completeTaskFromContext(formContent, taskId, identity.getName());
                }
            }
        }
    }

  @Override
  public int getPriority() {
    return 1;
  }
  
   
}
