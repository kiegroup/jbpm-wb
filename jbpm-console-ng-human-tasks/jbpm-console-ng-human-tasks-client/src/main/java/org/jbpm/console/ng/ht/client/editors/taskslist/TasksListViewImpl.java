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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.he.model.ActionHistoryEnum;
import org.jbpm.console.ng.he.model.PointHistory;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskType;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskView;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
import org.jbpm.console.ng.ht.client.util.CalendarPicker;
import org.jbpm.console.ng.ht.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;


@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite implements TasksListPresenter.TaskListView {
    private Constants constants = GWT.create(Constants.class);
    private HumanTasksImages images = GWT.create(HumanTasksImages.class);

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private TasksListPresenter presenter;

    @Inject
    @DataField
    public NavLink dayViewTasksNavLink;

    @Inject
    @DataField
    public NavLink gridViewTasksNavLink;

    @Inject
    @DataField
    public NavLink monthViewTasksNavLink;

    @Inject
    @DataField
    public NavLink weekViewTasksNavLink;
    
    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    @DataField
    public NavLink createQuickTaskNavLink;

    @Inject
    @DataField
    public NavLink showAllTasksNavLink;

    @Inject
    @DataField
    public NavLink showPersonalTasksNavLink;

    @Inject
    @DataField
    public NavLink showGroupTasksNavLink;

    @Inject
    @DataField
    public NavLink showActiveTasksNavLink;

    
    @DataField
    public Heading taskCalendarViewLabel = new Heading(4);

    @Inject
    @DataField
    private CalendarPicker calendarPicker;

    @Inject
    @DataField
    public FlowPanel tasksViewContainer;

    @Inject
    private TaskListMultiDayBox taskListMultiDayBox;

    @Inject
    private Event<NotificationEvent> notification;

    private Date currentDate;

    private TaskView currentView = TaskView.DAY;

    private TaskType currentTaskType = TaskType.ACTIVE;
    
    @Inject
    private Event<TaskSelectionEvent> taskSelection;
    
    @Inject
    private Event<PointHistory> pointHistory;

    @Inject
    @DataField
    public IconAnchor refreshIcon;
    
    private Set<TaskSummary> selectedTasks;
    private ListHandler<TaskSummary> sortHandler;
    private MultiSelectionModel<TaskSummary> selectionModel;
    
    
    public DataGrid<TaskSummary> myTaskListGrid;
   
    public SimplePager pager;
    
    
    

    @Override
    public void init( final TasksListPresenter presenter ) {
        this.presenter = presenter;
        
        refreshIcon.setTitle( constants.Refresh() );
        refreshIcon.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                refreshTasks();
                searchBox.setText("");
                displayNotification( constants.Tasks_Refreshed() );
            }
        } );
        
        taskListMultiDayBox.init();
        taskListMultiDayBox.setPresenter( presenter );
        calendarPicker.init();
        currentDate = new Date();
        calendarPicker.setViewType( "day" );
        calendarPicker.addValueChangeHandler( new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange( ValueChangeEvent<Date> event ) {
                currentDate = event.getValue();
                refreshTasks();
                
            }
        } );

        // By Default we will start in Grid View
        initializeGridView();
        
        dayViewTasksNavLink.setText( constants.Day() );
        dayViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.clear();
                tasksViewContainer.add(taskListMultiDayBox);
                tasksViewContainer.setStyleName("day");
                dayViewTasksNavLink.setStyleName("active");
                weekViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("");
                gridViewTasksNavLink.setStyleName("");
                currentView = TaskView.DAY;
                calendarPicker.setViewType( "day" );
                refreshTasks();
                
            }
        } );
        weekViewTasksNavLink.setText( constants.Week() );
        weekViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.clear();
                tasksViewContainer.add(taskListMultiDayBox);
                tasksViewContainer.setStyleName("week");
                dayViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("");
                gridViewTasksNavLink.setStyleName("");
                weekViewTasksNavLink.setStyleName("active");
                currentView = TaskView.WEEK;
                calendarPicker.setViewType( "week" );
                refreshTasks();
                
            }
        } );

        monthViewTasksNavLink.setText( constants.Month() );
        monthViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.clear();
                tasksViewContainer.add(taskListMultiDayBox);
                tasksViewContainer.setStyleName("month");
                dayViewTasksNavLink.setStyleName("");
                gridViewTasksNavLink.setStyleName("");
                weekViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("active");
                currentView = TaskView.MONTH;
                calendarPicker.setViewType( "month" );
                refreshTasks();
                
            }
        } );

        gridViewTasksNavLink.setText( constants.Grid() );
        gridViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                
                initializeGridView();
                refreshTasks();
                
           }

        });

        createQuickTaskNavLink.setText( constants.New_Task() );
        createQuickTaskNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                displayNotification("**ggggggggggggg*invoke fire ***");
                pointHistory.fire(new PointHistory(ActionHistoryEnum.NEW_TASK.getDescription()));
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Quick New Task" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        // Filters
        showPersonalTasksNavLink.setText( constants.Personal() );
        showPersonalTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showPersonalTasksNavLink.setStyleName( "active" );
                showGroupTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.PERSONAL;
                refreshTasks();
                
            }
        } );

        showGroupTasksNavLink.setText( constants.Group() );
        showGroupTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "active" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.GROUP;
                refreshTasks();
                
            }
        } );

        showActiveTasksNavLink.setText( constants.Active() );
        showActiveTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "active" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.ACTIVE;
                refreshTasks();
                
            }
        } );

        showAllTasksNavLink.setText( constants.All() );
        showAllTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "active" );
                currentTaskType = TaskType.ALL;
                refreshTasks();
                

            }
        } );

        taskCalendarViewLabel.setText( constants.Tasks_List() );
        taskCalendarViewLabel.setStyleName( "" );
        
        searchBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 || event.getNativeKeyCode() == 32){
                    displayNotification("Filter: |"+searchBox.getText()+"|");
                    filterTasks(searchBox.getText());
                }
                
            }
        });

        
        refreshTasks();
    }
    
    public void filterTasks(String text){
        presenter.filterTasks(text);
    }
    
    private void initializeGridView() {
        tasksViewContainer.clear();
        dayViewTasksNavLink.setStyleName("");
        weekViewTasksNavLink.setStyleName("");
        monthViewTasksNavLink.setStyleName("");
        gridViewTasksNavLink.setStyleName("active");
        currentView = TaskView.GRID;
        calendarPicker.setViewType( "grid" );
        myTaskListGrid = new DataGrid<TaskSummary>();
        myTaskListGrid.setStyleName("table table-bordered table-striped table-hover");
        pager = new SimplePager();
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(myTaskListGrid);
        pager.setPageSize(30);

        tasksViewContainer.add(myTaskListGrid);
        tasksViewContainer.add(pager);

        myTaskListGrid.setHeight("350px");
        // Set the message to display when the table is empty.
        myTaskListGrid.setEmptyTableWidget(new Label(constants.No_Pending_Tasks_Enjoy()));

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<TaskSummary>(presenter.getAllTaskSummaries());

        myTaskListGrid.addColumnSortHandler(sortHandler);

        // Add a selection model so we can select cells.
        selectionModel = new MultiSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedTasks = selectionModel.getSelectedSet();
                for (TaskSummary ts : selectedTasks) {
                    taskSelection.fire(new TaskSelectionEvent(ts.getId()));
                }
            }
        });

        myTaskListGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<TaskSummary> createCheckboxManager());

        initTableColumns(selectionModel);
        presenter.addDataDisplay(myTaskListGrid);

    }

    public void recieveStatusChanged( @Observes UserTaskEvent event ) {
        refreshTasks();

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void refreshTasks() {
        presenter.refreshTasks( currentDate, currentView, currentTaskType );
    }

    @Override
    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }

    @Override
    public MultiSelectionModel<TaskSummary> getSelectionModel() {
        return selectionModel;
    }

    public TextBox getSearchBox() {
        return searchBox;
    }
    
     private void initTableColumns(final SelectionModel<TaskSummary> selectionModel) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<TaskSummary, Boolean> checkColumn = new Column<TaskSummary, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(TaskSummary object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
            }
        };
        myTaskListGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        myTaskListGrid.setColumnWidth(checkColumn, "40px");

        // Id
        Column<TaskSummary, Number> taskIdColumn = new Column<TaskSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(TaskSummary object) {
                return object.getId();
            }
        };
        taskIdColumn.setSortable(true);
        myTaskListGrid.setColumnWidth(taskIdColumn, "40px");

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

        // Due Date.
        Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>(new TextCell()) {
            @Override
            public String getValue(TaskSummary object) {
                if (object.getExpirationTime() != null) {
                    return object.getExpirationTime().toString();
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
        cells.add(new StartActionHasCell("Start", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.startTasks(tasks, identity.getName());
            }
        }));

        cells.add(new CompleteActionHasCell("Complete", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.completeTasks(tasks, identity.getName());
            }
        }));

        cells.add(new ClaimActionHasCell("Claim", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.claimTasks(tasks, identity.getName());
            }
        }));

        cells.add(new ReleaseActionHasCell("Release", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                List<Long> tasks = new ArrayList<Long>(1);
                tasks.add(task.getId());
                presenter.releaseTasks(tasks, identity.getName());
            }
        }));

        cells.add(new DetailsHasCell("Edit", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Task Details Popup");
                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
                placeManager.goTo(placeRequestImpl);
            }
        }));

        cells.add(new PopupActionHasCell("Work Popup", new ActionCell.Delegate<TaskSummary>() {
            @Override
            public void execute(TaskSummary task) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));

                placeManager.goTo(placeRequestImpl);
            }
        }));

        CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
        Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(cell) {
            @Override
            public TaskSummary getValue(TaskSummary object) {
                return object;
            }
        };
        myTaskListGrid.addColumn(actionsColumn, constants.Actions());

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
                    mysb.appendHtmlConstant("<span title='" + constants.Details() + "'>");
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
                    if (value.getActualOwner() != null && (value.getStatus().equals("Reserved"))) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.startGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Start() + "'>");
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
                        mysb.appendHtmlConstant("<span title='" + constants.Complete() + "'>");
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

    private class PopupActionHasCell implements HasCell<TaskSummary, TaskSummary> {

        private ActionCell<TaskSummary> cell;

        public PopupActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
            cell = new ActionCell<TaskSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
                    if (value.getActualOwner() != null
                            && (value.getStatus().equals("Reserved") || value.getStatus().equals("InProgress"))) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.popupIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Work() + "'>");
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
                        mysb.appendHtmlConstant("<span title='" + constants.Claim() + "'>");
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
                            && ( value.getStatus().equals( "Reserved" ) || value.getStatus().equals( "InProgress" ) )) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.claimGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Release() + "'>");
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
