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
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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


  @Override
  public void init(final TasksListGridPresenter presenter) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("bannedColumns", constants.Task());
    params.put("initColumns", constants.Task() + "," + constants.Description());
    super.init(presenter, params);

    listGrid.setEmptyTableCaption(constants.No_Tasks_Found());
    selectionModel = new NoSelectionModel<TaskSummary>();
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        boolean close = false;
        if(selectedRow == -1){
          selectedRow = listGrid.getKeyboardSelectedRow();
          listGrid.paintRow(selectedRow);
        }else if (listGrid.getKeyboardSelectedRow() != selectedRow) {

          listGrid.clearRow(selectedRow);
          selectedRow = listGrid.getKeyboardSelectedRow();
          listGrid.paintRow(selectedRow);
        } else {
          close = true;
        }

        selectedItem = selectionModel.getLastSelectedObject();

        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));

        if (status == PlaceStatus.CLOSE) {
          placeManager.goTo("Task Details Multi");
          taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), "Form Display"));
        } else if (status == PlaceStatus.OPEN && !close) {
          taskSelected.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), "Form Display"));
        } else if (status == PlaceStatus.OPEN && close) {
          placeManager.closePlace("Task Details Multi");
        }

      }
    });

    noActionColumnManager = DefaultSelectionEventManager
                                        .createCustomManager(new DefaultSelectionEventManager.EventTranslator<TaskSummary>() {

      @Override
      public boolean clearCurrentSelection(CellPreviewEvent<TaskSummary> event) {
        return false;
      }

      @Override
      public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<TaskSummary> event) {
        NativeEvent nativeEvent = event.getNativeEvent();
        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
          // Ignore if the event didn't occur in the correct column.
          if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
            return DefaultSelectionEventManager.SelectAction.IGNORE;
          }
        }
        return DefaultSelectionEventManager.SelectAction.DEFAULT;
      }
    });
    listGrid.setSelectionModel(selectionModel, noActionColumnManager);

    listGrid.setRowStyles(new RowStyles<TaskSummary>() {

      @Override
      public String getStyleNames(TaskSummary row, int rowIndex) {
        if(row.getStatus().equals("InProgress")){
           if(row.getPriority() == 5){
             return "five";
           } else if(row.getPriority() == 4){
             return "four";
           } else if(row.getPriority() == 3){
             return "three";
           } else if(row.getPriority() == 2){
             return "two";
           } else if(row.getPriority() == 1){
             return "one";
           } else if(row.getPriority() == 0){
             return "one";
           } 
        }else if(row.getStatus().equals("Completed")){
          return "completed";
        }
        return null;
      }
    });
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
    
    Column descriptionColumn = initTaskDescriptionColumn();

    listGrid.addColumn(descriptionColumn, constants.Description());

    Column taskPriorityColumn = initTaskPriorityColumn();

    listGrid.addColumn(taskPriorityColumn, constants.Priority());

    Column statusColumn = initTaskStatusColumn();

    listGrid.addColumn(statusColumn, constants.Status());

    Column createdOnDateColumn = initTaskCreatedOnColumn();

    listGrid.addColumn(createdOnDateColumn, constants.Created_On());

    Column dueDateColumn = initTaskDueColumn();

    listGrid.addColumn(dueDateColumn, constants.Due_On());
    
    

    actionsColumn = initActionsColumn();
    listGrid.addColumn(actionsColumn, constants.Actions());
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
  
  private Column initTaskDescriptionColumn() {
    Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>(new TextCell()) {
      @Override
      public String getValue(TaskSummary object) {
        return object.getDescription();
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

  public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
    presenter.refreshGrid();
  }

  private Column initActionsColumn() {
    List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
    cells.add(new ClaimActionHasCell(constants.Claim(), new ActionCell.Delegate<TaskSummary>() {
      @Override
      public void execute(TaskSummary task) {

        presenter.claimTask(task.getTaskId(), identity.getName());
        taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
        listGrid.refresh();
      }
    }));

    cells.add(new ReleaseActionHasCell(constants.Release(), new ActionCell.Delegate<TaskSummary>() {
      @Override
      public void execute(TaskSummary task) {

        presenter.releaseTask(task.getTaskId(), identity.getName());
        taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getTaskName()));
        listGrid.refresh();
      }
    }));

    cells.add(new CompleteActionHasCell(constants.Complete(), new ActionCell.Delegate<TaskSummary>() {
      @Override
      public void execute(TaskSummary task) {
        placeManager.goTo("Task Details Multi");
        taskSelected.fire(new TaskSelectionEvent(task.getTaskId(), task.getName(), "Form Display"));
      }
    }));

    CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
    Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(cell) {
      @Override
      public TaskSummary getValue(TaskSummary object) {
        return object;
      }
    };
    return actionsColumn;

  }

  public void refreshNewTask(@Observes NewTaskEvent newTask) {
    presenter.refreshGrid();
    PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
    if (status == PlaceStatus.OPEN) {
      taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
    } else {
      placeManager.goTo("Task Details Multi");
      taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
    }

    selectionModel.setSelected(new TaskSummary(newTask.getNewTaskId(), newTask.getNewTaskName()), true);
  }

  protected class CompleteActionHasCell implements HasCell<TaskSummary, TaskSummary> {

    private ActionCell<TaskSummary> cell;

    public CompleteActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
      cell = new ActionCell<TaskSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
          if (value.getActualOwner() != null && value.getStatus().equals("InProgress")) {
            AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.completeGridIcon());
            SafeHtmlBuilder mysb = new SafeHtmlBuilder();
            mysb.appendHtmlConstant("<span title='" + constants.Complete() + "' style='margin-right:5px;'>");
            mysb.append(imageProto.getSafeHtml());
            mysb.appendHtmlConstant("</span>");
            sb.append(mysb.toSafeHtml());
          }
        }
      };
    }

    @Override
    public Cell<TaskSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public TaskSummary getValue(TaskSummary object) {
      return object;
    }
  }

  protected class ClaimActionHasCell implements HasCell<TaskSummary, TaskSummary> {

    private ActionCell<TaskSummary> cell;

    public ClaimActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
      cell = new ActionCell<TaskSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
          if (value.getStatus().equals("Ready")) {
            AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.releaseGridIcon());
            SafeHtmlBuilder mysb = new SafeHtmlBuilder();
            mysb.appendHtmlConstant("<span title='" + constants.Claim() + "' style='margin-right:5px;'>");
            mysb.append(imageProto.getSafeHtml());
            mysb.appendHtmlConstant("</span>");
            sb.append(mysb.toSafeHtml());
          }
        }
      };
    }

    @Override
    public Cell<TaskSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public TaskSummary getValue(TaskSummary object) {
      return object;
    }
  }

  protected class ReleaseActionHasCell implements HasCell<TaskSummary, TaskSummary> {

    private ActionCell<TaskSummary> cell;

    public ReleaseActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
      cell = new ActionCell<TaskSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
          if (value.getActualOwner() != null && value.getActualOwner().equals(identity.getName())
                  && (value.getStatus().equals("Reserved") || value.getStatus().equals("InProgress"))) {
            AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.claimGridIcon());
            SafeHtmlBuilder mysb = new SafeHtmlBuilder();
            mysb.appendHtmlConstant("<span title='" + constants.Release() + "' style='margin-right:5px;'>");
            mysb.append(imageProto.getSafeHtml());
            mysb.appendHtmlConstant("</span>");
            sb.append(mysb.toSafeHtml());
          }
        }
      };
    }

    @Override
    public Cell<TaskSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public TaskSummary getValue(TaskSummary object) {
      return object;
    }
  }

}
