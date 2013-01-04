/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.client.editors.tasklist.alternative;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

/**
 *
 * @author salaboy
 */
public class TaskListMultiDayBox extends Composite implements RequiresResize {

    private FlowPanel tasksContainer = new FlowPanel();
    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;

    private TasksListPresenter presenter;
    
    private Map<String, List<TaskSummary>> sectionTasks = new HashMap<String, List<TaskSummary>>();
    
    public TaskListMultiDayBox() {
        tasksContainer.setStyleName("task-container");
        initWidget(tasksContainer);
    }
    
     public void refresh(){
        tasksContainer.clear();
        for(String section : sectionTasks.keySet()){
           
           tasksContainer.add(new TaskListDayBox(section, sectionTasks.get(section), identity, placeManager, presenter ));
        }
        
    }
    public void onResize() {
    }

    public TasksListPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(TasksListPresenter presenter) {
        this.presenter = presenter;
    }
    
    public void addTasksByDay(String day, List<TaskSummary> taskSummaries){
        sectionTasks.put(day, taskSummaries);
    }
    
}
