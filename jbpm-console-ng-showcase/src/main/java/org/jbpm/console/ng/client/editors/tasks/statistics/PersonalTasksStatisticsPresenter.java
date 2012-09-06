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
package org.jbpm.console.ng.client.editors.tasks.statistics;

import org.jbpm.console.ng.client.editors.tasks.inbox.quicknewtask.*;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Personal Task Statistics")
public class PersonalTasksStatisticsPresenter {

    public interface InboxView
            extends
            UberView<PersonalTasksStatisticsPresenter> {

        void displayNotification(String text);
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Statistics";
    }

    @WorkbenchPartView
    public UberView<PersonalTasksStatisticsPresenter> getView() {
        return view;
    }

    public PersonalTasksStatisticsPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void refreshGraphs(final String userId) {
        
        taskServices.call(new RemoteCallback<Integer>() {
            @Override
            public void callback(Integer completedTasks) {
                view.displayNotification("Tasks Completed ( by "+userId + " ): "+completedTasks);
                
            }
        }).getCompletedTaskByUserId(userId);
        
        taskServices.call(new RemoteCallback<Integer>() {
            @Override
            public void callback(Integer pendingTasks) {
                view.displayNotification("Pending Tasks ( for "+userId + " ): "+pendingTasks);
                
            }
        }).getPendingTaskByUserId(userId);

    }
}
