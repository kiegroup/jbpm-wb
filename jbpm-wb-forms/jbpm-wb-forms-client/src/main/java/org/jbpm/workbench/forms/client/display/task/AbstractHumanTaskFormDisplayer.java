/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.forms.client.display.task;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayer;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.FormDisplayerConfig;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.ht.model.TaskKey;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.ht.util.TaskStatus;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

public abstract class AbstractHumanTaskFormDisplayer<S extends FormRenderingSettings> implements HumanTaskFormDisplayer<S> {

    protected long taskId = -1;
    protected S renderingSettings;
    protected String taskName;
    protected String serverTemplateId;
    protected String deploymentId;

    protected FormPanel container = GWT.create(FormPanel.class);
    protected ButtonGroup buttonsContainer = GWT.create(ButtonGroup.class);
    protected FlowPanel formContainer = GWT.create(FlowPanel.class);
    protected Constants constants = GWT.create(Constants.class);

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    protected Caller<TaskService> taskService;

    @Inject
    protected Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    protected Event<TaskCompletedEvent> taskCompleted;

    @Inject
    protected User identity;

    private Command onClose;

    private Command onRefresh;

    protected abstract void initDisplayer();

    protected abstract void completeFromDisplayer();

    protected abstract void saveStateFromDisplayer();

    protected abstract void startFromDisplayer();

    protected abstract void claimFromDisplayer();

    protected abstract void releaseFromDisplayer();

    @PostConstruct
    protected void init() {
        container.getElement().setId("form-data");
        container.add(formContainer);
    }

