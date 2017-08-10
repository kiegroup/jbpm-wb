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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@Dependent
public class TaskProcessContextPresenter {

    public User identity;

    private PlaceManager placeManager;

    private ActivityManager activityManager;

    private TaskProcessContextView view;

    private AuthorizationManager authorizationManager;

    private Long currentProcessInstanceId = -1L;


    @Inject
    public TaskProcessContextPresenter(TaskProcessContextView view,
                                       PlaceManager placeManager,
                                       ActivityManager activityManager,
                                       AuthorizationManager authorizationManager,
                                       User identity) {
        this.view = view;
        this.placeManager = placeManager;
        this.activityManager = activityManager;
        this.authorizationManager = authorizationManager;
        this.identity = identity;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        boolean enableProcessInstanceLink = false;
        if (hasAccessToPerspective(PerspectiveIds.PROCESS_INSTANCES) &&
                !activityManager.getActivities(new DefaultPlaceRequest(PerspectiveIds.PROCESS_INSTANCES)).isEmpty()) {
            enableProcessInstanceLink = true;
        }
        view.enablePIDetailsButton(enableProcessInstanceLink);
    }

    public IsWidget getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {
        final PlaceRequest request = new DefaultPlaceRequest(PerspectiveIds.PROCESS_INSTANCES);
        request.addParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                             currentProcessInstanceId.toString());
        placeManager.goTo(request);
    }

    public void setProcessContextData(Long processInstanceId,
                                      String processId) {

        if (processInstanceId == null) {
            view.setProcessInstanceId("None");
            view.setProcessId("None");
            view.enablePIDetailsButton(false);
            return;
        }

        view.setProcessInstanceId(String.valueOf(processInstanceId));
        view.setProcessId(processId);
        view.enablePIDetailsButton(true);

    }

    boolean hasAccessToPerspective(String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                  ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              identity);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentProcessInstanceId = event.getProcessInstanceId();
        setProcessContextData(event.getProcessInstanceId(),
                              event.getProcessId());
    }

    public interface TaskProcessContextView extends UberView<TaskProcessContextPresenter> {

        void displayNotification(String text);

        void setProcessInstanceId(String none);

        void setProcessId(String none);

        void enablePIDetailsButton(boolean enable);
    }
}
