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

package org.jbpm.console.ng.es.client.editors.requestlist;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.model.events.RequestSelectionEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "RequestListViewImpl.html")
public class RequestListViewImpl extends Composite implements RequestListPresenter.RequestListView, RequiresResize {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    private RequestListPresenter presenter;

    

   

    @Inject
    @DataField
    public LayoutPanel listContainer;

   
    @Inject
    @DataField
    public DataGrid<RequestSummary> myRequestListGrid;

    @DataField
    public SimplePager pager;

    @Inject
    @DataField
    public NavLink showAllLink;
    
    @Inject
    @DataField
    public NavLink showQueuedLink;

    @Inject
    @DataField
    public NavLink showRunningLink;

    @Inject
    @DataField
    public NavLink showRetryingLink;

    @Inject
    @DataField
    public NavLink showErrorLink;

    @Inject
    @DataField
    public NavLink showCompletedLink;

    @Inject
    @DataField
    public NavLink showCancelledLink;
    
    @Inject
    @DataField
    public NavLink fiterLabel;

    private Set<RequestSummary> selectedRequests;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<RequestSelectionEvent> requestSelection;

    private ListHandler<RequestSummary> sortHandler;

    public RequestListViewImpl() {
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
    }
    
    @Override
    public void onResize() {
        if( (getParent().getOffsetHeight()-120) > 0 ){
            listContainer.setHeight(getParent().getOffsetHeight()-120+"px");
        }
    }
    

