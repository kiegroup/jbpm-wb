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
package org.jbpm.console.ng.client.editors.tasks.inbox.group.list;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskSelectionEvent;
import org.jbpm.console.ng.client.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value="InboxGroupViewImpl.html")
public class InboxGroupViewImpl extends Composite
    implements
    InboxGroupPresenter.InboxView {

    
    @Inject
    private PlaceManager                         placeManager;

    private InboxGroupPresenter                  presenter;
    @Inject
    @DataField
    public Button                                refreshTasksButton;
    
    @Inject
    @DataField
    public Button                                claimTaskButton;
    @Inject
    @DataField
    public TextBox                               userText;
    @Inject
    @DataField
    public TextBox                               groupText;
    @Inject
    @DataField
    public DataGrid<TaskSummary>                 myGroupTaskListGrid;
    @Inject
    @DataField
    public SimplePager                           pagerGroup;
    private Set<TaskSummary>                     selectedGroupTasks;
    @Inject
    private Event<NotificationEvent>             notification;
    @Inject
    private Event<TaskSelectionEvent>            taskSelection;
    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
                                                                  public Object getKey(TaskSummary item) {
                                                                      return item == null ? null : item.getId();
                                                                  }
                                                              };

    @EventHandler("refreshTasksButton")
    public void refreshTasksButton(ClickEvent e) {
        presenter.refreshGroupTasks( userText.getText(),
                                     Arrays.asList( groupText.getText().split( "," ) ) );
    }

    @EventHandler("claimTaskButton")
    public void claimTaskButton(ClickEvent e) {
        presenter.claimTasks( selectedGroupTasks,
                              userText.getText(),
                              Arrays.asList( groupText.getText().split( "," ) ) );

    }

    @Override
    public void init(InboxGroupPresenter presenter) {
        this.presenter = presenter;
       
        myGroupTaskListGrid.setWidth( "100%" );
        myGroupTaskListGrid.setHeight( "300px" );

        // Set the message to display when the table is empty.
        myGroupTaskListGrid.setEmptyTableWidget( new Label( "Hooray you don't have any Group Task to Claim!!" ) );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        ListHandler<TaskSummary> sortHandler =
                new ListHandler<TaskSummary>( presenter.getDataProvider().getList() );
        myGroupTaskListGrid.addColumnSortHandler( sortHandler );
        myGroupTaskListGrid.setPageSize( 6 );
        // Create a Pager to control the table.
        pagerGroup.setDisplay( myGroupTaskListGrid );
        pagerGroup.setPageSize( 10 );

        // Add a selection model so we can select cells.
        final MultiSelectionModel<TaskSummary> selectionModel =
                new MultiSelectionModel<TaskSummary>( KEY_PROVIDER );
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {

                selectedGroupTasks = selectionModel.getSelectedSet();
                for ( TaskSummary ts : selectedGroupTasks ) {
                    taskSelection.fire( new TaskSelectionEvent( ts.getId() ) );
                }
            }
        } );

        myGroupTaskListGrid.setSelectionModel( selectionModel,
                                               DefaultSelectionEventManager
                                                       .<TaskSummary> createCheckboxManager() );

        initTableColumns( selectionModel,
                          sortHandler );

        presenter.addDataDisplay( myGroupTaskListGrid );
    }

    private void initTableColumns(final SelectionModel<TaskSummary> selectionModel,
                                  ListHandler<TaskSummary> sortHandler) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<TaskSummary, Boolean> checkColumn =
                new Column<TaskSummary, Boolean>( new CheckboxCell( true,
                                                                    false ) ) {
                    @Override
                    public Boolean getValue(TaskSummary object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected( object );
                    }
                };
        myGroupTaskListGrid.addColumn( checkColumn,
                                       SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
        myGroupTaskListGrid.setColumnWidth( checkColumn,
                                            40,
                                            Unit.PCT );

        // First name.
        Column<TaskSummary, Number> taskIdColumn =
                new Column<TaskSummary, Number>( new NumberCell() ) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getId();
                    }
                };
        taskIdColumn.setSortable( true );
        sortHandler.setComparator( taskIdColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return Long.valueOf( o1.getId() ).compareTo( Long.valueOf( o2.getId() ) );
                                       }
                                   } );
        myGroupTaskListGrid.addColumn( taskIdColumn,
                                       "Task Id" );
        taskIdColumn.setFieldUpdater( new FieldUpdater<TaskSummary, Number>() {
            public void update(int index,
                               TaskSummary object,
                               Number value) {
                // Called when the user changes the value.
                presenter.refreshData();
            }
        } );
        myGroupTaskListGrid.setColumnWidth( taskIdColumn,
                                            40,
                                            Unit.PCT );

        // Task name.
        Column<TaskSummary, String> taskNameColumn =
                new Column<TaskSummary, String>( new EditTextCell() ) {
                    @Override
                    public String getValue(TaskSummary object) {
                        return object.getName();
                    }
                };
        taskNameColumn.setSortable( true );
        sortHandler.setComparator( taskNameColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return o1.getName().compareTo( o2.getName() );
                                       }
                                   } );
        myGroupTaskListGrid.addColumn( taskNameColumn,
                                       "Task Name" );
        taskNameColumn.setFieldUpdater( new FieldUpdater<TaskSummary, String>() {
            public void update(int index,
                               TaskSummary object,
                               String value) {
                // Called when the user changes the value.
                //                
                presenter.refreshData();
            }
        } );
        myGroupTaskListGrid.setColumnWidth( taskNameColumn,
                                            130,
                                            Unit.PCT );

        // Task priority.
        Column<TaskSummary, Number> taskPriorityColumn =
                new Column<TaskSummary, Number>( new NumberCell() ) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getPriority();
                    }
                };
        taskPriorityColumn.setSortable( true );
        sortHandler.setComparator( taskPriorityColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return Integer.valueOf( o1.getPriority() ).compareTo( o2.getPriority() );
                                       }
                                   } );
        myGroupTaskListGrid.addColumn( taskPriorityColumn,
                                       new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Priority" ) ) );
        myGroupTaskListGrid.setColumnWidth( taskPriorityColumn,
                                            40,
                                            Unit.PCT );

        // Status.
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        sortHandler.setComparator( statusColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return o1.getStatus().compareTo( o2.getStatus() );
                                       }
                                   } );

        myGroupTaskListGrid.addColumn( statusColumn,
                                       new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Status" ) ) );
        myGroupTaskListGrid.setColumnWidth( statusColumn,
                                            50,
                                            Unit.PCT );

        // User.
        Column<TaskSummary, String> userColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getActualOwner();
            }
        };
        userColumn.setSortable( true );
        sortHandler.setComparator( userColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return o1.getActualOwner().compareTo( o2.getActualOwner() );
                                       }
                                   } );

        myGroupTaskListGrid.addColumn( userColumn,
                                       new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Actual Owner" ) ) );
        myGroupTaskListGrid.setColumnWidth( userColumn,
                                            50,
                                            Unit.PCT );

        // Description.
        Column<TaskSummary, String> descriptionColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable( true );

        myGroupTaskListGrid.addColumn( descriptionColumn,
                                       new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Description" ) ) );
        myGroupTaskListGrid.setColumnWidth( descriptionColumn,
                                            150,
                                            Unit.PCT );

    }

    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
    }

    public TextBox getUserText() {
        return userText;
    }

    public TextBox getGroupText() {
        return groupText;
    }

    public void onTaskSelected(@Observes TaskChangedEvent taskChanged) {
        presenter.refreshGroupTasks( taskChanged.getUserId(),
                                     taskChanged.getGroupsId() );
    }

}
