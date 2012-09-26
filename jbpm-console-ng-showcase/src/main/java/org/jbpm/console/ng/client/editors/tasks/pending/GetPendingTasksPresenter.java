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
package org.jbpm.console.ng.client.editors.tasks.pending;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Errai UI - Get Pending Tasks")
public class GetPendingTasksPresenter {

    public interface InboxView
            extends
            UberView<GetPendingTasksPresenter> {

        void displayNotification(String text);
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Errai UI - Get Pending Tasks";
    }

    @WorkbenchPartView
    public UberView<GetPendingTasksPresenter> getView() {
        return view;
    }

    public GetPendingTasksPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void getPendingTasks(final String userName) {
        
        taskServices.call(new RemoteCallback<Integer>() {
            @Override
            public void callback(Integer pendingTasks) {
                view.displayNotification("Pending Tasks " + pendingTasks + ")");
                
            }
        }).getPendingTaskByUserId(userName);

    }
}
