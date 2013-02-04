/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.client.editors.taskslist;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

/**
 *
 * @author salaboy
 */
public class TaskListDayBox extends Composite implements RequiresResize {

    private List<TaskSummary> taskSummaries;
    private FlowPanel taskListBox = new FlowPanel();
    private FlowPanel dayTaskContainer = new FlowPanel();
    private FlowPanel top = new FlowPanel();
     private FlowPanel fluidRow = new FlowPanel();
    private FlowPanel span12 = new FlowPanel();
    private Label dayLabel = new Label();
    private TasksListPresenter presenter;
    private Identity identity;
    private PlaceManager placeManager;

    public TaskListDayBox(String day, List<TaskSummary> taskSummaries, Identity identity, PlaceManager placeManager, TasksListPresenter presenter) {
        fluidRow.setStyleName("row-fluid");
        span12.setStyleName("span12");
        fluidRow.add(span12);
        
        taskListBox.setStyleName("tasks-list");
        dayTaskContainer.setStyleName("day-tasks-container");
        top.setStyleName("top");
        dayLabel.setText(day);
        top.add(dayLabel);
        dayTaskContainer.add(top);
        span12.add(taskListBox);
        dayTaskContainer.add(fluidRow);
        initWidget(dayTaskContainer);
        this.taskSummaries = taskSummaries;
        taskListBox.clear();
        for (TaskSummary ts : this.taskSummaries) {
            taskListBox.add(new TaskBox(placeManager, presenter, identity, ts.getId(), ts.getName(), ts.getActualOwner(), ts.getPotentialOwners(), ts.getStatus()));
        }
    }

    public void setPresenter(TasksListPresenter presenter) {
        this.presenter = presenter;
    }

 
    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public void setTaskSummaries(List<TaskSummary> taskSummaries) {
        this.taskSummaries = taskSummaries;
    }

    public List<TaskSummary> getTaskSummaries() {
        return taskSummaries;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    public void onResize() {
    }
}
