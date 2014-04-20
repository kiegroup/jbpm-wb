/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.client.mobile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.core.client.home.HomePresenter;
import org.jbpm.console.ng.mobile.core.client.home.HomePresenter.HomeView;
import org.jbpm.console.ng.mobile.ht.client.newtask.NewTaskPresenter;
import org.jbpm.console.ng.mobile.ht.client.newtask.NewTaskPresenter.NewTaskView;
import org.jbpm.console.ng.mobile.ht.client.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.mobile.ht.client.taskdetails.TaskDetailsPresenter.TaskDetailsView;
import org.jbpm.console.ng.mobile.ht.client.tasklist.TaskListPresenter;
import org.jbpm.console.ng.mobile.ht.client.tasklist.TaskListPresenter.TaskListView;
import org.jbpm.console.ng.mobile.pr.client.definition.details.ProcessDefinitionDetailsPresenter;
import org.jbpm.console.ng.mobile.pr.client.definition.details.ProcessDefinitionDetailsPresenter.ProcessDefinitionDetailsView;
import org.jbpm.console.ng.mobile.pr.client.definition.list.ProcessDefinitionsListPresenter;
import org.jbpm.console.ng.mobile.pr.client.definition.list.ProcessDefinitionsListPresenter.ProcessDefinitionsListView;
import org.jbpm.console.ng.mobile.pr.client.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.console.ng.mobile.pr.client.instance.details.ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView;
import org.jbpm.console.ng.mobile.pr.client.instance.list.ProcessInstancesListPresenter;
import org.jbpm.console.ng.mobile.pr.client.instance.list.ProcessInstancesListPresenter.ProcessInstancesListView;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

/**
 *
 * @author salaboy
 */
@Dependent
@WorkbenchScreen(identifier = "MobilePresenter")
public class MobileWorkBenchPresenter {
    
    @Inject    
    private MGWTPlaceManager placeManager;
    
    private HomeView homeView;
    
    private ProcessDefinitionsListView processDefinitionsListView;
    
    private ProcessDefinitionDetailsView processDefinitionDetailsView;
    
    private ProcessInstancesListView processInstancesListView;
    
    private ProcessInstanceDetailsView processInstanceDetailsView;
    
    private TaskListView taskListView;
    
    private NewTaskView newTaskView;
    
    private TaskDetailsView taskDetailsView;
    
    @Inject
    private HomePresenter homePresenter;
    
    @Inject
    private ProcessDefinitionsListPresenter processDefinitionsListPresenter;
    
    @Inject
    private ProcessDefinitionDetailsPresenter processDefinitionDetailsPresenter;
    
    @Inject
    private ProcessInstancesListPresenter processInstancesListPresenter;
    
    @Inject
    private ProcessInstanceDetailsPresenter processInstanceDetailsPresenter;
    
    @Inject    
    private TaskListPresenter taskListPresenter;
    
    @Inject
    private TaskDetailsPresenter taskDetailsPresenter;
    
    @Inject
    private NewTaskPresenter newTaskPresenter;
    
    private VerticalPanel widgets;
    
    public MobileWorkBenchPresenter() {
        
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Mobile View";
    }
    
    @WorkbenchPartView
    public IsWidget getView() {
        widgets = new VerticalPanel();
        
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                Window.alert("uncaught: " + e.getMessage());
                e.printStackTrace();
            }
        });
        new Timer() {
            
            @Override
            public void run() {
                start();
                
            }
        }.schedule(1);
        
        return widgets;
    }
    
    public void start() {
        
        MGWTSettings appSetting = MGWTSettings.getAppSetting();
        
        MGWT.applySettings(appSetting);

        //build animation helper and attach it
        AnimationHelper animationHelper = new AnimationHelper();
        RootPanel.get().add(animationHelper);
        GWT.log("Adding Screens to MGWT PLACEMANAGER");
        
        homeView = homePresenter.getView();
        placeManager.addScreen("Home", homeView);
        homeView.init(homePresenter);
        
        processDefinitionDetailsView = processDefinitionDetailsPresenter.getView();
        placeManager.addScreen("Process Definition Details", processDefinitionDetailsView);
        processDefinitionDetailsView.init(processDefinitionDetailsPresenter);
        
        processDefinitionsListView = processDefinitionsListPresenter.getView();
        placeManager.addScreen("Process Definitions List", processDefinitionsListView);
        processDefinitionsListView.init(processDefinitionsListPresenter);
        
        processInstancesListView = processInstancesListPresenter.getView();
        placeManager.addScreen("Process Instances List", processInstancesListView);
        processInstancesListView.init(processInstancesListPresenter);
        
        processInstanceDetailsView = processInstanceDetailsPresenter.getView();
        placeManager.addScreen("Process Instance Details", processInstanceDetailsView);
        processInstanceDetailsView.init(processInstanceDetailsPresenter);
        
        taskListView = taskListPresenter.getView();
        placeManager.addScreen("Tasks List", taskListView);
        taskListView.init(taskListPresenter);
        
        newTaskView = newTaskPresenter.getView();
        placeManager.addScreen("New Task", newTaskView);
        newTaskView.init(newTaskPresenter);
        
        taskDetailsView = taskDetailsPresenter.getView();
        placeManager.addScreen("Task Details", taskDetailsView);
        taskDetailsView.init(taskDetailsPresenter);

        //animate
        placeManager.goTo("Home", Animation.SLIDE);
    }
}
