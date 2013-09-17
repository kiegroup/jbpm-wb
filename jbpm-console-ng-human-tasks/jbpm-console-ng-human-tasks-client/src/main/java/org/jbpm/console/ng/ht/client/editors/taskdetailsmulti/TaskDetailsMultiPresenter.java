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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import com.github.gwtbootstrap.client.ui.Heading;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Task Details Multi")
public class TaskDetailsMultiPresenter {

    private Constants constants = GWT.create(Constants.class);
    @Inject
    private ActivityManager activityManager;
    @Inject
    private PlaceManager placeManager;
    
    private long selectedTaskId = 0;
    
    private String selectedTaskName = "";

    public interface TaskDetailsMultiView extends UberView<TaskDetailsMultiPresenter> {

        void displayNotification(String text);

        Heading getTaskIdAndName();

        HTMLPanel getContent();
    }
    @Inject
    Identity identity;
    @Inject
    public TaskDetailsMultiView view;
    private Menus menus;
    private PlaceRequest place;
    private Map<String, AbstractWorkbenchScreenActivity> activitiesMap = new HashMap<String, AbstractWorkbenchScreenActivity>(4);

    public TaskDetailsMultiPresenter() {
        makeMenuBar();
    }

    @WorkbenchPartView
    public UberView<TaskDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition(){
        return Position.EAST;
    }
    
    
    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnOpen
    public void onOpen() {
        WorkbenchSplitLayoutPanel splitPanel = (WorkbenchSplitLayoutPanel)view.asWidget().getParent().getParent().getParent().getParent()
                                            .getParent().getParent().getParent().getParent().getParent().getParent().getParent();
        splitPanel.setWidgetMinSize(splitPanel.getWidget(0), 500);
        
    }
    
    public void onTaskSelectionEvent(@Observes TaskSelectionEvent event){
        selectedTaskId = event.getTaskId();
        selectedTaskName = event.getTaskName();
        
        view.getTaskIdAndName().setText(String.valueOf(selectedTaskId) + " - "+selectedTaskName);
        
        view.getContent().clear();
        
        String placeToGo;
        if(event.getPlace() != null && !event.getPlace().equals("")){
            placeToGo = event.getPlace();
        }else{
            placeToGo = "Task Details";
        }
        
        

        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
        //Set Parameters here: 
        defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
        defaultPlaceRequest.addParameter("taskName", selectedTaskName);

        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        AbstractWorkbenchScreenActivity activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
        
        activitiesMap.put(placeToGo, activity);
        
        IsWidget widget = activity.getWidget();
        activity.launch(place, null);
        activity.onStartup(defaultPlaceRequest);
        view.getContent().add(widget);
        activity.onOpen();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(constants.Work())
                .respondsWith(new Command() {
            @Override
            public void execute() {
                view.getContent().clear();
                String placeToGo = "Form Display";

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
                //Set Parameters here: 

                defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
                defaultPlaceRequest.addParameter("taskName", selectedTaskName);
                AbstractWorkbenchScreenActivity activity = null;
                if(activitiesMap.get(placeToGo) == null){
                    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                    activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
                    
                }else{
                    activity = activitiesMap.get(placeToGo);
                }
                IsWidget widget = activity.getWidget();
                    
                activity.launch(place, null);
                activity.onStartup(defaultPlaceRequest);
                view.getContent().add(widget);
                activity.onOpen();

            }
        })
                .endMenu()
                .newTopLevelMenu(constants.Details())
                .respondsWith(new Command() {
            @Override
            public void execute() {
                view.getContent().clear();
                String placeToGo = "Task Details";

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
                //Set Parameters here: 
                defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
                defaultPlaceRequest.addParameter("taskName", selectedTaskName);

                AbstractWorkbenchScreenActivity activity = null;
                if(activitiesMap.get(placeToGo) == null){
                    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                    activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
                    
                }else{
                    activity = activitiesMap.get(placeToGo);
                }
                IsWidget widget = activity.getWidget();
                activity.launch(place, null);
                activity.onStartup(defaultPlaceRequest);
                view.getContent().add(widget);
                activity.onOpen();

            }
        })
                .endMenu()
                .newTopLevelMenu(constants.Assignments())
                .respondsWith(new Command() {
            @Override
            public void execute() {
                view.getContent().clear();
                String placeToGo = "Task Assignments";

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
                //Set Parameters here: 
                defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
                defaultPlaceRequest.addParameter("taskName", selectedTaskName);

                AbstractWorkbenchScreenActivity activity = null;
                if(activitiesMap.get(placeToGo) == null){
                    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                    activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
                    
                }else{
                    activity = activitiesMap.get(placeToGo);
                }
                IsWidget widget = activity.getWidget();
                activity.launch(place, null);
                activity.onStartup(defaultPlaceRequest);
                view.getContent().add(widget);
                activity.onOpen();

            }
        })
                .endMenu()
                .newTopLevelMenu(constants.Comments())
                .respondsWith(new Command() {
            @Override
            public void execute() {
                view.getContent().clear();
                String placeToGo = "Task Comments";

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
                //Set Parameters here: 
                defaultPlaceRequest.addParameter("taskId", String.valueOf(selectedTaskId));
                defaultPlaceRequest.addParameter("taskName", selectedTaskName);

                AbstractWorkbenchScreenActivity activity = null;
                if(activitiesMap.get(placeToGo) == null){
                    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                    activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
                    
                }else{
                    activity = activitiesMap.get(placeToGo);
                }
                IsWidget widget = activity.getWidget();
                activity.launch(place, null);
                activity.onStartup(defaultPlaceRequest);
                view.getContent().add(widget);
                activity.onOpen();



            }
        })
                .endMenu()
                .build();

    }
    
    @OnClose
    public void onClose(){
        for(String activityId : activitiesMap.keySet()){
            activitiesMap.get(activityId).onClose();
        }
        activitiesMap.clear();
    }
}
