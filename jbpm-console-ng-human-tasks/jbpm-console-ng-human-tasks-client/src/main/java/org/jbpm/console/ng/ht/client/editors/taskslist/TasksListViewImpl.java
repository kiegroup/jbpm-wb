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
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Navbar;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskType;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskView;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
import org.jbpm.console.ng.ht.client.util.DataGridUtils;
import org.jbpm.console.ng.ht.client.util.DataGridUtils.ActionsDataGrid;
import org.jbpm.console.ng.ht.client.util.LiCalendarPicker;
import org.jbpm.console.ng.ht.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.CalendarListContainer;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite implements TasksListPresenter.TaskListView, RequiresResize, CalendarListContainer {

    private Constants constants = GWT.create(Constants.class);
    private HumanTasksImages images = GWT.create(HumanTasksImages.class);

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private TasksListPresenter presenter;


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

    @Inject
    private TaskListMultiDayBox taskListMultiDayBox;

    @Inject
    private Event<NotificationEvent> notification;
    
    private Date currentDate;

    private TaskView currentView = TaskView.DAY;

    private TaskType currentTaskType = TaskType.ACTIVE;

    private String currentFilter = "";

    @Inject
    private Event<TaskSelectionEvent> taskSelected;
    
    private Set<TaskSummary> selectedTasks;
    
    private ListHandler<TaskSummary> sortHandler;

    public DataGrid<TaskSummary> myTaskListGrid;

    private Column<TaskSummary, Number> taskIdColumn;
    
    private ActionsDataGrid currentAction = null; 
    
    @DataField
    public SimplePager pager;
  

    public TasksListViewImpl() {
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
    }

    @Override
    public void init(final TasksListPresenter presenter) {
        this.presenter = presenter;

        taskListMultiDayBox.init();
        taskListMultiDayBox.setPresenter(presenter);

        currentDate = new Date();
        
        liCalendarPicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                changeCurrentDate(event.getValue());
                refreshTasks();
            }
        });
        

        // By Default we will start in Grid View
        initializeGridView();

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
                setDayView();
                refreshTasks();
            }

        });

        // Filters
        showPersonalTasksButton.setSize(ButtonSize.SMALL);
        showPersonalTasksButton.setText(constants.Personal());
        showPersonalTasksButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setPersonalTasks();
                refreshTasks();
            }
        });
        showGroupTasksButton.setSize(ButtonSize.SMALL);
        showGroupTasksButton.setText(constants.Group());
        showGroupTasksButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setGroupTasks();
                refreshTasks();
            }
        });
        showActiveTasksButton.setSize(ButtonSize.SMALL);
        showActiveTasksButton.setText(constants.Active());
        showActiveTasksButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveTasks();
                refreshTasks();
            }
        });
        showAllTasksButton.setSize(ButtonSize.SMALL);
        showAllTasksButton.setText(constants.All());
        showAllTasksButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setAllTasks();
                refreshTasks();
            }
        });
        
    }
    
    
   

    public void filterTasks(String text) {
        presenter.filterTasks(text);
    }
    
    @Override
    public void changeCurrentDate(Date date) {
        currentDate = date;
    }

    @Override
    public void setAllTasks() {
        showGroupTasksButton.setStyleName("btn btn-small");
        showPersonalTasksButton.setStyleName("btn btn-small");
        showActiveTasksButton.setStyleName("btn btn-small");
        showAllTasksButton.setStyleName("btn btn-small active");
        currentTaskType = TaskType.ALL;
    }

    @Override
    public void setActiveTasks() {
        showGroupTasksButton.setStyleName("btn btn-small");
        showPersonalTasksButton.setStyleName("btn btn-small");
        showActiveTasksButton.setStyleName("btn btn-small active");
        showAllTasksButton.setStyleName("btn btn-small");
        currentTaskType = TaskType.ACTIVE;
    }

    @Override
    public void setGroupTasks() {
        showGroupTasksButton.setStyleName("btn btn-small active");
        showPersonalTasksButton.setStyleName("btn btn-small");
        showActiveTasksButton.setStyleName("btn btn-small");
        showAllTasksButton.setStyleName("btn btn-small");
        currentTaskType = TaskType.GROUP;
    }

    @Override
    public void setPersonalTasks() {
        showPersonalTasksButton.setStyleName("btn btn-small active");
        showGroupTasksButton.setStyleName("btn btn-small");
        showActiveTasksButton.setStyleName("btn btn-small");
        showAllTasksButton.setStyleName("btn btn-small");
        currentTaskType = TaskType.PERSONAL;
    }

    @Override
    public void setGridView() {
        PaintGridFromCalendar(); 
        initializeGridView();
        pager.setVisible(true);
        if ((getParent().getOffsetHeight() - 120) > 0) {
            tasksViewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
        }
    }

    @Override
    public void setDayView() {
        paintCalendarFromGrid();
        tasksViewContainer.clear();
        tasksViewContainer.add(taskListMultiDayBox);
        tasksViewContainer.setStyleName("day");
        gridViewTasksNavLink.setStyleName("");
        calendarViewTasksNavLink.setStyleName("active");
        currentView = TaskView.DAY;
        liCalendarPicker.setDayView();
        pager.setVisible(false);
        tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
    }

    @Override
    public void setWeekView() {
        paintCalendarFromGrid();
        tasksViewContainer.clear();
        tasksViewContainer.add(taskListMultiDayBox);
        tasksViewContainer.setStyleName("week");
        gridViewTasksNavLink.setStyleName("");
        calendarViewTasksNavLink.setStyleName("active");
        currentView = TaskView.WEEK;
        liCalendarPicker.setWeekView();
        pager.setVisible(false);
        tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
    }

    @Override
    public void setMonthView() {
        paintCalendarFromGrid();
        tasksViewContainer.clear();
        tasksViewContainer.add(taskListMultiDayBox);
        tasksViewContainer.setStyleName("month");
        gridViewTasksNavLink.setStyleName("");
        calendarViewTasksNavLink.setStyleName("active");
        currentView = TaskView.MONTH;
        liCalendarPicker.setMonthView();
        pager.setVisible(false);
        tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
    }
    
    private void paintCalendarFromGrid(){
        if(DataGridUtils.idTaskCalendar == null ){
            DataGridUtils.idTaskCalendar = DataGridUtils.getIdRowSelected(myTaskListGrid);
        }
    } 

    private void initializeGridView() {
        tasksViewContainer.clear();
        calendarViewTasksNavLink.setStyleName("");
        gridViewTasksNavLink.setStyleName("active");
        currentView = TaskView.GRID;
        myTaskListGrid = new DataGrid<TaskSummary>();
        myTaskListGrid.setStyleName("table table-bordered table-striped table-hover");

        pager.setDisplay(myTaskListGrid);
        pager.setPageSize(10);

        tasksViewContainer.add(myTaskListGrid);

        // Set the message to display when the table is empty.
        myTaskListGrid.setEmptyTableWidget(new HTMLPanel(constants.No_Tasks_Found()));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<TaskSummary>(presenter.getDataProvider().getList());
        
        myTaskListGrid.getColumnSortList().setLimit(1);
        
        // Add a selection model so we can select cells.
        this.setSelectionModel(); 

        initTableColumns();

        myTaskListGrid.addColumnSortHandler(sortHandler);
        presenter.addDataDisplay(myTaskListGrid);

    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        refreshTasks();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void refreshTasks() {
        presenter.refreshTasks(currentDate, currentView, currentTaskType);

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

    private void initTableColumns() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

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

        // Id
        taskIdColumn = new Column<TaskSummary, Number>(new NumberCell()) {
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
        
        
        // Task name.
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
                return o1.getName().compareTo(o2.getName());
            }
        });

        // Task priority.
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
        // Status.
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

        // Created on Date.
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

        myTaskListGrid.addColumn(createdOnDateColumn, new ResizableHeader(constants.Created_On(), myTaskListGrid, createdOnDateColumn));
        sortHandler.setComparator(createdOnDateColumn, new Comparator<TaskSummary>() {
            @Override
            public int compare(TaskSummary o1, TaskSummary o2) {
                if (o1.getCreatedOn() == null || o2.getCreatedOn() == null) {
                    return 0;
                }
                return o1.getCreatedOn().compareTo(o2.getCreatedOn());
            }
        });

        // Due Date.
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

        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();
        cells.add(new ClaimActionHasCell(ActionsDataGrid.CLAIM.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.CLAIM; 
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.claimTasks(tasks, identity.getName());
            }
        }));

        cells.add(new ReleaseActionHasCell(ActionsDataGrid.RELEASE.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.RELEASE; 
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.releaseTasks(tasks, identity.getName());
            }
        }));

        cells.add(new StartActionHasCell(ActionsDataGrid.START.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.START;
                DataGridUtils.currentIdSelected = task.getId(); 
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.startTasks(tasks, identity.getName());
            }
        }));

        cells.add(new CompleteActionHasCell(ActionsDataGrid.COMPLETE.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.COMPLETE;
                DataGridUtils.currentIdSelected = task.getId(); 
                placeManager.goTo("Task Details Multi");
                taskSelected.fire(new TaskSelectionEvent(task.getId(), task.getName(), "Form Display"));
                changeRowSelected(new TaskStyleEvent(task.getId()));
            }
        }));
        
        cells.add(new DetailsHasCell(ActionsDataGrid.DETAILS.getDescription(), new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                currentAction = ActionsDataGrid.DETAILS;
                PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
                Long idSelected = DataGridUtils.getIdRowSelected(myTaskListGrid);
                DataGridUtils.currentIdSelected = task.getId();
                if(status == PlaceStatus.CLOSE || !Long.valueOf(task.getId()).equals(idSelected)){
                    placeManager.goTo("Task Details Multi");
                    taskSelected.fire(new TaskSelectionEvent(task.getId(), task.getName()));
                }else if( status == PlaceStatus.OPEN || Long.valueOf(task.getId()).equals(idSelected)){
                    placeManager.closePlace(new DefaultPlaceRequest("Task Details Multi"));
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
    public void onResize() {
        if (currentView.equals(TaskView.GRID)) {
            if ((getParent().getOffsetHeight() - 120) > 0) {
                tasksViewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
                tasksViewContainer.setWidth(getParent().getOffsetWidth()+ "px");
            }
        } else {
            tasksViewContainer.setHeight(getParent().getOffsetHeight() + "px");
            tasksViewContainer.setWidth(getParent().getOffsetWidth()+ "px");
        }
    }

    private class DetailsHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public DetailsHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {

                    ImageResource detailsIcon = images.detailsIcon();
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(detailsIcon);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Details() + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
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

    private class StartActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public StartActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null
                            && value.getActualOwner().equals(identity.getName())
                            && (value.getStatus().equals("Reserved"))) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.startGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Start() + "' style='margin-right:5px;'>");
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

    private class CompleteActionHasCell implements HasCell<TaskSummary, TaskSummary> {

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

    private class ClaimActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public ClaimActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getPotentialOwners() != null && !value.getPotentialOwners().isEmpty()
                            && value.getStatus().equals("Ready")) {
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

    private class ReleaseActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public ReleaseActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getPotentialOwners() != null && !value.getPotentialOwners().isEmpty()
                            && value.getActualOwner().equals(identity.getName())
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
    
    public void changeRowSelected(@Observes TaskStyleEvent taskStyleEvent){
        if( taskStyleEvent.getTaskEventId() != null && this.getCurrentView() == TaskView.GRID){
            DataGridUtils.paintRowSelected(myTaskListGrid, taskStyleEvent.getTaskEventId());
        } 
        if(currentTaskType.equals(TaskType.ALL)){
            DataGridUtils.paintRowsCompleted(myTaskListGrid);
        }
    }
    
    public void refreshNewTask(@Observes NewTaskEvent newTask){
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
        DataGridUtils.currentIdSelected = newTask.getNewTaskId();
        if( status == PlaceStatus.OPEN ){
            placeManager.goTo("Task Details Multi");
            taskSelected.fire(new TaskSelectionEvent(newTask.getNewTaskId(), newTask.getNewTaskName()));
        }
        if( this.getCurrentView() == TaskView.GRID){
            myTaskListGrid.setFocus(true);
        }else{
            presenter.changeBgTaskCalendar(new TaskCalendarEvent(newTask.getNewTaskId()));
        } 
    }
    
    private void setSelectionModel(){
        final SingleSelectionModel<TaskSummary> selectionModel = new SingleSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if(currentAction == null){
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
    
    private void onFocusGrid(){
        if(DataGridUtils.idTaskCalendar != null){
         DataGridUtils.currentIdSelected = DataGridUtils.idTaskCalendar;
            DataGridUtils.idTaskCalendar = null;
        }
        if(DataGridUtils.currentIdSelected != null){
            changeRowSelected(new TaskStyleEvent(DataGridUtils.currentIdSelected));
        }else if(currentTaskType.equals(TaskType.ALL)){
            DataGridUtils.paintRowsCompleted(myTaskListGrid);
        }
    }
    
    private void onMouseOverGrid(final CellPreviewEvent<TaskSummary> event){
        TaskSummary task = event.getValue();
        if(task.getDescription() != null){
            myTaskListGrid.getRowElement(event.getIndex()).getCells().getItem(event.getColumn()).setTitle(task.getDescription());
        }
    }
    
    private void PaintGridFromCalendar(){
        if(DataGridUtils.idTaskCalendar != null){
            DataGridUtils.currentIdSelected = DataGridUtils.idTaskCalendar; 
        }
    } 
    
}
