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

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Dependent
public class TaskCommentsPresenter extends AbstractTaskPresenter {

    public static final int COMMENTS_PAGE_SIZE = 10;
    public static final int WORK_COMMENTS_PAGE_SIZE = 3;

    protected Caller<TaskService> taskService;
    protected Constants constants = Constants.INSTANCE;
    protected User identity;
    protected boolean forLog = false;
    protected boolean forAdmin = false;
    protected boolean sortAsc = false;
    protected int currentPage = 1;
    protected int pageSize = COMMENTS_PAGE_SIZE;

    public TaskCommentsPresenter.TaskCommentsView view;

    @PostConstruct
    public void init() {
        view = taskCommentsViewProvider.get();
        view.init(this);
    }

    @Inject
    private ManagedInstance<TaskCommentsViewImpl> taskCommentsViewProvider;

    public IsWidget getView() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void showCommentsHeader() {
        view.showCommentHeader();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    private void loadTaskComments() {
        taskService.call(
                (List<CommentSummary> allComments) -> {
                    if (allComments != null) {
                        view.setCommentList(allComments.stream()
                                                    .sorted((isSortAsc() ?
                                                            comparing(CommentSummary::getAddedAt) :
                                                            comparing(CommentSummary::getAddedAt).reversed()))
                                                    .collect(toList())
                                                    .subList(0, Math.min(allComments.size(), getPageSize() * getCurrentPage())));
                        if (allComments.size() <= (getPageSize() * getCurrentPage())) {
                            view.hideLoadButton();
                        } else {
                            view.showLoadButton();
                        }
                    }
                }
        ).getTaskComments(getServerTemplateId(),
                          getContainerId(),
                          getTaskId());
    }

    public void refreshCommentsView() {
        setCurrentPage(1);
        view.clearCommentInputForm();
        loadTaskComments();
    }

    public void loadMoreTaskComments() {
        setCurrentPage(getCurrentPage() + 1);
        loadTaskComments();
    }

    public void sortComments(final boolean sortAsc) {
        setSortAsc(sortAsc);

        refreshCommentsView();
    }

    public void addTaskComment(final String text) {
        if (forLog) {
            return;
        }
        taskService.call((Void response) -> {
                             refreshCommentsView();
                             view.resetPagination();
                         }
        ).addTaskComment(getServerTemplateId(),
                         getContainerId(),
                         getTaskId(),
                         text,
                         new Date());
    }

    public void removeTaskComment(long commentId) {
        taskService.call((Void response) -> {
                             refreshCommentsView();
                             displayNotification(constants.CommentDeleted());
                         }
        ).deleteTaskComment(getServerTemplateId(),
                            getContainerId(),
                            getTaskId(),
                            commentId);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        setSelectedTask(event);
        forAdmin = event.isForAdmin();
        forLog = event.isForLog();
        if (forLog) {
            view.disableNewComments();
        }
        refreshCommentsView();
    }

    public Predicate<CommentSummary> getDeleteCondition() {
        return c -> forLog == false && (forAdmin || c.getAddedBy().equals(identity.getIdentifier()));
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (isSameTaskFromEvent().test(event)) {
            refreshCommentsView();
        }
    }

    public void onTaskCompletedEvent(@Observes final TaskCompletedEvent event) {
        if (isSameTaskFromEvent().test(event) && forLog == false) {
            forLog = true;
            view.disableNewComments();
            refreshCommentsView();
        }
    }

    @Inject
    public void setTaskService(final Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    @Inject
    public void setIdentity(User identity) {
        this.identity = identity;
    }

    public TaskCommentsView getTaskCommentView() {
        return view;
    }

    public interface TaskCommentsView extends UberElement<TaskCommentsPresenter> {

        void disableNewComments();

        void clearCommentInputForm();

        void setCommentList(List<CommentSummary> commentList);

        void resetPagination();

        void hideLoadButton();

        void showLoadButton();

        void showCommentHeader();
    }

    public interface CommentAction extends Command {

        String label();
    }
}
