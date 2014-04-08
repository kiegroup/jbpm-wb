/*
 * Copyright 2014 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.mobile.core.client.home;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

import org.jbpm.console.ng.mobile.client.AbstractView;

/**
 * 
 * @author livthomas
 */
public class HomeViewImpl extends AbstractView implements HomePresenter.HomeView {
    
    private final Button processDefinitionsButton;
    
    private final Button processInstancesButton;
    
    private final Button tasksListButton;

    public HomeViewImpl() {
        title.setHTML("jBPM Mobile");
        headerBackButton.setVisible(false);

        ScrollPanel scrollPanel = new ScrollPanel();
        layoutPanel.add(scrollPanel);

        RoundPanel homePanel = new RoundPanel();

        processDefinitionsButton = new Button("Process Definitions");
        homePanel.add(processDefinitionsButton);

        processInstancesButton = new Button("Process Instances");
        homePanel.add(processInstancesButton);

        tasksListButton = new Button("Tasks List");
        homePanel.add(tasksListButton);

        scrollPanel.add(homePanel);
    }

    @Override
    public HasTapHandlers getProcessDefinitionsButton() {
        return processDefinitionsButton;
    }

    @Override
    public HasTapHandlers getProcessInstancesButton() {
        return processInstancesButton;
    }

    @Override
    public HasTapHandlers getTasksListButton() {
        return tasksListButton;
    }

}
