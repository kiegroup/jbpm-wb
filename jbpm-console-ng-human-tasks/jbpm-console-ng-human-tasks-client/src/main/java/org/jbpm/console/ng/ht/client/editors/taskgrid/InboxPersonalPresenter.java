package org.jbpm.console.ng.ht.client.editors.taskgrid;


import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.DataGrid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

import com.google.gwt.user.cellview.client.ColumnSortEvent;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

@Dependent
@WorkbenchScreen(identifier = "Grid Tasks List")
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
            }).getTasksOwned(userId, statuses, "en-UK");



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
        List<Long> tasksIds = new ArrayList<Long>(selectedTasks.size());
        for (TaskSummary ts : selectedTasks) {
            tasksIds.add(ts.getId());
        }
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Started");
                view.refreshTasks();
            }
        }).startBatch(tasksIds, userId);



    }

    public void releaseTasks(Set<TaskSummary> selectedTasks, final String userId) {
        List<Long> tasksIds = new ArrayList<Long>(selectedTasks.size());
        for (TaskSummary ts : selectedTasks) {
            tasksIds.add(ts.getId());
        }

        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Released");
                view.refreshTasks();
            }
        }).releaseBatch(tasksIds, userId);

    }

    public void completeTasks(Set<TaskSummary> selectedTasks, final String userId) {
        List<Long> tasksIds = new ArrayList<Long>(selectedTasks.size());
        for (TaskSummary ts : selectedTasks) {
            tasksIds.add(ts.getId());
        }

        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Completed");
                view.refreshTasks();
            }
        }).completeBatch(tasksIds, userId, null);



    }

    public void claimTasks(Set<TaskSummary> selectedTasks, final String userId) {
        List<Long> tasksIds = new ArrayList<Long>(selectedTasks.size());
        for (TaskSummary ts : selectedTasks) {
            tasksIds.add(ts.getId());
        }
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Claimed");
                view.refreshTasks();

            }
        }).claimBatch(tasksIds, userId);



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

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        view.refreshTasks();
    }
}