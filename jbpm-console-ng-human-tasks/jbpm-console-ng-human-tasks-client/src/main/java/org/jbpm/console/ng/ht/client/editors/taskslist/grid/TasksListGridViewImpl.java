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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "TasksListGridViewImpl.html")
public class TasksListGridViewImpl extends AbstractListView<TaskSummary, TasksListGridPresenter>
        implements TasksListGridPresenter.TaskListView {

  private final Constants constants = GWT.create(Constants.class);
  private final HumanTasksImages images = GWT.create(HumanTasksImages.class);

  @Inject
  private Event<TaskSelectionEvent> taskSelected;


  private Button activeFilterButton;

  private Button personalFilterButton;

  private Button groupFilterButton;

  private Button allFilterButton;
  
  private NoSelectionModel<TaskSummary> selectionModel;
  
  private TaskSummary selectedItem;
  
  private int selectedRow;

  @Override
  public void init(final TasksListGridPresenter presenter) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("bannedColumns",constants.Task());
    params.put("initColumns",constants.Task()+","+constants.Created_On());
    super.init(presenter, params);
    
    
    listGrid.setEmptyTableCaption(constants.No_Tasks_Found());
    selectionModel = new NoSelectionModel<TaskSummary>();
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        boolean close = false;
        if(listGrid.getKeyboardSelectedRow() != selectedRow){
          
          listGrid.clearRow(selectedRow);
          selectedRow = listGrid.getKeyboardSelectedRow();
          listGrid.paintRow(selectedRow);
        }else{
          close = true;
        }
        
        selectedItem = selectionModel.getLastSelectedObject();

        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));

        if (status == PlaceStatus.CLOSE) {
          placeManager.goTo("Task Details Multi");
          taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName()));
        } else if (status == PlaceStatus.OPEN && !close) {
          taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName()));
        } else if (status == PlaceStatus.OPEN && close) {
          placeManager.closePlace("Task Details Multi");
        }
        
      }
    });

    listGrid.setSelectionModel(selectionModel);

    initExtraButtons();
    initFiltersBar();
  }

  private void initFiltersBar() {
    HorizontalPanel filtersBar = new HorizontalPanel();
    Label filterLabel = new Label();
    filterLabel.setStyleName("");
    filterLabel.setText(constants.Filters() + ": ");

    activeFilterButton = new Button();
    activeFilterButton.setIcon(IconType.FILTER);
    activeFilterButton.setSize(ButtonSize.SMALL);
    activeFilterButton.setText(constants.Active());
    activeFilterButton.setEnabled(false);
    activeFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(false);
        personalFilterButton.setEnabled(true);
        groupFilterButton.setEnabled(true);
        allFilterButton.setEnabled(true);
        presenter.refreshActiveTasks();
      }
    });

    personalFilterButton = new Button();
    personalFilterButton.setIcon(IconType.FILTER);
    personalFilterButton.setSize(ButtonSize.SMALL);
    personalFilterButton.setText(constants.Personal());
    personalFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        personalFilterButton.setEnabled(false);
        groupFilterButton.setEnabled(true);
        allFilterButton.setEnabled(true);
        presenter.refreshPersonalTasks();
      }
    });

    groupFilterButton = new Button();
    groupFilterButton.setIcon(IconType.FILTER);
    groupFilterButton.setSize(ButtonSize.SMALL);
    groupFilterButton.setText(constants.Group());
    groupFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        personalFilterButton.setEnabled(true);
        groupFilterButton.setEnabled(false);
        allFilterButton.setEnabled(true);
        presenter.refreshGroupTasks();
      }
    });

    allFilterButton = new Button();
    allFilterButton.setIcon(IconType.FILTER);
    allFilterButton.setSize(ButtonSize.SMALL);
    allFilterButton.setText(constants.All());
    allFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        personalFilterButton.setEnabled(true);
        groupFilterButton.setEnabled(true);
        allFilterButton.setEnabled(false);
        presenter.refreshAllTasks();
      }
    });

    filtersBar.add(filterLabel);
    ButtonGroup filtersButtonGroup = new ButtonGroup(activeFilterButton, personalFilterButton,
            groupFilterButton, allFilterButton);
    
    filtersBar.add(filtersButtonGroup);

    listGrid.getCenterToolbar().add(filtersBar);

   
  }

  private void initExtraButtons() {
    Button newTaskButton = new Button();
    newTaskButton.setTitle(constants.New_Task());
    newTaskButton.setIcon(IconType.PLUS_SIGN);
    newTaskButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        placeManager.goTo(new DefaultPlaceRequest("Quick New Task"));
      }
    });
    listGrid.getLeftToolbar().add(newTaskButton);

  }

  @Override
  public void initColumns() {
    initCellPreview();
    Column taskIdColumn = initTaskIdColumn();
    
    listGrid.addColumn(taskIdColumn, constants.Id());
    
    Column taskNameColumn = initTaskNameColumn();
    
    listGrid.addColumn(taskNameColumn, constants.Task());
    
    Column taskPriorityColumn = initTaskPriorityColumn();
    
    listGrid.addColumn(taskPriorityColumn, constants.Priority());
    
    Column statusColumn = initTaskStatusColumn();
    
    listGrid.addColumn(statusColumn, constants.Status());
    
    
    Column createdOnDateColumn = initTaskCreatedOnColumn();
    
    listGrid.addColumn(createdOnDateColumn, constants.Created_On());
    
    Column dueDateColumn = initTaskDueColumn();
    
    listGrid.addColumn(dueDateColumn, constants.Due_On());
  //  initActionsColumn();
  }

  private void initCellPreview() {
    listGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<TaskSummary>() {

      @Override
      public void onCellPreview(final CellPreviewEvent<TaskSummary> event) {

        
        if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
          onMouseOverGrid(event);
        }

      }
    });

  }

  private void onMouseOverGrid(final CellPreviewEvent<TaskSummary> event) {
    TaskSummary task = event.getValue();
    
    if (task.getDescription() != null) {
      listGrid.setTooltip(listGrid.getKeyboardSelectedRow(), event.getColumn(), task.getDescription());
    }
  }

  private Column initTaskIdColumn() {
    Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>(new NumberCell()) {
      @Override
      public Number getValue(TaskSummary object) {
        return object.getTaskId();
      }
    };
    taskIdColumn.setSortable(true);
    return taskIdColumn;

  }

  private Column initTaskNameColumn() {
    Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>(new TextCell()) {
      @Override
      public String getValue(TaskSummary object) {
        return object.getTaskName();
      }
    };
    taskNameColumn.setSortable(true);
    return taskNameColumn;
  }

  private Column initTaskPriorityColumn() {
    Column<TaskSummary, Number> taskPriorityColumn = new Column<TaskSummary, Number>(new NumberCell()) {
      @Override
      public Number getValue(TaskSummary object) {
        return object.getPriority();
      }
    };
    taskPriorityColumn.setSortable(true);
    return taskPriorityColumn;

  }

  private Column initTaskStatusColumn() {
    Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>(new TextCell()) {
      @Override
      public String getValue(TaskSummary object) {
        return object.getStatus();
      }
    };
    statusColumn.setSortable(true);
    return statusColumn;

  }

  private Column initTaskCreatedOnColumn() {
    Column<TaskSummary, String> createdOnDateColumn = new Column<TaskSummary, String>(new TextCell()) {
      @Override
      public String getValue(TaskSummary object) {
        if (object.getCreatedOn() != null) {
          Date createdOn = object.getCreatedOn();
          DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
          return format.format(createdOn);
        }
        return "";
      }
    };
    createdOnDateColumn.setSortable(true);
    return createdOnDateColumn;

  }

  private Column initTaskDueColumn() {
    Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>(new TextCell()) {
      @Override
      public String getValue(TaskSummary object) {
        if (object.getExpirationTime() != null) {
          Date expirationTime = object.getExpirationTime();
          DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
          return format.format(expirationTime);
        }
        return "";
      }
    };
    dueDateColumn.setSortable(true);
    return dueDateColumn;

  }

 

  public void changeRowSelected(@Observes TaskStyleEvent taskStyleEvent) {
//    if (taskStyleEvent.getTaskEventId() != null && this.getCurrentView() == TaskView.GRID) {
//      DataGridUtils.paintRowSelected(myTaskListGrid, String.valueOf(taskStyleEvent.getTaskEventId()));
//    }
//    if (currentTaskType.equals(TaskType.ALL)) {
//      DataGridUtils.paintRowsCompleted(myTaskListGrid);
//    }
//    currentAction = null;

  }

  public void refreshNewTask(@Observes NewTaskEvent newTask) {
    presenter.refreshGrid();
    PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
    if (status == PlaceStatus.OPEN) {
      taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
    }else{
      placeManager.goTo("Task Details Multi");
      taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
    }
    
    selectionModel.setSelected(new TaskSummary(newTask.getNewTaskId(), newTask.getNewTaskName()), true);
  }

  public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
    presenter.refreshGrid();
  }

  
}
