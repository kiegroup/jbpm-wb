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
package org.jbpm.console.ng.mobile.ht.client.taskdetails;

import com.google.gwt.user.client.ui.HasText;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.uberfire.security.Identity;

/**
 *
 * @author livthomas
 */
@Dependent
public class TaskDetailsPresenter {

    public interface TaskDetailsView extends MGWTUberView<TaskDetailsPresenter> {

        void refreshTask(TaskSummary task, boolean owned);

        HasText getPotentialOwnersText();

        HasText getDelegateTextBox();

        void displayNotification(String title, String message);

    }

    @Inject
    private TaskDetailsView view;

    @Inject
    private Identity identity;

    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    public TaskDetailsView getView() {
        return view;
    }

    public void refresh(final long taskId) {
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary task) {
                view.refreshTask(task, task.getActualOwner().equals(identity.getName()));
                refreshPotentialOwners(taskId);
            }
        }).getTaskDetails(taskId);
    }

    public void refreshPotentialOwners(final long taskId) {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(taskId);
        taskServices.call(new RemoteCallback<Map<Long, List<String>>>() {
            @Override
            public void callback(Map<Long, List<String>> ids) {
                if (ids.isEmpty()) {
                    view.getPotentialOwnersText().setText("No potential owners");
                } else {
                    view.getPotentialOwnersText().setText(ids.get(taskId).toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).getPotentialOwnersForTaskIds(taskIds);
    }

    public void saveTask(final long taskId) {
        // TODO with forms
    }

    public void releaseTask(final long taskId) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was released!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).release(taskId, identity.getName());
    }

    public void claimTask(final long taskId) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was claimed!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).claim(taskId, identity.getName());
    }

    public void startTask(final long taskId) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was started!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).start(taskId, identity.getName());
    }

    public void completeTask(final long taskId) {
        final Map<String, Object> params = new HashMap<String, Object>();
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was completed!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).complete(taskId, identity.getName(), params);
    }

    public void updateTask(final long taskId, String name, String description, Date dueDate, int priority) {
        List<String> descriptions = new ArrayList<String>();
        descriptions.add(description);

        List<String> names = new ArrayList<String>();
        names.add(name);

        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                view.displayNotification("Success", "Task details has been updated for the task with id = "
                        + taskId);
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).updateSimpleTaskDetails(taskId, names, priority, descriptions, dueDate);
    }

    public void delegateTask(final long taskId, String entity) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task was succesfully delegated");
                view.getDelegateTextBox().setText("");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).delegate(taskId, identity.getName(), entity);
    }

}
