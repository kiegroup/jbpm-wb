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
package org.jbpm.workbench.ht.client.editors.taskprocesscontext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.events.ProcessInstancesWithDetailsRequestEvent;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@Dependent
public class TaskProcessContextPresenter {

    public interface TaskProcessContextView extends UberView<TaskProcessContextPresenter> {

        void displayNotification(String text);

        void setProcessInstanceId(String none);

        void setProcessId(String none);

        void enablePIDetailsButton(boolean enable);
    }

    private PlaceManager placeManager;

    private ActivityManager activityManager;

    private TaskProcessContextView view;

    private AuthorizationManager authorizationManager;

    public User identity;

    private Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected;

    private Caller<TaskService> taskService;

    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    private long currentTaskId = 0;
    private long currentProcessInstanceId = -1L;
    private String serverTemplateId;
    private String containerId;

    @Inject
    public TaskProcessContextPresenter(TaskProcessContextView view,
                                       PlaceManager placeManager,
                                       Caller<TaskService> taskService,
                                       Caller<ProcessRuntimeDataService> processRuntimeDataService,
                                       Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected,
                                       ActivityManager activityManager,
                                       AuthorizationManager authorizationManager,
                                       User identity) {
        this.view = view;
        this.taskService = taskService;
        this.processRuntimeDataService = processRuntimeDataService;
        this.placeManager = placeManager;
        this.processInstanceSelected = processInstanceSelected;
        this.activityManager = activityManager;
        this.authorizationManager = authorizationManager;
        this.identity = identity;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        boolean enableProcessInstanceLink = false;
        if( hasAccessToPerspective(PerspectiveIds.PROCESS_INSTANCES) &&
                !activityManager.getActivities(new DefaultPlaceRequest(PerspectiveIds.PROCESS_INSTANCES)).isEmpty() ) {
            enableProcessInstanceLink = true;
        }
        view.enablePIDetailsButton(enableProcessInstanceLink);
    }

    public IsWidget getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {
        processRuntimeDataService.call(new RemoteCallback<ProcessInstanceSummary>() {
                              @Override
                              public void callback(ProcessInstanceSummary summary) {
                                  placeManager.goTo(PerspectiveIds.PROCESS_INSTANCES);
                                  processInstanceSelected.fire(new ProcessInstancesWithDetailsRequestEvent(
                                          serverTemplateId,
                                          summary.getDeploymentId(),
                                          summary.getProcessInstanceId(),
                                          summary.getProcessId(),
                                          summary.getProcessName(),
                                          summary.getState())
                                  );
                              }
                          }
        ).getProcessInstance(serverTemplateId, new ProcessInstanceKey(serverTemplateId, containerId, currentProcessInstanceId));
    }

    public void refreshProcessContextOfTask() {
        taskService.call(new RemoteCallback<TaskSummary>() {
                                  @Override
                                  public void callback(TaskSummary details) {
                                      if (details == null || details.getProcessInstanceId() == null) {
                                          view.setProcessInstanceId("None");
                                          view.setProcessId("None");
                                          view.enablePIDetailsButton(false);
                                          return;
                                      }

                                      currentProcessInstanceId = details.getProcessInstanceId();
                                      view.setProcessInstanceId(String.valueOf(currentProcessInstanceId));
                                      view.setProcessId(details.getProcessId());
                                  }
                              }
        ).getTask(serverTemplateId, containerId, currentTaskId);
    }

    boolean hasAccessToPerspective(String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId, ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef, identity);
    }


    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentTaskId = event.getTaskId();
        this.serverTemplateId = event.getServerTemplateId();
        this.containerId = event.getContainerId();
        refreshProcessContextOfTask();
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshProcessContextOfTask();
        }
    }
}
