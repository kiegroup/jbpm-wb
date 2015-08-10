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
package org.jbpm.console.ng.ht.admin.client.editors.settings;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.admin.client.i18n.Constants;
import org.jbpm.console.ng.ht.admin.service.TaskServiceAdminEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Tasks Admin Settings")
public class TaskAdminSettingsPresenter {

    private Constants constants = GWT.create(Constants.class);

    public interface TaskAdminSettingsView extends UberView<TaskAdminSettingsPresenter> {

        void displayNotification(String text);

        TextBox getUserNameText();

        Button getGenerateMockTasksButton();
    }

    @Inject
    TaskAdminSettingsView view;

    @Inject
    Caller<TaskServiceAdminEntryPoint> taskAdminServices;

    private PlaceRequest place;

 

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List_Admin();
    }

    @WorkbenchPartView
    public UberView<TaskAdminSettingsPresenter> getView() {
        return view;
    }

    public TaskAdminSettingsPresenter() {
    }

    @PostConstruct
    public void init() {
    }

//            //System.out.println(" FIRST OPTION -> Groups were I'm Included  and I want to be autoassigned add/start/claim!!");
//            taskServices.call( new RemoteCallback<Long>() {
//                @Override
//                public void callback( Long taskId ) {
//                    refreshNewTask(taskId, taskName, "Task Created and Started (id = " + taskId + ")");
//                }
//            }, new ErrorCallback<Message>() {
//                   @Override
//                   public boolean error( Message message, Throwable throwable ) {
//                       ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//                       return true;
//                   }
//               } ).addTaskAndClaimAndStart( str, null, identity.getName(), templateVars );
//        
    public void generateMockTasks(String userName, int amountOfTasks) {
        taskAdminServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Task succesfully created!");
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).generateMockTasks(userName, amountOfTasks);

    }

    @OnOpen
    public void onOpen() {
        view.getUserNameText().setFocus(true);

    }
}
