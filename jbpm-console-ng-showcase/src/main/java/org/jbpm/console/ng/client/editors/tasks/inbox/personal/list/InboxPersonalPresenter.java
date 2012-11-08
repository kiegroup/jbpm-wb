/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.client.editors.tasks.inbox.personal.list;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CheckBox;

import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import java.util.ArrayList;
import org.jbpm.console.ng.client.model.TaskSummary;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@Dependent
@WorkbenchScreen(identifier = "Personal Tasks")
public class InboxPersonalPresenter {

    public interface InboxView
            extends
            UberView<InboxPersonalPresenter> {

        void displayNotification(String text);

        CheckBox getShowCompletedCheck();

        CheckBox getShowGroupTasksCheck();

        DataGrid<TaskSummary> getDataGrid();

        ColumnSortEvent.ListHandler<TaskSummary> getSortHandler();

        MultiSelectionModel<TaskSummary> getSelectionModel();
        
        public void refreshTasks();
    }
    @Inject
    private InboxView view;
    @Inject
    private Identity identity;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;
    private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();
    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
        public Object getKey(TaskSummary item) {
            return item == null ? null : item.getId();
        }
    };

    @WorkbenchPartTitle
    public String getTitle() {
        return "Personal Tasks";
    }

    @WorkbenchPartView
    public UberView<InboxPersonalPresenter> getView() {
        return view;
    }

    public InboxPersonalPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void refreshTasks(final String userId, final boolean showOnlyPersonal, final boolean showCompleted, final boolean showGroupTasks) {
        List<Role> roles = identity.getRoles();
        List<String> groups = new ArrayList<String>(roles.size());
        for (Role r : roles) {
            groups.add(r.getName().trim());
        }
        if (showCompleted) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {

                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(tasks);
                    dataProvider.refresh();
                    view.getSelectionModel().clear();

                }
            }).getTasksOwned(userId);
        } else if (showGroupTasks) {

            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(tasks);
                    dataProvider.refresh();
                    view.getSelectionModel().clear();

                }
            }).getTasksAssignedByGroups(groups, "en-UK");

        } else if (showOnlyPersonal) {
            List<String> statuses = new ArrayList<String>(4);
            statuses.add("Ready");
            statuses.add("InProgress");
            statuses.add("Created");
            statuses.add("Reserved");
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(tasks);
                    dataProvider.refresh();
                    view.getSelectionModel().clear();

                }
            }).getTasksOwned(userId, statuses,  "en-UK");



        } else {

            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(tasks);
                    dataProvider.refresh();
                    view.getSelectionModel().clear();

                }
            }).getTasksAssignedPersonalAndGroupsTasks(userId, groups, "en-UK");


        }



    }

    public void startTasks(final Set<TaskSummary> selectedTasks, final String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Task(s) Started");
                    view.refreshTasks();
                }
            }).start(ts.getId(), userId);
        }


    }

    public void releaseTasks(Set<TaskSummary> selectedTasks, final String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Task(s) Released");
                    view.refreshTasks();
                }
            }).release(ts.getId(), userId);
        }
    }

    public void completeTasks(Set<TaskSummary> selectedTasks, final String userId) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Task(s) Completed");
                    view.refreshTasks();
                }
            }).complete(ts.getId(), userId, null);
        }

    }

    public void claimTasks(Set<TaskSummary> selectedTasks, final String userId) {
        for (final TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Task (Id = " + ts.getId() + ") Claimed");
                    view.refreshTasks();

                }
            }).claim(ts.getId(), userId);
        }


    }

    public void addDataDisplay(HasData<TaskSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<TaskSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnReveal
    public void onReveal() {
        
    }
}
