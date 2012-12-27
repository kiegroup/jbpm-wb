/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.client.editors.tasklist.alternative;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

/**
 *
 * @author salaboy
 */
public class TaskListBox extends Composite implements RequiresResize{
   
    
    private List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>();
    private FlowPanel taskListBox = new FlowPanel();
    private FlowPanel dayTaskContainer = new FlowPanel();
    private FlowPanel top = new FlowPanel();
    private Label dayLabel = new Label();
    private TasksListPresenter presenter;
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    
    public TaskListBox() {
        taskListBox.setStyleName("tasks-list");
        dayTaskContainer.setStyleName("day-tasks-container");
        top.setStyleName("top");
        dayLabel.setText("Today");
        top.add(dayLabel);
        dayTaskContainer.add(top);
        dayTaskContainer.add(taskListBox);
        initWidget(dayTaskContainer);
    }

    public void setPresenter(TasksListPresenter presenter) {
        this.presenter = presenter;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    
    public void refresh(){
        taskListBox.clear();
        
        for(TaskSummary ts : this.taskSummaries){
           taskListBox.add(new TaskBox(placeManager, presenter, identity,  ts.getId(), ts.getName(), ts.getActualOwner(), ts.getPotentialOwners(), ts.getStatus()));
        }
        
    }

    public void setTaskSummaries(List<TaskSummary> taskSummaries) {
        this.taskSummaries = taskSummaries;
    }

    public List<TaskSummary> getTaskSummaries() {
        return taskSummaries;
    }
    
    
    
    public void onClick(ClickEvent event) {
       
    }

    public void onResize() {
        
    }

    

   
    
}
