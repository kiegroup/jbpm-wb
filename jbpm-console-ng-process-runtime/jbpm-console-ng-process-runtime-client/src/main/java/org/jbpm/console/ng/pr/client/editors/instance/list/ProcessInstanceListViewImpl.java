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

package org.jbpm.console.ng.pr.client.editors.instance.list;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessSelectionEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessInstanceListViewImpl.html")
public class ProcessInstanceListViewImpl extends Composite implements ProcessInstanceListPresenter.ProcessInstanceListView {

    private Constants constants = GWT.create( Constants.class );
    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private ProcessInstanceListPresenter presenter;


    private String currentFilter = "";
    
    @Inject
    @DataField
    public FlowPanel listContainer;


    @Inject
    @DataField
    public NavLink showAllLink;

    @Inject
    @DataField
    public NavLink showCompletedLink;

    @Inject
    @DataField
    public NavLink showAbortedLink;

    @Inject
    @DataField
    public NavLink showRelatedToMeLink;

    @Inject
    @DataField
    public NavLink fiterLabel;

    @Inject
    @DataField
    public DataGrid<ProcessInstanceSummary> processInstanceListGrid;

    public SimplePager pager;

    private Set<ProcessInstanceSummary> selectedProcessInstances;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ProcessSelectionEvent> processSelection;

    private ListHandler<ProcessInstanceSummary> sortHandler;

    public ProcessInstanceListViewImpl() {
        
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }
    