    @Override
    public void init(FormDisplayerConfig<TaskKey, S> config,
                     Command onCloseCommand,
                     Command onRefreshCommand) {

        if (this.renderingSettings != null) {
            clearRenderingSettings();
            clearStatus();
        }

        this.serverTemplateId = config.getKey().getServerTemplateId();
        this.taskId = config.getKey().getTaskId();
        this.deploymentId = config.getKey().getDeploymentId();
        this.renderingSettings = config.getRenderingSettings();
        this.onClose = onCloseCommand;
        this.onRefresh = onRefreshCommand;

        if (renderingSettings == null) {
            return;
        }

        taskService.call(new RemoteCallback<TaskSummary>() {
                             @Override
                             public void callback(final TaskSummary task) {
                                 if (task == null) {
                                     return;
                                 }
                                 buttonsContainer.clear();
                                 taskName = task.getName();
                                 deploymentId = task.getDeploymentId();
                                 if (TaskStatus.TASK_STATUS_READY.equals(task.getTaskStatus())) {
                                     Button claimButton = new Button();
                                     claimButton.setType(ButtonType.PRIMARY);
                                     claimButton.setText(constants.Claim());
                                     claimButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             claimFromDisplayer();
                                         }
                                     });
                                     buttonsContainer.add(claimButton);
                                 }

                                 if (TaskStatus.TASK_STATUS_RESERVED.equals(task.getTaskStatus()) && task.getActualOwner().equals(identity.getIdentifier())) {

                                     Button releaseButton = new Button();
                                     releaseButton.setText(constants.Release());
                                     releaseButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             releaseFromDisplayer();
                                         }
                                     });
                                     buttonsContainer.add(releaseButton);

                                     Button startButton = new Button();
                                     startButton.setType(ButtonType.PRIMARY);
                                     startButton.setText(constants.Start());
                                     startButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             startFromDisplayer();
                                         }
                                     });
                                     buttonsContainer.add(startButton);
                                 } else if (TaskStatus.TASK_STATUS_IN_PROGRESS.equals(task.getTaskStatus()) && task.getActualOwner().equals(identity.getIdentifier())) {
                                     Button saveButton = new Button();
                                     saveButton.setText(constants.Save());
                                     saveButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             saveStateFromDisplayer();
                                         }
                                     });
                                     buttonsContainer.add(saveButton);

                                     Button releaseButton = new Button();
                                     releaseButton.setText(constants.Release());
                                     releaseButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             releaseFromDisplayer();
                                         }
                                     });
                                     buttonsContainer.add(releaseButton);

                                     Button completeButton = new Button();
                                     completeButton.setType(ButtonType.PRIMARY);
                                     completeButton.setText(constants.Complete());
                                     completeButton.addClickHandler(new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             completeFromDisplayer();
                                         }
                                     });

                                     buttonsContainer.add(completeButton);
                                 }
                                 initDisplayer();
                             }
                         },
                         getUnexpectedErrorCallback()).getTask(serverTemplateId,
                                                               deploymentId,
                                                               taskId);
    }

    protected void clearRenderingSettings() {
        this.renderingSettings = null;
    }

    @Override
    public void complete(Map<String, Object> params) {
        taskService.call(getCompleteTaskRemoteCallback(),
                         getUnexpectedErrorCallback())
                .completeTask(serverTemplateId,
                              deploymentId,
                              taskId,
                              params);
    }

    @Override
    public void claim() {
        taskService.call(getClaimTaskCallback(),
                         getUnexpectedErrorCallback()).claimTask(serverTemplateId,
                                                                 deploymentId,
                                                                 taskId);
    }

    @Override
    public void release() {
        taskService.call(getReleaseTaskRemoteCallback(),
                         getUnexpectedErrorCallback()).releaseTask(serverTemplateId,
                                                                   deploymentId,
                                                                   taskId);
    }

    @Override
    public void saveState(Map<String, Object> state) {
        taskService.call(getSaveTaskStateCallback(),
                         getUnexpectedErrorCallback()).saveTaskContent(serverTemplateId,
                                                                       deploymentId,
                                                                       taskId,
                                                                       state);
    }

    @Override
    public void start() {
        taskService.call(getStartTaskRemoteCallback(),
                         getUnexpectedErrorCallback()).startTask(serverTemplateId,
                                                                 deploymentId,
                                                                 taskId);
    }

    @Override
    public Panel getContainer() {
        return container;
    }

    @Override
    public IsWidget getFooter() {
        return buttonsContainer;
    }

    protected RemoteCallback getStartTaskRemoteCallback() {
        return (RemoteCallback<Void>) nothing -> {
            taskRefreshed.fire(new TaskRefreshedEvent(serverTemplateId,
                                                      deploymentId,
                                                      taskId));
            refresh();
        };
    }

    protected RemoteCallback getClaimTaskCallback() {
        return (RemoteCallback<Void>) nothing -> {
            taskRefreshed.fire(new TaskRefreshedEvent(serverTemplateId,
                                                      deploymentId,
                                                      taskId));
            refresh();
        };
    }

    protected RemoteCallback getSaveTaskStateCallback() {
        return (RemoteCallback<Long>) contentId -> {
            taskRefreshed.fire(new TaskRefreshedEvent(serverTemplateId,
                                                      deploymentId,
                                                      taskId));
            refresh();
        };
    }

    protected RemoteCallback getReleaseTaskRemoteCallback() {
        return (RemoteCallback<Void>) nothing -> {
            taskRefreshed.fire(new TaskRefreshedEvent(serverTemplateId,
                                                      deploymentId,
                                                      taskId));
            refresh();
        };
    }

    protected RemoteCallback<Void> getCompleteTaskRemoteCallback() {
        return nothing -> {
            taskCompleted.fire(new TaskCompletedEvent(serverTemplateId,
                                                      deploymentId,
                                                      taskId));
            close();
        };
    }

    protected ErrorCallback<Message> getUnexpectedErrorCallback() {
        return (message, throwable) -> {
            String notification = constants.UnexpectedError(throwable.getMessage());
            errorPopup.showMessage(notification);
            return true;
        };
    }

    @Override
    public void addOnCloseCallback(Command callback) {
        this.onClose = callback;
    }

    @Override
    public void addOnRefreshCallback(Command callback) {
        this.onRefresh = callback;
    }

    protected void refresh() {
        if (this.onRefresh != null) {
            this.onRefresh.execute();
        }
    }

    @Override
    public void close() {
        if (this.onClose != null) {
            this.onClose.execute();
        }
        clearStatus();
    }

    protected void clearStatus() {
        taskId = -1;
        renderingSettings = null;
        taskName = null;
        deploymentId = null;
        serverTemplateId = null;

        buttonsContainer.clear();
        formContainer.clear();

        onClose = null;
        onRefresh = null;
    }
}
