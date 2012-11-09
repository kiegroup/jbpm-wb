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
package org.jbpm.console.ng.client.editors.home;

import com.google.gwt.user.client.ui.SuggestBox;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;


import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.model.ProcessSummary;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Home Screen")
public class HomePresenter {

    public interface InboxView
            extends
            UberView<HomePresenter> {

        void displayNotification(String text);

        SuggestBox getActionText();
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;
    @Inject
    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    // Retrieve the actions from a service
    Map<String, String> actions = new HashMap<String, String>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Home Screen";
    }

    @WorkbenchPartView
    public UberView<HomePresenter> getView() {
        return view;
    }

    public void doAction(String action) {
        String locatedAction = actions.get(action);
        if (locatedAction == null || locatedAction.equals("")) {
            view.displayNotification(" Action Not Implemented Yet!");
            return;
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(locatedAction);
//        placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));

        placeManager.goTo(placeRequestImpl);

    }

    @OnReveal
    public void onReveal() {
        actions.put("Show me my pending Tasks", "Personal Tasks");
        actions.put("Show me my Inbox", "Inbox Perspective");
        actions.put("I want to start a new Process", "Process Runtime Perspective");
        actions.put("I want to design a new Process Model", "Process Designer Perspective");
        actions.put("I want to create a Task", "Quick New Task");
        actions.put("Show me all the pending tasks in my Group", "Group Tasks");

        //Try to initialize the database before all the other screens.
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                knowledgeServices.call(new RemoteCallback<Integer>() {
                    @Override
                    public void callback(Integer processes) {
                    }
                }).getAmountOfSessions();
            }
        }).getTasksOwned("");


    }
}