    @Override
    public void init( final ProcessInstanceListPresenter presenter ) {
        this.presenter = presenter;
        
        listContainer.add( processInstanceListGrid );
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(processInstanceListGrid);
        pager.setPageSize(10);
        listContainer.add( pager );

        processInstanceListGrid.setHeight( "350px" );
        fiterLabel.setText( constants.Showing() );
        showAllLink.setText( constants.Active() );
        showAllLink.setStyleName( "active" );
        showAllLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showAllLink.setStyleName( "active" );
                showCompletedLink.setStyleName( "" );
                showAbortedLink.setStyleName( "" );
                showRelatedToMeLink.setStyleName( "" );
                presenter.refreshActiveProcessList();
            }
        } );

        showCompletedLink.setText( constants.Completed() );
        showCompletedLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showAllLink.setStyleName( "" );
                showCompletedLink.setStyleName( "active" );
                showAbortedLink.setStyleName( "" );
                showRelatedToMeLink.setStyleName( "" );
                presenter.refreshCompletedProcessList();
            }
        } );
        showAbortedLink.setText( constants.Aborted() );
        showAbortedLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showAllLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showAbortedLink.setStyleName( "active" );
                showRelatedToMeLink.setStyleName( "" );
                presenter.refreshAbortedProcessList();
            }
        } );
        showRelatedToMeLink.setText( constants.Related_To_Me() );
        showRelatedToMeLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showAllLink.setStyleName( "" );
                showCompletedLink.setStyleName( "" );
                showAbortedLink.setStyleName( "" );
                showRelatedToMeLink.setStyleName( "active" );
                presenter.refreshRelatedToMeProcessList();
            }
        } );
 
        // Set the message to display when the table is empty.
        Label emptyTable = new Label( constants.No_Process_Instances_Found() );
        emptyTable.setStyleName( "" );
        processInstanceListGrid.setEmptyTableWidget( emptyTable );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<ProcessInstanceSummary>( presenter.getDataProvider().getList() );
        processInstanceListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( processInstanceListGrid );
        pager.setPageSize( 10 );

        // Add a selection model so we can select cells.
        final MultiSelectionModel<ProcessInstanceSummary> selectionModel = new MultiSelectionModel<ProcessInstanceSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                selectedProcessInstances = selectionModel.getSelectedSet();
                for ( ProcessInstanceSummary ts : selectedProcessInstances ) {
                    processSelection.fire( new ProcessSelectionEvent( ts.getId() ) );
                }
            }
        } );

        processInstanceListGrid.setSelectionModel( selectionModel,
                                                   DefaultSelectionEventManager.<ProcessInstanceSummary>createCheckboxManager() );

        initTableColumns( selectionModel );

        presenter.addDataDisplay( processInstanceListGrid );

    }

    public DataGrid<ProcessInstanceSummary> getProcessInstanceListGrid() {
        return processInstanceListGrid;
    }

    public Set<ProcessInstanceSummary> getSelectedProcessInstances() {
        return selectedProcessInstances;
    }

    

    private void initTableColumns( final SelectionModel<ProcessInstanceSummary> selectionModel ) {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        Column<ProcessInstanceSummary, Boolean> checkColumn = new Column<ProcessInstanceSummary, Boolean>( new CheckboxCell(
                true, false ) ) {
            @Override
            public Boolean getValue( ProcessInstanceSummary object ) {
                // Get the value from the selection model.
                return selectionModel.isSelected( object );
            }
        };
        processInstanceListGrid.addColumn( checkColumn, new ResizableHeader("", processInstanceListGrid,
                                                                                   checkColumn ) );
        processInstanceListGrid.setColumnWidth( checkColumn, "40px" );

        // Process Name.
        Column<ProcessInstanceSummary, String> processNameColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessName();
            }
        };
        processNameColumn.setSortable( true );
        sortHandler.setComparator( processNameColumn, new Comparator<ProcessInstanceSummary>() {
            @Override
            public int compare( ProcessInstanceSummary o1,
                                ProcessInstanceSummary o2 ) {
                return o1.getProcessId().compareTo( o2.getProcessId() );
            }
        } );
        processInstanceListGrid.addColumn( processNameColumn, new ResizableHeader( constants.Name(), processInstanceListGrid,
                                                                                   processNameColumn ) );

        Column<ProcessInstanceSummary, String> processInitiatorColumn = new Column<ProcessInstanceSummary, String>(
                new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getInitiator();
            }
        };
        processInitiatorColumn.setSortable( true );
        sortHandler.setComparator( processInitiatorColumn, new Comparator<ProcessInstanceSummary>() {
            @Override
            public int compare( ProcessInstanceSummary o1,
                                ProcessInstanceSummary o2 ) {
                return o1.getInitiator().compareTo( o2.getInitiator() );
            }
        } );
        processInstanceListGrid.addColumn( processInitiatorColumn, new ResizableHeader( constants.Initiator(),
                                                                                        processInstanceListGrid, processInitiatorColumn ) );
        processInstanceListGrid.setColumnWidth( processInitiatorColumn, "180px" );
        // Process Version.
        Column<ProcessInstanceSummary, String> processVersionColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessVersion();
            }
        };
        processVersionColumn.setSortable( true );
        sortHandler.setComparator( processVersionColumn, new Comparator<ProcessInstanceSummary>() {
            @Override
            public int compare( ProcessInstanceSummary o1,
                                ProcessInstanceSummary o2 ) {
                return o1.getProcessVersion().compareTo( o2.getProcessVersion() );
            }
        } );
        processInstanceListGrid.addColumn( processVersionColumn, new ResizableHeader( constants.Version(),
                                                                                      processInstanceListGrid, processVersionColumn ) );
        processInstanceListGrid.setColumnWidth( processVersionColumn, "90px" );
        // Process State
        Column<ProcessInstanceSummary, String> processStateColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                String statusStr = constants.Unknown();
                switch ( object.getState() ) {
                    case ProcessInstance.STATE_ACTIVE:
                        statusStr = constants.Active();
                        break;
                    case ProcessInstance.STATE_ABORTED:
                        statusStr = constants.Aborted();
                        break;
                    case ProcessInstance.STATE_COMPLETED:
                        statusStr = constants.Completed();
                        break;
                    case ProcessInstance.STATE_PENDING:
                        statusStr = constants.Pending();
                        break;
                    case ProcessInstance.STATE_SUSPENDED:
                        statusStr = constants.Suspended();
                        break;

                    default:
                        break;
                }

                return statusStr;
            }
        };
        processStateColumn.setSortable( true );
        sortHandler.setComparator( processStateColumn, new Comparator<ProcessInstanceSummary>() {
            @Override
            public int compare( ProcessInstanceSummary o1,
                                ProcessInstanceSummary o2 ) {
                return Integer.valueOf( o1.getState() ).compareTo( o2.getState() );
            }
        } );
        processInstanceListGrid.addColumn( processStateColumn, new ResizableHeader( constants.State(), processInstanceListGrid,
                                                                                    processStateColumn ) );
        processInstanceListGrid.setColumnWidth( processStateColumn, "100px" );
        // start time
        Column<ProcessInstanceSummary, String> startTimeColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getStartTime();
            }
        };
        startTimeColumn.setSortable( true );
        sortHandler.setComparator( startTimeColumn, new Comparator<ProcessInstanceSummary>() {
            @Override
            public int compare( ProcessInstanceSummary o1,
                                ProcessInstanceSummary o2 ) {
                return Long.valueOf( o1.getStartTime() ).compareTo( Long.valueOf( o2.getStartTime() ) );
            }
        } );
        processInstanceListGrid.addColumn( startTimeColumn, new ResizableHeader( constants.Start_Date(), processInstanceListGrid,
                                                                                 startTimeColumn ) );
        processInstanceListGrid.setColumnWidth( startTimeColumn, "210px" );

        List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();

        cells.add( new DetailsActionHasCell( "Details", new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute( ProcessInstanceSummary processInstance ) {

                DefaultPlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instance Details" );
                placeRequestImpl.addParameter( "processInstanceId", Long.toString( processInstance.getId() ) );
                placeRequestImpl.addParameter( "processDefId", processInstance.getProcessId() );
                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        cells.add( new SignalActionHasCell( "Singal", new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute( ProcessInstanceSummary processInstance ) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Signal Process Popup" );
                placeRequestImpl.addParameter( "processInstanceId", Long.toString( processInstance.getId() ) );

                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        cells.add( new AbortActionHasCell( "Abort", new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute( ProcessInstanceSummary processInstance ) {

                presenter.abortProcessInstance( processInstance.getProcessId(), processInstance.getId() );
            }
        } ) );

        CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>( cells );
        Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
                cell ) {
            @Override
            public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
                return object;
            }
        };
        processInstanceListGrid.addColumn( actionsColumn, new ResizableHeader( constants.Actions(), processInstanceListGrid,
                                                                                    actionsColumn ) );
        processInstanceListGrid.setColumnWidth( actionsColumn, "100px" );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public DataGrid<ProcessInstanceSummary> getDataGrid() {
        return processInstanceListGrid;
    }

    public ListHandler<ProcessInstanceSummary> getSortHandler() {
        return sortHandler;
    }

   

    private class DetailsActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public DetailsActionHasCell( String text,
                                     Delegate<ProcessInstanceSummary> delegate ) {
            cell = new ActionCell<ProcessInstanceSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessInstanceSummary value,
                                    SafeHtmlBuilder sb ) {
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.detailsGridIcon() );
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( "<span title='" + constants.Details() + "' style='margin-right:5px;'>");
                    mysb.append( imageProto.getSafeHtml() );
                    mysb.appendHtmlConstant( "</span>" );
                    sb.append( mysb.toSafeHtml() );

                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
            return object;
        }
    }

    private class AbortActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public AbortActionHasCell( String text,
                                   Delegate<ProcessInstanceSummary> delegate ) {
            cell = new ActionCell<ProcessInstanceSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessInstanceSummary value,
                                    SafeHtmlBuilder sb ) {
                    if ( value.getState() == ProcessInstance.STATE_ACTIVE ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.abortGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.Abort() + "' style='margin-right:5px;'>");
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
            return object;
        }
    }

    private class SignalActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public SignalActionHasCell( String text,
                                    Delegate<ProcessInstanceSummary> delegate ) {
            cell = new ActionCell<ProcessInstanceSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessInstanceSummary value,
                                    SafeHtmlBuilder sb ) {
                    if ( value.getState() == ProcessInstance.STATE_ACTIVE ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.signalGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.Signal() + "' style='margin-right:5px;'>");
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
            return object;
        }
    }

    public void formClosed( @Observes BeforeClosePlaceEvent closed ) {
        if ( "Signal Process Popup".equals( closed.getPlace().getIdentifier() ) ) {
            presenter.refreshActiveProcessList();
        }
    }

    public NavLink getShowAllLink() {
        return showAllLink;
    }

    public NavLink getShowCompletedLink() {
        return showCompletedLink;
    }

    public NavLink getShowAbortedLink() {
        return showAbortedLink;
    }

    public NavLink getShowRelatedToMeLink() {
        return showRelatedToMeLink;
    }
    
    

}
