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
package org.jbpm.console.ng.client.editors.tasks.inbox.personal.list;

import java.util.Comparator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskSelectionEvent;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.UserTaskEvent;
import org.jbpm.console.ng.client.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.client.util.ResizableHeader;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "InboxPersonalViewImpl.html")
public class InboxPersonalViewImpl extends Composite
    implements
    InboxPersonalPresenter.InboxView {

    @Inject
    private Identity identity;
   

    @Inject
    private PlaceManager                       placeManager;
    private InboxPersonalPresenter             presenter;
    
    @Inject
    @DataField
    public Button                              refreshTasksButton;
    @Inject
    @DataField
    public Button                              startTaskButton;
    @Inject
    @DataField
    public Button                              completeTaskButton;
    @Inject
    @DataField
    public Button                              releaseTaskButton;
    @Inject
    @DataField
    public TextBox                             userText;
    @Inject
    @DataField
    public DataGrid<TaskSummary>               myTaskListGrid;
    @Inject
    @DataField
    public SimplePager                         pager;
    @Inject
    @DataField
    public CheckBox                            showCompletedCheck;
    
    private Set<TaskSummary>                   selectedTasks;
    @Inject
    private Event<NotificationEvent>           notification;
    @Inject
    private Event<TaskSelectionEvent>          taskSelection;
    
    private ListHandler<TaskSummary>            sortHandler;

    private MultiSelectionModel<TaskSummary> selectionModel;
    
    @Override
    public void init(InboxPersonalPresenter presenter) {
        this.presenter = presenter;
        myTaskListGrid.setWidth( "100%" );
        myTaskListGrid.setHeight("200px");

        // Set the message to display when the table is empty.
        myTaskListGrid.setEmptyTableWidget( new Label( "Hooray you don't have any pending Task!!" ) );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<TaskSummary>( presenter.getDataProvider().getList() );
        
        myTaskListGrid.addColumnSortHandler( sortHandler );
        
        // Create a Pager to control the table.
        
        pager.setDisplay( myTaskListGrid );
        pager.setPageSize( 6 );

        // Add a selection model so we can select cells.
        selectionModel =
                new MultiSelectionModel<TaskSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedTasks = selectionModel.getSelectedSet();
                for ( TaskSummary ts : selectedTasks ) {
                    taskSelection.fire( new TaskSelectionEvent( ts.getId() ) );
                }
            }
        } );
        
        myTaskListGrid.setSelectionModel( selectionModel,
                                          DefaultSelectionEventManager
                                                  .<TaskSummary> createCheckboxManager() );

        initTableColumns( selectionModel );

        userText.setText(identity.getName());

        presenter.addDataDisplay( myTaskListGrid );

    }

    public void recieveStatusChanged(@Observes UserTaskEvent event) {
        Boolean isChecked = showCompletedCheck.getValue();

        presenter.refreshTasks( event.getUserId(),
                                isChecked );
        userText.setText( event.getUserId() );
    }

    @EventHandler("refreshTasksButton")
    public void refreshTasksButton(ClickEvent e) {
        Boolean isChecked = showCompletedCheck.getValue();
        presenter.refreshTasks( userText.getText(),
                                isChecked );
    }

    @EventHandler("startTaskButton")
    public void startTaskButton(ClickEvent e) {
        if ( selectedTasks.isEmpty() ) {
            displayNotification( "Please Select at least one Task to Execute a Quick Action" );
            return;
        }
        presenter.startTasks( selectedTasks,
                              userText.getText() );
    }

    @EventHandler("completeTaskButton")
    public void completeTaskButton(ClickEvent e) {
        if ( selectedTasks.isEmpty() ) {
            displayNotification( "Please Select at least one Task to Execute a Quick Action" );
            return;
        }
        presenter.completeTasks( selectedTasks,
                                 userText.getText() );

    }

    @EventHandler("releaseTaskButton")
    public void releaseTaskButton(ClickEvent e) {
        if ( selectedTasks.isEmpty() ) {
            displayNotification( "Please Select at least one Task to Execute a Quick Action" );
            return;
        }
        presenter.releaseTasks( selectedTasks,
                                userText.getText() );
    }

    private void initTableColumns(final SelectionModel<TaskSummary> selectionModel) {
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
        myTaskListGrid.addColumn( checkColumn,
                                  SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
       

        // Id 
        Column<TaskSummary, Number> taskIdColumn =
                new Column<TaskSummary, Number>( new NumberCell() ) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getId();
                    }
                };
        taskIdColumn.setSortable( true );
        
        myTaskListGrid.addColumn( taskIdColumn,
                                  new ResizableHeader("Id",myTaskListGrid, taskIdColumn ));
       sortHandler.setComparator( taskIdColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return Long.valueOf( o1.getId() ).compareTo( Long.valueOf( o2.getId() ) );
                                       }
                                   } );
   
        // Task name.
        Column<TaskSummary, String> taskNameColumn =
                new Column<TaskSummary, String>( new TextCell() ) {
                    @Override
                    public String getValue(TaskSummary object) {
                        return object.getName();
                    }
                };
        taskNameColumn.setSortable( true );
        
        myTaskListGrid.addColumn( taskNameColumn,
                                  new ResizableHeader("Task",myTaskListGrid, taskNameColumn ));
        sortHandler.setComparator( taskNameColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return o1.getName().compareTo( o2.getName() );
                                       }
                                   } );
       
        // Task priority.
        Column<TaskSummary, Number> taskPriorityColumn =
                new Column<TaskSummary, Number>( new NumberCell() ) {
                    @Override
                    public Number getValue(TaskSummary object) {
                        return object.getPriority();
                    }
                };
        taskPriorityColumn.setSortable( true );
        taskPriorityColumn.setSortable(true);
        myTaskListGrid.addColumn( taskPriorityColumn,
                                  new ResizableHeader("Priority", myTaskListGrid, taskPriorityColumn ));
      
        sortHandler.setComparator( taskPriorityColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return Integer.valueOf( o1.getPriority() ).compareTo( o2.getPriority() );
                                       }
                                   } );
        // Status.
        Column<TaskSummary, String> statusColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue(TaskSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        

        myTaskListGrid.addColumn( statusColumn,
                                  new ResizableHeader("Status",myTaskListGrid, statusColumn ));
        sortHandler.setComparator( statusColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return o1.getStatus().compareTo( o2.getStatus() );
                                       }
                                   } );
               
        // Due Date.
        Column<TaskSummary, String> dueDateColumn = new Column<TaskSummary, String>( new TextCell() ) {
            @Override
            public String getValue(TaskSummary object) {
                if(object.getExpirationTime() != null){
                    return object.getExpirationTime().toString();
                }
                return "";
            }
        };
        dueDateColumn.setSortable( true );

        myTaskListGrid.addColumn( dueDateColumn,
                                  new ResizableHeader("Due On",myTaskListGrid, dueDateColumn ));
       sortHandler.setComparator( dueDateColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           if(o1.getExpirationTime() == null || o2.getExpirationTime() == null){
                                               return 0;
                                           }
                                           return o1.getExpirationTime().compareTo( o2.getExpirationTime() );
                                       }
                                   } );

        // Task parent id.
        Column<TaskSummary, String> taskParentIdColumn =
                new Column<TaskSummary, String>( new TextCell() ) {
                    @Override
                    public String getValue(TaskSummary object) {
                        return (object.getParentId() > 0) ? String.valueOf( object.getParentId() ) : "No Parent";
                    }
                };
        taskParentIdColumn.setSortable( true );
        
        myTaskListGrid.addColumn( taskParentIdColumn,
                                  new ResizableHeader("Parent",myTaskListGrid, taskParentIdColumn ));
     sortHandler.setComparator( taskParentIdColumn,
                                   new Comparator<TaskSummary>() {
                                       public int compare(TaskSummary o1,
                                                          TaskSummary o2) {
                                           return Integer.valueOf( o1.getParentId() ).compareTo( o2.getParentId() );
                                       }
                                   } );

        Column<TaskSummary, String> editColumn =
                new Column<TaskSummary, String>( new ButtonCell() ) {
                    @Override
                    public String getValue(TaskSummary task) {
                        return "Edit";
                    }

                };

        editColumn.setFieldUpdater( new FieldUpdater<TaskSummary, String>() {
            @Override
            public void update(int index,
                               TaskSummary task,
                               String value) {
                
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Task Edit Perspective" );  
                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
                placeManager.goTo( placeRequestImpl);

            }
        } );

        myTaskListGrid.addColumn( editColumn,
                                  new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Edit" ) ) );
      

        Column<TaskSummary, String> workColumn =
                new Column<TaskSummary, String>( new ButtonCell() ) {
                    @Override
                    public String getValue(TaskSummary task) {
                        return "Work";
                    }
                };

        workColumn.setFieldUpdater( new FieldUpdater<TaskSummary, String>() {
            @Override
            public void update(int index,
                               TaskSummary task,
                               String value) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Perspective" ); 
                placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));
                
                placeManager.goTo( placeRequestImpl);

            }
        } );

        myTaskListGrid.addColumn( workColumn,
                                  new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( "Work" ) ) );
   

    }

    public void displayNotification(String text) {
        notification.fire( new NotificationEvent( text ) );
    }

    public TextBox getUserText() {
        return userText;
    }

    public void onTaskSelected(@Observes TaskChangedEvent taskChanged) {
        Boolean isChecked = showCompletedCheck.getValue();
        presenter.refreshTasks( taskChanged.getUserId(),
                                isChecked );

    }

    public CheckBox getShowCompletedCheck() {
        return showCompletedCheck;
    }

    public DataGrid<TaskSummary> getDataGrid() {
        return myTaskListGrid;
    }

    public ListHandler<TaskSummary> getSortHandler() {
        return sortHandler;
    }

    public MultiSelectionModel<TaskSummary> getSelectionModel() {
        return selectionModel;
    }

    
    
    

}
