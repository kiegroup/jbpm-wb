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
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateParser;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskPresenter;


/**
 *
 * @author livthomas
 */
public class TaskDetailsPresenter extends AbstractTaskPresenter {

    public interface TaskDetailsView extends View {

        void refreshTask(TaskSummary task, boolean owned);

        HasTapHandlers getSaveButton();

        HasTapHandlers getReleaseButton();

        HasTapHandlers getClaimButton();

        HasTapHandlers getStartButton();

        HasTapHandlers getCompleteButton();

        HasText getDescriptionTextArea();

        HasText getDueOnDateBox();

        MListBox getPriorityListBox();

        HasTapHandlers getUpdateButton();
        
        HasText getPotentialOwnersText();

        HasText getDelegateTextBox();

        HasTapHandlers getDelegateButton();

    }

    @Inject
    private TaskDetailsView view;

    private TaskSummary task;

    public TaskDetailsView getView(TaskSummary task) {
        this.task = task;
        refresh();
        return view;
    }

    public void refresh() {
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary response) {
                task = response;
                view.refreshTask(task, task.getActualOwner().equals(identity.getName()));
                refreshPotentialOwners();
            }
        }).getTaskDetails(task.getId());
    }

    public void refreshPotentialOwners() {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(task.getId());
        taskServices.call(new RemoteCallback<Map<Long, List<String>>>() {
            @Override
            public void callback(Map<Long, List<String>> ids) {
                if (ids.isEmpty()) {
                    view.getPotentialOwnersText().setText("No potential owners");
                } else {
                    view.getPotentialOwnersText().setText(ids.get(task.getId()).toString());
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

    private void saveTask() {
        // TODO with forms
    }

    private void releaseTask() {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + task.getId() + " was released!");
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).release(task.getId(), identity.getName());
    }

    private void claimTask() {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + task.getId() + " was claimed!");
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).claim(task.getId(), identity.getName());
    }

    private void startTask() {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + task.getId() + " was started!");
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).start(task.getId(), identity.getName());
    }

    private void completeTask() {
        final Map<String, Object> params = new HashMap<String, Object>();
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + task.getId() + " was completed!");
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).complete(task.getId(), identity.getName(), params);
    }

    private void updateTask(String description, Date dueDate, int priority) {
        List<String> descriptions = new ArrayList<String>();
        descriptions.add(description);

        List<String> names = new ArrayList<String>();
        names.add(task.getName());

        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                view.displayNotification("Success", "Task details has been updated for the task with id = "
                        + task.getId());
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).updateSimpleTaskDetails(task.getId(), names, priority, descriptions, dueDate);
    }

    private void delegateTask(String entity) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task was succesfully delegated");
                view.getDelegateTextBox().setText("");
                refresh();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).delegate(task.getId(), identity.getName(), entity);
    }

    @AfterInitialization
    public void setUpHandlers() {
        view.getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                AnimationHelper animationHelper = new AnimationHelper();
                RootPanel.get().clear();
                RootPanel.get().add(animationHelper);
                animationHelper.goTo(clientFactory.getTaskListPresenter().getView(), Animation.SLIDE_REVERSE);
            }
        });

        view.getSaveButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                saveTask();
            }
        });

        view.getReleaseButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                releaseTask();
            }
        });

        view.getClaimButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                claimTask();
            }
        });

        view.getStartButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                startTask();
            }
        });

        view.getCompleteButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                completeTask();
            }
        });

        view.getUpdateButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                try {
                    updateTask(view.getDescriptionTextArea().getText(), new DateParser().parse(view.getDueOnDateBox()
                            .getText()), view.getPriorityListBox().getSelectedIndex());
                } catch (ParseException ex) {
                    view.displayNotification("Wrong date format", "Enter the date in the correct format!");
                }
            }
        });

        view.getDelegateButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                delegateTask(view.getDelegateTextBox().getText());
            }
        });
    }

}
