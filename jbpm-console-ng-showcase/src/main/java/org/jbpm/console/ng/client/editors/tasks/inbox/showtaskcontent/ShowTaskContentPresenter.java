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
package org.jbpm.console.ng.client.editors.tasks.inbox.showtaskcontent;

import com.google.gwt.user.client.ui.HorizontalPanel;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Show Task Content")
public class ShowTaskContentPresenter {

    public interface InboxView
            extends
            UberView<ShowTaskContentPresenter> {

        void displayNotification(String text);

        VerticalPanel getContentPanel();
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Show Task Content";
    }

    @WorkbenchPartView
    public UberView<ShowTaskContentPresenter> getView() {
        return view;
    }

    public ShowTaskContentPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void getContent(Long contentId) {
        taskServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> content) {
                for (String key : content.keySet()) {
                    HorizontalPanel horizontalPanel = new HorizontalPanel();
                    TextBox keyTextBox = new TextBox();
                    keyTextBox.setText(key);
                    TextBox valueTextBox = new TextBox();
                    valueTextBox.setText(content.get(key));
                    horizontalPanel.add(keyTextBox);
                    horizontalPanel.add(valueTextBox);
                    view.getContentPanel().add(horizontalPanel);
                }

            }
        }).getContentListById(contentId);
    }
}
