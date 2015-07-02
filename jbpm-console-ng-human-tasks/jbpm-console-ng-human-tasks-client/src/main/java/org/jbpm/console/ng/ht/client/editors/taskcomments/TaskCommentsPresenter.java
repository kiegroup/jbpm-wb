/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.taskcomments;

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskCommentsService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class TaskCommentsPresenter {

    public interface TaskCommentsView extends UberView<TaskCommentsPresenter> {

        void clearCommentInput();

        void redrawDataGrid();

        void adjustDisplayForListOfSize(int size);

        void displayNotification(String text);
    }

    @Inject
    private TaskCommentsView view;

    @Inject
    private User identity;

    private long currentTaskId = 0;

    @Inject
    Caller<TaskCommentsService> taskCommentsServices;

    private ListDataProvider<CommentSummary> dataProvider = new ListDataProvider<CommentSummary>();

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
        taskCommentsServices.call(
                new RemoteCallback<List<CommentSummary>>() {
                    @Override
                    public void callback(List<CommentSummary> comments) {
                        dataProvider.getList().clear();
                        dataProvider.getList().addAll(comments);
                        dataProvider.refresh();
                        view.adjustDisplayForListOfSize(comments.size());
                        view.redrawDataGrid();
                    }
                },
                new ShowErrorInModalCallback()
        ).getAllCommentsByTaskId(currentTaskId);
    }

    public void addTaskComment(final String text) {
        if ("".equals(text.trim())) {
            view.displayNotification("The Comment cannot be empty!");
        } else {
            addTaskComment(text, new Date());
        }
    }

    private void addTaskComment(final String text, final Date addedOn) {
        taskCommentsServices.call(
                new RemoteCallback<Long>() {
                    @Override
                    public void callback(Long response) {
                        refreshComments();
                        view.clearCommentInput();
                    }
                },
                new ShowErrorInModalCallback()
        ).addComment(currentTaskId, text, identity.getIdentifier(), addedOn);
    }

    public void removeTaskComment(long commentId) {
        taskCommentsServices.call(
                new RemoteCallback<Long>() {
                    @Override
                    public void callback(Long response) {
                        refreshComments();
                        view.clearCommentInput();
                        view.displayNotification("Comment Deleted!");
                    }
                },
                new ShowErrorInModalCallback()
        ).deleteComment(currentTaskId, commentId);
    }

    public void addDataDisplay(final HasData<CommentSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        currentTaskId = event.getTaskId();
        refreshComments();
        view.redrawDataGrid();
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshComments();
            view.redrawDataGrid();
        }
    }

    private static class ShowErrorInModalCallback implements ErrorCallback<Message> {

        @Override
        public boolean error(Message message, Throwable throwable) {
            ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
            return true;
        }
    }
}
