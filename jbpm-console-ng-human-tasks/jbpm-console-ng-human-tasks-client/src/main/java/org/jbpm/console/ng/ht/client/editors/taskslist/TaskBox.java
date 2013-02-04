/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.client.editors.taskslist;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import java.util.ArrayList;
import java.util.List;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 *
 * @author salaboy
 */
public class TaskBox extends Composite {

    private String taskName = "Default Task Name";
    private FocusPanel taskContainer = new FocusPanel();
   
    private FlowPanel taskPanel = new FlowPanel();
    private FlowPanel hourPanel = new FlowPanel();
    private FlowPanel taskPriorityPanel = new FlowPanel();
    private FlowPanel taskNamePanel = new FlowPanel();
    private FlowPanel taskOptions = new FlowPanel();
    private Label taskNameLabel = new Label();
    private long taskId = -1;
    private String actualOwner;
    private List<String> potentialOwners;
    private String status;
    private TasksListPresenter presenter;
    private Identity identity;
    private PlaceManager placeManager;
    

    public TaskBox() {
        
        
        taskPanel.setStyleName("task");
        
        taskNamePanel.add(taskNameLabel);
        hourPanel.setStyleName("hour");
        taskPanel.add(taskPriorityPanel);
        taskPanel.add(hourPanel);
        taskPriorityPanel.setStyleName("priority five");
        taskNamePanel.setStyleName("task-name");
        taskPanel.add(taskNamePanel);
        taskOptions.setStyleName("task-options");
        taskPanel.add(taskOptions);
        taskContainer.add(taskPanel);

        // All composites must call initWidget() in their constructors.
        initWidget(taskContainer);

        

    }

    public TaskBox(final PlaceManager placeManager, final TasksListPresenter presenter, final Identity identity, final long taskId, final String taskName, final String actualOwner, final List<String> potentialOwners, final String status) {
        this();
        this.taskId = taskId;
        this.taskName = taskName;
        taskNameLabel.setText(taskName);
        this.actualOwner = actualOwner;
        this.potentialOwners = potentialOwners;
        this.status = status;
        this.presenter = presenter;
        this.identity = identity;
        
        taskContainer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Task Details Popup");
                placeRequestImpl.addParameter("taskId", String.valueOf( taskId ) );
                placeManager.goTo(placeRequestImpl);
            }
        });
        
        
        List<FocusPanel> options = new ArrayList<FocusPanel>();
        FlowPanel personalOrGroupTask = new FlowPanel();

        if ("".equals(actualOwner) && !potentialOwners.isEmpty() && status.equals("Ready")) {
            personalOrGroupTask.setStyleName("group-task");
            personalOrGroupTask.add(new HTML("Group Task"));
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.claimTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Claim"));
            panel.setStyleName("clickable claim");
            options.add(focusPanel);

        } else if (!"".equals(actualOwner) && !potentialOwners.isEmpty() && !potentialOwners.contains(identity.getName()) && (status.equals("Reserved") || status.equals("InProgress"))) {
            personalOrGroupTask.setStyleName("group-task");
            personalOrGroupTask.add(new HTML("Group Task"));
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.releaseTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Release"));
            panel.setStyleName("clickable release");
            options.add(focusPanel);

        } else {
            personalOrGroupTask.setStyleName("personal-task");
            personalOrGroupTask.add(new HTML("Personal Task"));
        }

        if (status.equals("Reserved")) {
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.startTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Start"));
            panel.setStyleName("clickable start");
            options.add(focusPanel);

        }
        if (status.equals("InProgress")) {
            FlowPanel panel = new FlowPanel();
            taskPanel.setStyleName("task in-progress");
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.completeTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Complete"));
            panel.setStyleName("clickable complete");
            options.add(focusPanel);
        }

        for (FocusPanel p : options) {
            taskOptions.add(p);
        }

        taskOptions.add(personalOrGroupTask);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        taskNameLabel.setText(taskName);
    }
}
