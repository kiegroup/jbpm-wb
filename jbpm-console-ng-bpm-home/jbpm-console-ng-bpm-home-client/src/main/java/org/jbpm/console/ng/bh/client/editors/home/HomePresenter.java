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
package org.jbpm.console.ng.bh.client.editors.home;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
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

    public interface HomeView
            extends
            UberView<HomePresenter> {

        void displayNotification(String text);

        SuggestBox getActionText();
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    HomeView view;
    // Retrieve the actions from a service
    Map<String, String> actions = new HashMap<String, String>();

    @PostConstruct
    public void init() {
       
    }

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

        


    }

    
    @EventHandler("discoverLabel")
    public void discover() {

        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Discover");
        dialogBox.setPopupPosition(500, 200);
        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What kind of behavior do you want define?");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);

        KeyPressHandler keyPressHandler = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 27) {

                    dialogBox.hide();


                }
            }
        };
        Button processesButton = new Button("Processes");
        dialogContents.add(processesButton);
        Button rulesButton = new Button("Rules");
        dialogContents.add(rulesButton);

        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);

        dialogBox.addHandler(keyPressHandler, KeyPressEvent.getType());

        dialogBox.show();
    }
    
    @EventHandler("designLabel")
    public void design() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Design");
        dialogBox.setPopupPosition(500, 200);

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();

        HorizontalPanel options = new HorizontalPanel();
        
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What kind of model do you want to create?");
        dialogContents.add(details);
        


        Button processesButton = new Button("Processes");
        options.add(processesButton);
        Button rulesButton = new Button("Rules");
        options.add(rulesButton);
        Button formsButton = new Button("Forms");
        options.add(formsButton);
        Button dataButton = new Button("Data Models");
        options.add(dataButton);
        
       

        dialogContents.add(options);
        
        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);

        dialogBox.show();

    }
    @EventHandler("deployLabel")
    public void deploy() {
       final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Deploy");
        dialogBox.setPopupPosition(500, 200);

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();

        HorizontalPanel options = new HorizontalPanel();
        
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What kind runtime configurations do you want to define?");
        dialogContents.add(details);
        


        Button processesButton = new Button("Process Runtime");
        options.add(processesButton);
        Button rulesButton = new Button("Rules Runtime");
        options.add(rulesButton);
        Button eventsButton = new Button("Events Runtime");
        options.add(eventsButton);
        Button serviceButton = new Button("Service Connectors");
        options.add(serviceButton);
        Button datasourcesButton = new Button("Data Sources");
        options.add(datasourcesButton);
        
       

        dialogContents.add(options);
        
        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);

        dialogBox.show();

    }
    @EventHandler("workLabel")
    public void work() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Work Popup Selector");
        placeManager.goTo(placeRequestImpl);

    }
    @EventHandler("monitorLabel")
    public void monitor() {
         final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Monitor");
        dialogBox.setPopupPosition(500, 200);

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();

        HorizontalPanel options = new HorizontalPanel();
        
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What do you want to do?");
        dialogContents.add(details);
        

        Button tasksButton = new Button("Personal Statistics", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                
            }
        });
        options.add(tasksButton);
        
        Button processesButton = new Button("Business Activity Monitoring", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                
            }
        });
        options.add(processesButton);
        
        
        
       

        dialogContents.add(options);
        
        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);

        dialogBox.show();
    }
    @EventHandler("improveLabel")
    public void improve() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Improve");
        dialogBox.setPopupPosition(500, 200);

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();

        HorizontalPanel options = new HorizontalPanel();
        
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What do you want to do?");
        dialogContents.add(details);
        

        Button tasksButton = new Button("Write Notes", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                
            }
        });
        options.add(tasksButton);
        
        Button processesButton = new Button("Data Mining", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                
            }
        });
        options.add(processesButton);
        
        
        
       

        dialogContents.add(options);
        
        Button closeButton = new Button(
                "Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);

        dialogBox.show();
    }
}
