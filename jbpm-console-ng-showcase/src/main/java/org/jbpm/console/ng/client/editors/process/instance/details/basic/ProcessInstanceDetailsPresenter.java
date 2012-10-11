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
package org.jbpm.console.ng.client.editors.process.instance.details.basic;

import com.google.gwt.user.client.ui.TextBox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PassThroughPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    public interface InboxView
            extends
            UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification(String text);


        TextBox getProcessNameText();

      
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Instance Details";
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsPresenter> getView() {
        return view;
    }

    

    public void refreshProcessDef(String processId) {
//        taskServices.call(new RemoteCallback<TaskSummary>() {
//            @Override
//            public void callback(TaskSummary details) {
//                view.getTaskIdText().setText(String.valueOf(details.getId()));
//                view.getTaskNameText().setText(details.getName());
//
//                view.getTaskDescriptionTextArea().setText(details.getDescription());
//                view.getDueDate().setValue(details.getExpirationTime());
//
//                view.getUserText().setText(details.getActualOwner());
//                int i = 0;
//                for (String strategy : view.getSubTaskStrategies()) {
//                    if (details.getSubTaskStrategy().equals(strategy)) {
//                        view.getSubTaskStrategyListBox().setSelectedIndex(i);
//                    }
//                    i++;
//                }
//                i = 0;
//                for (String priority : view.getPriorities()) {
//                    if (details.getPriority() == i) {
//                        view.getTaskPriorityListBox().setSelectedIndex(i);
//                    }
//                    i++;
//                }
//                i = 0;
//            }
//        }).getProcessDetails(taskId);

    }

    

    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        String processId = (String)((PassThroughPlaceRequest)p).getPassThroughParameter("processInstanceId", "");
        view.getProcessNameText().setText(processId);
        refreshProcessDef(processId);
    }
}
