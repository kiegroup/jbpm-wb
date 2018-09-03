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
package org.jbpm.workbench.ht.client.editors.taskcomments;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.mvp.UberView;

@Dependent
public class TaskCommentsPresenter extends AbstractTaskPresenter {

    private final TaskCommentsView view;
    private final Caller<TaskService> taskService;
    private final ListDataProvider<CommentSummary> dataProvider = new ListDataProvider<CommentSummary>();
    private Constants constants = Constants.INSTANCE;
    private User identity;
    private boolean forLog = false;
    private boolean forAdmin = false;

    @Inject
    public TaskCommentsPresenter(TaskCommentsView view,
                                 Caller<TaskService> taskService,
                                 User identity) {
        this.view = view;
        this.taskService = taskService;
        this.identity = identity;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getView() {
        return view;
    }

    public ListDataProvider<CommentSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshComments() {
        taskService.call((List<CommentSummary> comments) -> {
                             dataProvider.getList().clear();
                             dataProvider.getList().addAll(comments);
                             view.redrawDataGrid();
                         },
                         (Message message, Throwable throwable) -> {
                             if (throwable.getMessage().contains("cannot find container")) {
                                 view.setErrorMessage(constants.TaskCommentsNotAvailable(getContainerId()));
                                 return false;
                             } else {
                                 return true;
                             }
                         }).getTaskComments(getServerTemplateId(),
                                            getContainerId(),
                                            getTaskId());
    }

    public void addTaskComment(final String text) {
        if (forLog) {
            return;
        }

        if ("".equals(text.trim())) {
            view.displayNotification(constants.CommentCannotBeEmpty());
        } else {
            addTaskComment(text,
                           new Date());
        }
    }

    private void addTaskComment(final String text,
                                final Date addedOn) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void response) {
                        refreshComments();
                        view.clearCommentInput();
                    }
                }
        ).addTaskComment(getServerTemplateId(),
                         getContainerId(),
                         getTaskId(),
                         text,
                         addedOn);
    }

    public void removeTaskComment(long commentId) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void response) {
                        refreshComments();
                        view.clearCommentInput();
                        view.displayNotification(constants.CommentDeleted());
                    }
                }
        ).deleteTaskComment(getServerTemplateId(),
                            getContainerId(),
                            getTaskId(),
                            commentId);
    }

    public void addDataDisplay(final HasData<CommentSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        setSelectedTask(event);
        forAdmin = event.isForAdmin();
        forLog = event.isForLog();
        view.newCommentsEnabled(forLog == false);
        refreshComments();
    }

    public Predicate<CommentSummary> getDeleteCondition() {
        return c -> forLog == false && (forAdmin || c.getAddedBy().equals(identity.getIdentifier()));
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (isSameTaskFromEvent().test(event)) {
            refreshComments();
        }
    }

    public void onTaskCompletedEvent(@Observes final TaskCompletedEvent event) {
        if (isSameTaskFromEvent().test(event) && forLog == false) {
            forLog = true;
            view.newCommentsEnabled(false);
            refreshComments();
        }
    }

    public interface TaskCommentsView extends UberView<TaskCommentsPresenter> {

        void clearCommentInput();

        void redrawDataGrid();

        void displayNotification(String text);

        void newCommentsEnabled(Boolean enabled);

        void setErrorMessage(String message);
    }
}
