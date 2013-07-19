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

package org.jbpm.console.ng.pr.client.editors.definition.list;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessDefinitionListViewImpl.html")
public class ProcessDefinitionListViewImpl extends Composite implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

    private Constants constants = GWT.create( Constants.class );
    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private ProcessDefinitionListPresenter presenter;

    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    public IconAnchor refreshIcon;

    @Inject
    @DataField
    public DataGrid<ProcessSummary> processdefListGrid;

    @Inject
    @DataField
    public FlowPanel listContainer;

    public SimplePager pager;

    
    @DataField
    public Heading processDefinitionsLabel = new Heading(4);

    private Set<ProcessSummary> selectedProcessDef;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ProcessDefSelectionEvent> processSelection;
    private ListHandler<ProcessSummary> sortHandler;

    
    
    @Override
    public void init( final ProcessDefinitionListPresenter presenter ) {
        this.presenter = presenter;
        refreshIcon.setTitle( constants.Refresh());
        refreshIcon.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.refreshProcessList();
                searchBox.setText("");
            }
        } );

        listContainer.add( processdefListGrid );
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay(processdefListGrid);
        pager.setPageSize(10);
        listContainer.add( pager );

        processdefListGrid.setHeight( "350px" );
        // Set the message to display when the table is empty.
        Label emptyTable = new Label( constants.No_Process_Definitions_Found() );
        emptyTable.setStyleName( "" );
        processdefListGrid.setEmptyTableWidget( emptyTable );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<ProcessSummary>( presenter.getDataProvider().getList() );
        processdefListGrid.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( processdefListGrid );
        pager.setPageSize( 10 );

        // Add a selection model so we can select cells.
        final MultiSelectionModel<ProcessSummary> selectionModel = new MultiSelectionModel<ProcessSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                selectedProcessDef = selectionModel.getSelectedSet();
                for ( ProcessSummary pd : selectedProcessDef ) {
                    processSelection.fire( new ProcessDefSelectionEvent( pd.getId() ) );
                }
            }
        } );

        processdefListGrid.setSelectionModel( selectionModel,
                                              DefaultSelectionEventManager.<ProcessSummary>createCheckboxManager() );
        
        searchBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 || event.getNativeKeyCode() == 32){
                    displayNotification("Filter: |"+searchBox.getText()+"|");
                    presenter.filterProcessList(searchBox.getText());
                }
                
            }
        });
        
        HTMLPanel span2 = new HTMLPanel(constants.Process_Definitions());
        span2.setStyleName("span2");
        processDefinitionsLabel.add(span2);
        refreshIcon.setCustomIconStyle("icon-jbpm-refresh");
        processDefinitionsLabel.add(refreshIcon);

        initTableColumns( selectionModel );

        presenter.addDataDisplay( processdefListGrid );

    }

    private void initTableColumns( final SelectionModel<ProcessSummary> selectionModel ) {

        // Process Name String.
        Column<ProcessSummary, String> processNameColumn = new Column<ProcessSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessSummary object ) {
                return object.getName();
            }
        };
        processNameColumn.setSortable( true );
        sortHandler.setComparator( processNameColumn, new Comparator<ProcessSummary>() {
            @Override
            public int compare( ProcessSummary o1,
                                ProcessSummary o2 ) {
                return o1.getName().compareTo( o2.getName() );
            }
        } );
        processdefListGrid.addColumn( processNameColumn, new ResizableHeader( constants.Name(), processdefListGrid,
                                                                              processNameColumn ) );

        // Version Type
        Column<ProcessSummary, String> versionColumn = new Column<ProcessSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessSummary object ) {
                return object.getVersion();
            }
        };
        versionColumn.setSortable( true );
        sortHandler.setComparator( versionColumn, new Comparator<ProcessSummary>() {
            @Override
            public int compare( ProcessSummary o1,
                                ProcessSummary o2 ) {
                Integer version1 = ( ( o1.getVersion() == null || o1.getVersion().equals( "" ) ) ) ? 0 : Integer.valueOf( o1
                                                                                                                                  .getVersion() );
                Integer version2 = ( ( o2.getVersion() == null || o2.getVersion().equals( "" ) ) ) ? 0 : Integer.valueOf( o2
                                                                                                                                  .getVersion() );
                return version1.compareTo( version2 );
            }
        } );
        processdefListGrid
                .addColumn( versionColumn, new ResizableHeader( constants.Version(), processdefListGrid, versionColumn ) );
        processdefListGrid.setColumnWidth( versionColumn, "90px" );

        // actions (icons)
        List<HasCell<ProcessSummary, ?>> cells = new LinkedList<HasCell<ProcessSummary, ?>>();

        cells.add( new StartActionHasCell( "Start process", new Delegate<ProcessSummary>() {
            @Override
            public void execute( ProcessSummary process ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Display" );
                placeRequestImpl.addParameter( "processId", process.getId() );
                placeRequestImpl.addParameter( "domainId", process.getDeploymentId() );
                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        cells.add( new DetailsActionHasCell( "Details", new Delegate<ProcessSummary>() {
            @Override
            public void execute( ProcessSummary process ) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Definition Details" );
                placeRequestImpl.addParameter( "processId", process.getId() );
                placeRequestImpl.addParameter( "deploymentId", process.getDeploymentId() );
                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        CompositeCell<ProcessSummary> cell = new CompositeCell<ProcessSummary>( cells );
        Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>( cell ) {
            @Override
            public ProcessSummary getValue( ProcessSummary object ) {
                return object;
            }
        };
        processdefListGrid.addColumn( actionsColumn, new ResizableHeader( constants.Actions(), processdefListGrid, actionsColumn ) );
        processdefListGrid.setColumnWidth( actionsColumn, "70px" );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public DataGrid<ProcessSummary> getDataGrid() {
        return processdefListGrid;
    }

    public ListHandler<ProcessSummary> getSortHandler() {
        return sortHandler;
    }

    public TextBox getSearchBox() {
        return searchBox;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    private class StartActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public StartActionHasCell( String text,
                                   Delegate<ProcessSummary> delegate ) {
            cell = new ActionCell<ProcessSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessSummary value,
                                    SafeHtmlBuilder sb ) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.startGridIcon() );
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( "<span title='" + constants.Start() + "' style='margin-right:5px;'>");
                    mysb.append( imageProto.getSafeHtml() );
                    mysb.appendHtmlConstant( "</span>" );
                    sb.append( mysb.toSafeHtml() );
                }
            };
        }

        @Override
        public Cell<ProcessSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessSummary getValue( ProcessSummary object ) {
            return object;
        }
    }

    private class DetailsActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public DetailsActionHasCell( String text,
                                     Delegate<ProcessSummary> delegate ) {
            cell = new ActionCell<ProcessSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessSummary value,
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
        public Cell<ProcessSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessSummary getValue( ProcessSummary object ) {
            return object;
        }
    }

}