    @Override
    public void init(final RequestListPresenter presenter ) {
        this.presenter = presenter;

        listContainer.add( myRequestListGrid );
        
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay( myRequestListGrid );
        pager.setPageSize(10);
       
        fiterLabel.setText( constants.Showing() );
        
        showAllLink.setText( constants.All() );
        showAllLink.setStyleName( "active" );
        showAllLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showAllLink.setStyleName( "active" );
                showQueuedLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                presenter.refreshRequests( null );
            }
        } );
        
        
        showQueuedLink.setText( constants.Queued() );
        showQueuedLink.setStyleName( "" );
        showQueuedLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showQueuedLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "QUEUED" );
                presenter.refreshRequests( statuses );
            }
        } );
        
        showRunningLink.setText( constants.Running() );
        showRunningLink.setStyleName( "" );
        showRunningLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showRunningLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showQueuedLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "RUNNING" );
                presenter.refreshRequests( statuses );
            }
        } );
        
        
        showRetryingLink.setText( constants.Retrying() );
        showRetryingLink.setStyleName( "" );
        showRetryingLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showRetryingLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showQueuedLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "RETRYING" );
                presenter.refreshRequests( statuses );
            }
        } );
        
        showErrorLink.setText( constants.Error() );
        showErrorLink.setStyleName( "" );
        showErrorLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showErrorLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showQueuedLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "ERROR" );
                presenter.refreshRequests( statuses );
            }
        } );
        
        showCompletedLink.setText( constants.Completed() );
        showCompletedLink.setStyleName( "" );
        showCompletedLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showCompletedLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showQueuedLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCancelledLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "DONE" );
                presenter.refreshRequests( statuses );
            }
        } );
        
        showCancelledLink.setText( constants.Cancelled() );
        showCancelledLink.setStyleName( "" );
        showCancelledLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showCancelledLink.setStyleName( "active" );
                showAllLink.setStyleName( "" );
                showQueuedLink.setStyleName( "" );
                showRunningLink.setStyleName( "" );
                showRetryingLink.setStyleName( "" );
                showErrorLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                
                List<String> statuses = new ArrayList<String>();
                statuses.add( "CANCELLED" );
                presenter.refreshRequests( statuses );
            }
        } );
        

        // Set the message to display when the table is empty.
        myRequestListGrid.setEmptyTableWidget( new Label( constants.No_Pending_Jobs() ) );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<RequestSummary>( presenter.getDataProvider().getList() );
        myRequestListGrid.addColumnSortHandler( sortHandler );

       
        // Add a selection model so we can select cells.
        final MultiSelectionModel<RequestSummary> selectionModel = new MultiSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                selectedRequests = selectionModel.getSelectedSet();
                for ( RequestSummary r : selectedRequests ) {
                    requestSelection.fire( new RequestSelectionEvent( r.getId() ) );
                }
            }
        } );

        myRequestListGrid.setSelectionModel( selectionModel,
                                             DefaultSelectionEventManager.<RequestSummary>createCheckboxManager() );

        initTableColumns( selectionModel );

        presenter.addDataDisplay( myRequestListGrid );

    }

    public void requestCreated( @Observes RequestChangedEvent event ) {
        presenter.refreshRequests(null);
    }

  


   
    

    private void initTableColumns( final SelectionModel<RequestSummary> selectionModel ) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<RequestSummary, Boolean> checkColumn = new Column<RequestSummary, Boolean>( new CheckboxCell( true, false ) ) {
            @Override
            public Boolean getValue( RequestSummary object ) {
                // Get the value from the selection model.
                return selectionModel.isSelected( object );
            }
        };
        myRequestListGrid.addColumn( checkColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
        myRequestListGrid.setColumnWidth( checkColumn, "40px" );

        // Id
        Column<RequestSummary, Number> taskIdColumn = new Column<RequestSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( RequestSummary object ) {
                return object.getId();
            }
        };
        taskIdColumn.setSortable( true );
        sortHandler.setComparator( taskIdColumn, new Comparator<RequestSummary>() {
            @Override
            public int compare( RequestSummary o1,
                                RequestSummary o2 ) {
                return Long.valueOf( o1.getId() ).compareTo( Long.valueOf( o2.getId() ) );
            }
        } );

        myRequestListGrid.addColumn( taskIdColumn, new ResizableHeader( constants.Id(), myRequestListGrid, taskIdColumn ) );
        myRequestListGrid.setColumnWidth( taskIdColumn, "40px" );

        // Task name.
        Column<RequestSummary, String> taskNameColumn = new Column<RequestSummary, String>( new EditTextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getCommandName();
            }
        };
        taskNameColumn.setSortable( true );
        sortHandler.setComparator( taskNameColumn, new Comparator<RequestSummary>() {
            @Override
            public int compare( RequestSummary o1,
                                RequestSummary o2 ) {
                return o1.getCommandName().compareTo( o2.getCommandName() );
            }
        } );
        myRequestListGrid.addColumn( taskNameColumn, new ResizableHeader( constants.JobName(), myRequestListGrid, taskNameColumn ) );

        // Status
        Column<RequestSummary, String> statusColumn = new Column<RequestSummary, String>( new EditTextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        sortHandler.setComparator( statusColumn, new Comparator<RequestSummary>() {
            @Override
            public int compare( RequestSummary o1,
                                RequestSummary o2 ) {
                return o1.getStatus().compareTo( o2.getStatus() );
            }
        } );
        myRequestListGrid.addColumn( statusColumn, new ResizableHeader( constants.Status(), myRequestListGrid, taskNameColumn ) );
        myRequestListGrid.setColumnWidth( statusColumn, "100px" );

        // Due Date.
        Column<RequestSummary, String> dueDateColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                if ( object.getTime() != null ) {
                    return object.getTime().toString();
                }
                return "";
            }
        };
        dueDateColumn.setSortable( true );

        myRequestListGrid.addColumn( dueDateColumn, new ResizableHeader( constants.Due_On(), myRequestListGrid, dueDateColumn ) );

        // actions (icons)
        List<HasCell<RequestSummary, ?>> cells = new LinkedList<HasCell<RequestSummary, ?>>();

        cells.add( new ActionHasCell( "Details", new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                DefaultPlaceRequest request = new DefaultPlaceRequest( "Job Request Details" );
                request.addParameter( "requestId", String.valueOf( job.getId() ) );
                placeManager.goTo( request );
            }
        } ) );
        cells.add( new ActionHasCell( "Cancel", new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to cancel this Job?" ) ) {
                    presenter.cancelRequest( job.getId() );
                }
            }
        } ) );

        CompositeCell<RequestSummary> cell = new CompositeCell<RequestSummary>( cells );
        Column<RequestSummary, RequestSummary> actionsColumn = new Column<RequestSummary, RequestSummary>( cell ) {
            @Override
            public RequestSummary getValue( RequestSummary object ) {
                return object;
            }
        };

        myRequestListGrid.addColumn( actionsColumn, new SafeHtmlHeader( SafeHtmlUtils.fromSafeConstant( constants.Actions() ) ) );
        myRequestListGrid.setColumnWidth( actionsColumn, "100px" );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public NavLink getShowAllLink() {
        return showAllLink;
    }

    public NavLink getShowQueuedLink() {
        return showQueuedLink;
    }

    public NavLink getShowRunningLink() {
        return showRunningLink;
    }

    public NavLink getShowRetryingLink() {
        return showRetryingLink;
    }

    public NavLink getShowErrorLink() {
        return showErrorLink;
    }

    public NavLink getShowCompletedLink() {
        return showCompletedLink;
    }

    public NavLink getShowCancelledLink() {
        return showCancelledLink;
    }

  

    @Override
    public DataGrid<RequestSummary> getDataGrid() {
        return myRequestListGrid;
    }

    @Override
    public ListHandler<RequestSummary> getSortHandler() {
        return sortHandler;
    }

    private class ActionHasCell implements HasCell<RequestSummary, RequestSummary> {

        private ActionCell<RequestSummary> cell;

        public ActionHasCell( String text,
                              Delegate<RequestSummary> delegate ) {
            cell = new ActionCell<RequestSummary>( text, delegate );
        }

        @Override
        public Cell<RequestSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<RequestSummary, RequestSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public RequestSummary getValue( RequestSummary object ) {
            return object;
        }
    }

}
