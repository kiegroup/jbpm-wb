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
package org.jbpm.console.ng.client.editors.tasks.inbox.group;

import org.jbpm.console.ng.client.model.TaskSummary;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import java.util.Set;
import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "Group Tasks")
public class InboxGroupPresenter {

    public interface InboxView
            extends
            IsWidget {

        void displayNotification(String text);
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Group Tasks";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public InboxGroupPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void refreshGroupTasks(String userId, List<String> groupIds) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                dataProvider.setList(tasks);
                dataProvider.refresh();

            }
        }).getTasksAssignedAsPotentialOwner(userId, groupIds, "en-UK");

    }

    public void claimTasks(Set<TaskSummary> selectedTasks, final String userId, final List<String> groupIds) {
        for (TaskSummary ts : selectedTasks) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Task(s) Claimed");
                    refreshGroupTasks(userId, groupIds);

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
}
