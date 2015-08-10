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
package org.jbpm.console.ng.pr.admin.client.editors.settings;

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
import org.jbpm.console.ng.pr.admin.client.i18n.ProcessAdminConstants;

import org.jbpm.console.ng.pr.admin.service.ProcessServiceAdminEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Admin Settings")
public class ProcessAdminSettingsPresenter {

    private ProcessAdminConstants processAdminConstants = GWT.create(ProcessAdminConstants.class);

    public interface ProcessAdminSettingsView extends UberView<ProcessAdminSettingsPresenter> {

        void displayNotification(String text);

        TextBox getDeploymentIdText();

        TextBox getProcessIdText();

        Button getGenerateMockInstancesButton();
    }

    @Inject
    ProcessAdminSettingsView view;

    @Inject
    Caller<ProcessServiceAdminEntryPoint> instancesAdminServices;

    private PlaceRequest place;

 

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return processAdminConstants.Process_Instances_Admin();
    }

    @WorkbenchPartView
    public UberView<ProcessAdminSettingsPresenter> getView() {
        return view;
    }

    public ProcessAdminSettingsPresenter() {
    }

    @PostConstruct
    public void init() {
    }


    public void generateMockInstances(String deployId,String processId, int amountOfTasks) {
        instancesAdminServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Process Instances succesfully created!");
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).generateMockInstances( deployId, processId, amountOfTasks );

    }

    @OnOpen
    public void onOpen() {
        view.getDeploymentIdText().setFocus( true );

    }
}
