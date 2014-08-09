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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ht.forms.api.FormRefreshCallback;
import org.jbpm.console.ng.ht.forms.api.GenericFormDisplayer;
import org.jbpm.console.ng.ht.forms.ht.api.HumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.forms.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 *
 * @author salaboy
 */
@Dependent
@WorkbenchScreen(identifier = "Generic Form Display")
public class GenericFormDisplayPresenter implements FormRefreshCallback {

  private Constants constants = GWT.create(Constants.class);

  private List<HumanTaskFormDisplayer> taskDisplayers = new ArrayList<HumanTaskFormDisplayer>();
  private List<StartProcessFormDisplayer> processDisplayers = new ArrayList<StartProcessFormDisplayer>();

  protected long currentTaskId = 0;

  protected String currentProcessId;

  protected String currentDeploymentId;
  
  protected String placeOnClose;

  protected PlaceRequest place;
  
  @Inject
  private Event<BeforeClosePlaceEvent> closePlaceEvent;
  
  @Inject
  private PlaceManager placeManager;

  @Inject
  protected SyncBeanManager iocManager;

  public interface GenericFormDisplayView extends UberView<GenericFormDisplayPresenter> {

    void displayNotification(String text);

    void render(FlowPanel content);
  }

  @Inject
  private GenericFormDisplayView view;

  @Inject
  private Caller<FormServiceEntryPoint> formServices;

  public GenericFormDisplayPresenter() {

  }

  @PostConstruct
  private void init() {
    Collection<IOCBeanDef<GenericFormDisplayer>> displayers = iocManager.lookupBeans(GenericFormDisplayer.class);
    if (displayers != null) {
      for (IOCBeanDef displayerDef : displayers) {
        if (displayerDef.getInstance() instanceof HumanTaskFormDisplayer) {
          taskDisplayers.add((HumanTaskFormDisplayer) displayerDef.getInstance());
        } else if (displayerDef.getInstance() instanceof StartProcessFormDisplayer) {
          processDisplayers.add((StartProcessFormDisplayer) displayerDef.getInstance());
        }
      }
    }

  }

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Form();
  }

  @WorkbenchPartView
  public UberView<GenericFormDisplayPresenter> getView() {
    return view;
  }

  @OnOpen
  public void onOpen() {
    currentTaskId = Long.parseLong(place.getParameter("taskId", "-1"));
    currentProcessId = place.getParameter("processId", "none");
    currentDeploymentId = place.getParameter("domainId", "none");
    placeOnClose = place.getParameter("onClose", "none");
    
    refresh();
    
  }

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    this.place = place;
  }

  @Override
  public void close() {
    if(!placeOnClose.equals("none")){
      placeManager.closePlace(place);
      placeManager.forceClosePlace(placeOnClose);
    }else{
      closePlaceEvent.fire(new BeforeClosePlaceEvent(GenericFormDisplayPresenter.this.place));
    }
  }
  
  

  @Override
  public void refresh() {
    if (currentTaskId != -1) {
      if (taskDisplayers != null) {
        formServices.call(new RemoteCallback<String>() {
          @Override
          public void callback(String form) {
            Collections.sort(taskDisplayers, new Comparator<HumanTaskFormDisplayer>() {

              @Override
              public int compare(HumanTaskFormDisplayer o1, HumanTaskFormDisplayer o2) {
                if(o1.getPriority() < o2.getPriority()){
                  return -1;
                }else if(o1.getPriority() > o2.getPriority()){
                  return 1;
                }else{
                  return 0;
                }
              }
            });
            for (HumanTaskFormDisplayer d : taskDisplayers) {
              if (d.supportsContent(form)) {
                d.init(new TaskKey(currentTaskId), form);
                d.addFormRefreshCallback(GenericFormDisplayPresenter.this);
                view.render(d.getContainer());
                return;
              }
            }
          }
        }).getFormDisplayTask(currentTaskId);
      }

    } else if (!currentProcessId.equals("none")) {
      if (processDisplayers != null) {
        formServices.call(new RemoteCallback<String>() {
          @Override
          public void callback(String form) {
            Collections.sort(processDisplayers, new Comparator<StartProcessFormDisplayer>() {

              @Override
              public int compare(StartProcessFormDisplayer o1, StartProcessFormDisplayer o2) {
                if(o1.getPriority() < o2.getPriority()){
                  return -1;
                }else if(o1.getPriority() > o2.getPriority()){
                  return 1;
                }else{
                  return 0;
                }
              }
            });
            for (StartProcessFormDisplayer d : processDisplayers) {
              if (d.supportsContent(form)) {
                d.init(new ProcessDefinitionKey(currentDeploymentId, currentProcessId), form);
                d.addFormRefreshCallback(GenericFormDisplayPresenter.this);
                view.render(d.getContainer());
                return;
              }
            }
          }
        }).getFormDisplayProcess(currentDeploymentId, currentProcessId);
      }

    }

  }

}
