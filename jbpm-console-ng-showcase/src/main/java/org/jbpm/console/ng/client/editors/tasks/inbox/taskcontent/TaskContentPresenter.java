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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskcontent;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Task Content")
public class TaskContentPresenter {

    public interface InboxView
            extends
            UberView<TaskContentPresenter> {

        void displayNotification(String text);

        VerticalPanel getContentPanel();

        VerticalPanel getOutputPanel();

        TextBox getTaskIdText();
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Task Content";
    }

    @WorkbenchPartView
    public UberView<TaskContentPresenter> getView() {
        return view;
    }

    public TaskContentPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void saveContent(Long taskId, Map<String, String> values) {
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Content Saved for Task " + taskId + "");

            }
        }).saveContent(taskId, values);
    }

    public void getContentByTaskId(Long taskId) {

        taskServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> content) {
                for (String key : content.keySet()) {
                    FlowPanel flowPanel = new FlowPanel();
                    flowPanel.setStyleName("group-row value-pair");
                    TextBox keyTextBox = new TextBox();
                    keyTextBox.setText(key);
                    TextBox valueTextBox = new TextBox();
                    valueTextBox.setText(content.get(key));
                    flowPanel.add(keyTextBox);
                    flowPanel.add(valueTextBox);
                    view.getContentPanel().add(flowPanel);
                }

            }
        }).getContentListByTaskId(taskId);

        taskServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> content) {
                for (String key : content.keySet()) {
                    FlowPanel flowPanel = new FlowPanel();
                    flowPanel.setStyleName("group-row value-pair");
                    TextBox keyTextBox = new TextBox();
                    keyTextBox.setText(key);
                    TextBox valueTextBox = new TextBox();
                    valueTextBox.setText(content.get(key));
                    flowPanel.add(keyTextBox);
                    flowPanel.add(valueTextBox);
                    view.getOutputPanel().add(flowPanel);
                }

            }
        }).getTaskOutputContentByTaskId(taskId);
    }
    
    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        long taskId = Long.parseLong(p.getParameter("taskId", "0"));
        view.getTaskIdText().setText(String.valueOf(taskId));
        getContentByTaskId(new Long(view.getTaskIdText().getText()));
    }
}
