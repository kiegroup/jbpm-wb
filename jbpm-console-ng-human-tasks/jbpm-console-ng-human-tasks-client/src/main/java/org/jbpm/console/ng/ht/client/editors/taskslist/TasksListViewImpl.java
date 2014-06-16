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
package org.jbpm.console.ng.ht.client.editors.taskslist;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.experimental.pagination.JBPMSimplePager;
import org.jbpm.console.ng.gc.client.experimental.pagination.JBPMSimplePager.TextLocation;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.jbpm.console.ng.gc.client.util.DataGridUtils.ActionsDataGrid;
import org.jbpm.console.ng.gc.client.util.LiCalendarPicker;
import org.jbpm.console.ng.ht.client.util.ResizableHeader;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskType;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskView;
import org.jbpm.console.ng.ga.model.CalendarListContainer;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends ActionsCellTaskList implements TasksListPresenter.TaskListView, RequiresResize,
        CalendarListContainer {
    
    public TasksListViewImpl() {
        pager = new JBPMSimplePager(TextLocation.CENTER, false, true);
        pager.sinkEvents(1);
        pager.addHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                if(currentTaskType.equals(TaskType.ALL)){
                    DataGridUtils.paintRowsCompleted(myTaskListGrid);
                }else if(DataGridUtils.currentIdSelected != null){
                    DataGridUtils.paintRowSelected(myTaskListGrid, String.valueOf(DataGridUtils.currentIdSelected));
                }
            }
        }, ClickEvent.getType());
    }

    private TasksListPresenter presenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    @DataField
    public NavLink gridViewTasksNavLink;

    @Inject
    @DataField
    public NavLink calendarViewTasksNavLink;

    @Inject
    @DataField
    public Button showAllTasksButton;

    @Inject
    @DataField
    public Button showPersonalTasksButton;

    @Inject
    @DataField
    public Button showGroupTasksButton;

    @Inject
    @DataField
    public Button showActiveTasksButton;

    @Inject
    @DataField
    private LiCalendarPicker liCalendarPicker;

    @Inject
    @DataField
    public LayoutPanel tasksViewContainer;

    @DataField
    public JBPMSimplePager pager;

    @Inject
    private TaskListMultiDayBox taskListMultiDayBox;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    private Date currentDate;

    private TaskView currentView = TaskView.DAY;

    private TaskType currentTaskType = TaskType.ACTIVE;

    private String currentFilter = "";

    private ListHandler<TaskSummary> sortHandler;

    public DataGrid<TaskSummary> myTaskListGrid;

    private ActionsDataGrid currentAction = null; 


    @Override
    public void init(final TasksListPresenter presenter) {
        this.presenter = presenter;
        taskListMultiDayBox.init();
        taskListMultiDayBox.setPresenter(presenter);
        currentDate = new Date();
        this.initializeCalendarPicker();
        this.initializeGridView();
        this.initializeButtonsView();
        this.initializeFilters();
    }

    private void initializeCalendarPicker() {
        liCalendarPicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                changeCurrentDate(event.getValue());
                refreshTasks();
            }
        });
    }

    private void initializeGridView() {
        tasksViewContainer.clear();
        calendarViewTasksNavLink.setStyleName("");
        gridViewTasksNavLink.setStyleName("active");
        currentView = TaskView.GRID;
        
        myTaskListGrid = new DataGrid<TaskSummary>();
        myTaskListGrid.setStyleName("table table-bordered table-striped table-hover");
        
        pager.setDisplay(myTaskListGrid);
        pager.setPageSize(DataGridUtils.pageSize);
        //pager.setStyleName(STYLE_PAGER);
        pager.setDataProvider(presenter);
        tasksViewContainer.add(myTaskListGrid);
        myTaskListGrid.setEmptyTableWidget(new HTMLPanel(constants.No_Tasks_Found()));
        // Attach a column sort handler to the ListDataProvider to sort the
        // list.
        sortHandler = new ColumnSortEvent.ListHandler<TaskSummary>(presenter.getDataProvider().getList());
        myTaskListGrid.getColumnSortList().setLimit(1);
        this.setSelectionModel();
        this.setGridEvents();
        this.initGridColumns();
        myTaskListGrid.addColumnSortHandler(sortHandler);
        presenter.addDataDisplay(myTaskListGrid);
    }

    private void initializeButtonsView() {
        gridViewTasksNavLink.setText(constants.Grid());
        gridViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                liCalendarPicker.clear();
                setGridView();
                refreshTasks();
            }

        });
        final CalendarListContainer container = this;
        calendarViewTasksNavLink.setText(constants.Calendar());
        calendarViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                liCalendarPicker.clear();
                liCalendarPicker.setListContainer(container);
                liCalendarPicker.init();
                setCalendarView(TaskView.DAY);
            }

        });
    }

    private void initializeFilters() {
        this.setFilters(showPersonalTasksButton, constants.Personal(), TaskType.PERSONAL);
        this.setFilters(showGroupTasksButton, constants.Group(), TaskType.GROUP);
        this.setFilters(showActiveTasksButton, constants.Active(), TaskType.ACTIVE);
        this.setFilters(showAllTasksButton, constants.All(), TaskType.ALL);
    }

    private void setFilters(Button button, String description, final TaskType taskType) {
        button.setSize(ButtonSize.SMALL);
        button.setText(description);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveButton(taskType);
                refreshTasks();
            }
        });
    }

    @Override
    public void changeCurrentDate(Date date) {
        currentDate = date;
    }

    @Override
    public void setActiveButton(TaskType taskType) {
        showGroupTasksButton.setStyleName("btn btn-small");
        showPersonalTasksButton.setStyleName("btn btn-small");
        showActiveTasksButton.setStyleName("btn btn-small");
        showAllTasksButton.setStyleName("btn btn-small");
        currentTaskType = taskType;

        switch (taskType) {
        case ALL:
            showAllTasksButton.setStyleName(showAllTasksButton.getStyleName() + " active");
            break;
        case ACTIVE:
            showActiveTasksButton.setStyleName(showActiveTasksButton.getStyleName() + " active");
            break;
        case GROUP:
            showGroupTasksButton.setStyleName(showGroupTasksButton.getStyleName() + " active");
            break;
        case PERSONAL:
            showPersonalTasksButton.setStyleName(showPersonalTasksButton.getStyleName() + " active");
            break;
        }

    }

    @Override
    public void setGridView() {
        DataGridUtils.PaintGridFromCalendar(myTaskListGrid);
        this.initializeGridView();
        pager.setVisible(true);
        if ((getParent().getOffsetHeight() - 120) > 0) {
            tasksViewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
        }
        
    }

    @Override
    public void setCalendarView(TaskView taskView) {
        DataGridUtils.paintCalendarFromGrid(myTaskListGrid);
        tasksViewContainer.clear();
        tasksViewContainer.add(taskListMultiDayBox);
        tasksViewContainer.setStyleName(taskView.name().toLowerCase());
        gridViewTasksNavLink.setStyleName("");
        calendarViewTasksNavLink.setStyleName("active");
        currentView = taskView;
        liCalendarPicker.setCalendarView(taskView);
        pager.setVisible(false);
        
        tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
        refreshTasks();
    }

    private void initGridColumns() {
        this.idColumn();
        this.nameColumn();
        this.priorityColumn();
        this.statusColumn();
        this.onDateColumn();
        this.dueDateColumn();
        this.actionsColumn();
        DataGridUtils.setHideOnAllColumns(myTaskListGrid);
    }

    private void setGridEvents() {
        myTaskListGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<TaskSummary>() {
            @Override
            public void onCellPreview(final CellPreviewEvent<TaskSummary> event) {
                if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onMouseOverGrid(event);
                }
                if (BrowserEvents.FOCUS.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onFocusGrid();
                }
            }
        });
    }

    @Override
    public void onResize() {
        if (currentView.equals(TaskView.GRID)) {
            if ((getParent().getOffsetHeight() - 120) > 0) {
                tasksViewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
                tasksViewContainer.setWidth(getParent().getOffsetWidth() + "px");
            }
        } else {
            tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
            tasksViewContainer.setWidth(getParent().getOffsetWidth() + "px");
        }
    }

    private void setSelectionModel() {
        final SingleSelectionModel<TaskSummary> selectionModel = new SingleSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if (currentAction == null) {
                    TaskSummary task = selectionModel.getSelectedObject();
                    if (task != null) {
                        placeManager.goTo("Task Details Multi");
                        taskSelected.fire(new TaskSelectionEvent(task.getId(), task.getName()));
                        DataGridUtils.currentIdSelected = task.getId();
                    }
                }
                currentAction = null;
            }
        });
        myTaskListGrid.setSelectionModel(selectionModel);
    }

    private void onFocusGrid() {
        if (DataGridUtils.idTaskCalendar != null) {
            DataGridUtils.currentIdSelected = DataGridUtils.idTaskCalendar;
            DataGridUtils.idTaskCalendar = null;
        }
        if (DataGridUtils.currentIdSelected != null) {
            changeRowSelected(new TaskStyleEvent(DataGridUtils.currentIdSelected));
        } else if (currentTaskType.equals(TaskType.ALL)) {
            DataGridUtils.paintRowsCompleted(myTaskListGrid);
        }
    }

    private void onMouseOverGrid(final CellPreviewEvent<TaskSummary> event) {
        TaskSummary task = event.getValue();
        if (task.getDescription() != null) {
            DataGridUtils.setTooltip(myTaskListGrid, event.getValue().getId(), event.getColumn(), task.getDescription());
        }
    }

    private void idColumn() {
        Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getId();
            }
        };
        taskIdColumn.setSortable(true);
        myTaskListGrid.setColumnWidth(taskIdColumn, "50px");
        myTaskListGrid.addColumn(taskIdColumn, new ResizableHeader(constants.Id(), myTaskListGrid, taskIdColumn));
        sortHandler.setComparator(taskIdColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
            }
        });
    }

    private void nameColumn() {
        Column<TaskSummary, String> taskNameColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getName();
            }
        };
        taskNameColumn.setSortable(true);
        myTaskListGrid.addColumn(taskNameColumn, new ResizableHeader(constants.Task(), myTaskListGrid, taskNameColumn));
        sortHandler.setComparator(taskNameColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
    }

    private void priorityColumn() {
        Column<TaskSummary, Number> taskPriorityColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getPriority();
            }
        };
        taskPriorityColumn.setSortable(true);
        myTaskListGrid.addColumn(taskPriorityColumn, new ResizableHeader(constants.Priority(), myTaskListGrid,
                taskPriorityColumn));
        myTaskListGrid.setColumnWidth(taskPriorityColumn, "100px");
        sortHandler.setComparator(taskPriorityColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                return Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
            }
        });
    }

    private void statusColumn() {
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        myTaskListGrid.addColumn(statusColumn, new ResizableHeader(constants.Status(), myTaskListGrid, statusColumn));
        sortHandler.setComparator(statusColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });
        myTaskListGrid.setColumnWidth(statusColumn, "100px");
    }

    private void onDateColumn() {
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

        myTaskListGrid.addColumn(createdOnDateColumn, new ResizableHeader(constants.Created_On(), myTaskListGrid,
                createdOnDateColumn));
        sortHandler.setComparator(createdOnDateColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                if (o1.getCreatedOn() == null || o2.getCreatedOn() == null) {
                    return 0;
                }
                return o1.getCreatedOn().compareTo(o2.getCreatedOn());
            }
        });
    }

    private void dueDateColumn() {
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
        myTaskListGrid.addColumn(dueDateColumn, new ResizableHeader(constants.Due_On(), myTaskListGrid, dueDateColumn));
        sortHandler.setComparator(dueDateColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                if (o1.getExpirationTime() == null || o2.getExpirationTime() == null) {
                    return 0;
                }
                return o1.getExpirationTime().compareTo(o2.getExpirationTime());
            }
        });
    }

    private void actionsColumn() {
        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
        cells.add(new ClaimActionHasCell(ActionsDataGrid.CLAIM.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.CLAIM;
                presenter.claimTasks(Lists.newArrayList(task.getId()), identity.getName());
            }
        }));

        cells.add(new ReleaseActionHasCell(ActionsDataGrid.RELEASE.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.RELEASE;
                presenter.releaseTasks(Lists.newArrayList(task.getId()), identity.getName());
            }
        }));

        cells.add(new StartActionHasCell(ActionsDataGrid.START.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.START;
                DataGridUtils.currentIdSelected = task.getId();
                presenter.startTasks(Lists.newArrayList(task.getId()), identity.getName());
            }
        }));

        cells.add(new CompleteActionHasCell(ActionsDataGrid.COMPLETE.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.COMPLETE;
                DataGridUtils.currentIdSelected = task.getId();
                placeManager.goTo("Task Details Multi");
                taskSelected.fire(new TaskSelectionEvent(task.getId(), task.getName(), "Form Display"));
            }
        }));

        cells.add(new DetailsHasCell(ActionsDataGrid.DETAILS.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.DETAILS;
                PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
                String idRowSelected = DataGridUtils.getIdRowSelected(myTaskListGrid);
                Long idSelected = 0L;
                if(idRowSelected != null){
                  idSelected = Long.valueOf(idRowSelected);
                }
                
                DataGridUtils.currentIdSelected = task.getId();
                if(status == PlaceStatus.CLOSE || !Long.valueOf(task.getId()).equals(idSelected)){
                    placeManager.goTo("Task Details Multi");
                    taskSelected.fire(new TaskSelectionEvent(task.getId(), task.getName()));
                }else if( status == PlaceStatus.OPEN || Long.valueOf(task.getId()).equals(idSelected)){
                    presenter.closeEditPanel();
                }
            }
        }));

        CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
        Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(cell) {
            @Override
            public TaskSummary getValue(TaskSummary object) {
                return object;
            }
        };
        myTaskListGrid.addColumn(actionsColumn, new ResizableHeader(constants.Actions(), myTaskListGrid, actionsColumn));
        myTaskListGrid.setColumnWidth(actionsColumn, "120px");
    }

    @Override
    public void setDayView() {
        setCalendarView(TaskView.DAY);
    }

    @Override
    public void setWeekView() {
        setCalendarView(TaskView.WEEK);
    }

    @Override
    public void setMonthView() {
        setCalendarView(TaskView.MONTH);
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void refreshTasks() {
        presenter.refreshTasks(currentDate, currentView, currentTaskType, pager.getCurrentPage() * DataGridUtils.pageSize, (DataGridUtils.pageSize * DataGridUtils.clientSidePages), true);
    }

    public DataGrid<TaskSummary> getTaskListGrid() {
        return myTaskListGrid;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public TaskView getCurrentView() {
        return currentView;
    }

    public TaskType getCurrentTaskType() {
        return currentTaskType;
    }

    @Override
    public String getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }

    @Override
    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }

    public void changeRowSelected(@Observes TaskStyleEvent taskStyleEvent) {
        if (taskStyleEvent.getTaskEventId() != null && this.getCurrentView() == TaskView.GRID) {
            DataGridUtils.paintRowSelected(myTaskListGrid, String.valueOf(taskStyleEvent.getTaskEventId()));
        }
        if (currentTaskType.equals(TaskType.ALL)) {
            DataGridUtils.paintRowsCompleted(myTaskListGrid);
        }
        currentAction = null;
    }

    public void refreshNewTask(@Observes NewTaskEvent newTask){
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
        DataGridUtils.currentIdSelected = newTask.getNewTaskId();
        if( status == PlaceStatus.OPEN ){
            placeManager.goTo("Task Details Multi");
            taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
        }
        if( this.getCurrentView() != TaskView.GRID){
            presenter.changeBgTaskCalendar(new TaskCalendarEvent(newTask.getNewTaskId()));
        }
        this.setSelectionModel();
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        currentAction = null;
        refreshTasks();
    }

    @Override
    public JBPMSimplePager getPager() {
        return pager;
    }

}
