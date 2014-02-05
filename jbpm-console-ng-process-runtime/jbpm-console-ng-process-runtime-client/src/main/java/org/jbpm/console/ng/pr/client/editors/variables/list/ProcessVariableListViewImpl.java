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

package org.jbpm.console.ng.pr.client.editors.variables.list;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import java.util.Date;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.client.util.DataGridUtils;
import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessVariableListViewImpl.html")
public class ProcessVariableListViewImpl extends Composite implements
                                                              ProcessVariableListPresenter.ProcessVariableListView, RequiresResize {

    private ProcessVariableListPresenter presenter;

   
    
    @Inject
    @DataField
    public Label processInstanceIdLabel;
    
    @Inject
    @DataField
    public HTML processInstanceIdText;

    @Inject
    @DataField
    public Label processNameLabel;
    
    @Inject
    @DataField
    public HTML processNameText;

    @Inject
    @DataField
    public Label processDefinitionIdLabel;
    
    @Inject
    @DataField
    public HTML processDefinitionIdText;
    
    @Inject
    @DataField
    public LayoutPanel listContainer;

    @Inject
    @DataField
    public DataGrid<VariableSummary> processDataGrid;

    @DataField
    public SimplePager pager;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private ColumnSortEvent.ListHandler<VariableSummary> sortHandler;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );
    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );
    private ProcessInstanceSummary processInstance;
    private Path processAssetPath;
    private String encodedProcessSource;
    private List<NodeInstanceSummary> activeNodes;
    private List<NodeInstanceSummary> completedNodes;

    public ProcessVariableListViewImpl() {
        pager = new SimplePager(SimplePager.TextLocation.LEFT, false, true);
    }

    @Override
    public void init( final ProcessVariableListPresenter presenter ) {
        this.presenter = presenter;
        listContainer.add( processDataGrid );
        
        
        pager.setDisplay(processDataGrid);
        pager.setPageSize( 9 );
        
        processNameLabel.setText( constants.Process_Definition_Name() );
        processDefinitionIdLabel.setText( constants.Process_Definition_Id() );
        processInstanceIdLabel.setText(constants.Process_Instance_ID());

        // Set the message to display when the table is empty.
        
        processDataGrid.setEmptyTableWidget( new HTMLPanel(constants.No_Variables_Available()) );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<VariableSummary>( presenter.getDataProvider().getList() );

        processDataGrid.addColumnSortHandler( sortHandler );
        
        initTableColumns();

        presenter.addDataDisplay( processDataGrid );
    }
    
    @Override
    public void onResize() {
        if( (getParent().getOffsetHeight()-120) > 0 ){
            listContainer.setHeight(getParent().getOffsetHeight()-120+"px");
        }
    }

     public void formClosed( @Observes BeforeClosePlaceEvent closed ) {
        if ( "Edit Variable Popup".equals( closed.getPlace().getIdentifier() ) ) {
            presenter.loadVariables( processInstanceIdText.getText(), processDefinitionIdText.getText() );
        }
    }
    
    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public HTML getProcessNameText() {
        return processNameText;
    }

    private void initTableColumns() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        // Id
        Column<VariableSummary, String> variableId = new Column<VariableSummary, String>( new TextCell() ) {
            @Override
            public void render(Cell.Context context, VariableSummary variableSummary, SafeHtmlBuilder sb) {
                String title = variableSummary.getVariableId();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( VariableSummary object ) {
                return DataGridUtils.trimToColumnWidth(processDataGrid, this, object.getVariableId());
            }
        };
        variableId.setSortable( true );

        processDataGrid.addColumn( variableId, new ResizableHeader( constants.Name(), 75, processDataGrid, variableId ) );
        sortHandler.setComparator( variableId, new Comparator<VariableSummary>() {
            @Override
            public int compare( VariableSummary o1,
                                VariableSummary o2 ) {
                return o1.getVariableId().compareTo( o2.getVariableId() );
            }
        } );

        // Value.
        Column<VariableSummary, String> valueColumn = new Column<VariableSummary, String>( new TextCell() ) {
            @Override
            public void render(Cell.Context context, VariableSummary variableSummary, SafeHtmlBuilder sb) {
                String title = variableSummary.getNewValue();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( VariableSummary object ) {
                return DataGridUtils.trimToColumnWidth(processDataGrid, this, object.getNewValue());
            }
        };
        valueColumn.setSortable( true );

        processDataGrid.addColumn( valueColumn, new ResizableHeader( constants.Value(), processDataGrid, valueColumn ) );
        sortHandler.setComparator( valueColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare( VariableSummary o1,
                                VariableSummary o2 ) {
                return o1.getNewValue().compareTo( o2.getNewValue() );
            }
        } );

        // Type.
        Column<VariableSummary, String> typeColumn = new Column<VariableSummary, String>( new TextCell() ) {
            @Override
            public void render(Cell.Context context, VariableSummary variableSummary, SafeHtmlBuilder sb) {
                String title = variableSummary.getType();
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }

            @Override
            public String getValue( VariableSummary object ) {
                return DataGridUtils.trimToColumnWidth(processDataGrid, this, object.getType());
            }
        };
        typeColumn.setSortable( true );

        processDataGrid.addColumn( typeColumn, new ResizableHeader( constants.Type(), processDataGrid, typeColumn ) );
        sortHandler.setComparator( typeColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare( VariableSummary o1,
                                VariableSummary o2 ) {
                return o1.getType().compareTo( o2.getType() );
            }
        } );

        // Last Time Changed Date.
        Column<VariableSummary, String> lastModificationColumn = new Column<VariableSummary, String>( new TextCell() ) {
            @Override
            public void render(Cell.Context context, VariableSummary variableSummary, SafeHtmlBuilder sb) {
                Date lastMofidication = new Date(variableSummary.getTimestamp());
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                String title = format.format(lastMofidication);
                sb.append(DataGridUtils.createDivStart(title));
                super.render(context, variableSummary, sb);
                sb.append(DataGridUtils.createDivEnd());
            }
            @Override
            public String getValue( VariableSummary object ) {
                Date lastMofidication = new Date(object.getTimestamp());
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                return DataGridUtils.trimToColumnWidth(processDataGrid, this, format.format(lastMofidication));
            }
        };
        lastModificationColumn.setSortable( true );

        processDataGrid.addColumn( lastModificationColumn, new ResizableHeader( constants.Last_Modification(), processDataGrid,
                                                                       lastModificationColumn ) );
        sortHandler.setComparator( lastModificationColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare( VariableSummary o1,
                                VariableSummary o2 ) {

                return new Long(o1.getTimestamp()).compareTo( new Long(o2.getTimestamp()) );
            }
        } );

        List<HasCell<VariableSummary, ?>> cells = new LinkedList<HasCell<VariableSummary, ?>>();

        cells.add( new EditVariableActionHasCell( "Edit Variable", new Delegate<VariableSummary>() {
            @Override
            public void execute( VariableSummary variable ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Edit Variable Popup" );
                placeRequestImpl.addParameter( "processInstanceId", Long.toString( variable.getProcessInstanceId() ) );
                placeRequestImpl.addParameter( "variableId", variable.getVariableId() );
                placeRequestImpl.addParameter( "value", variable.getNewValue() );

                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        cells.add( new VariableHistoryActionHasCell( "Variable History", new Delegate<VariableSummary>() {
            @Override
            public void execute( VariableSummary variable ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Variable History Popup" );
                placeRequestImpl.addParameter( "processInstanceId", Long.toString( variable.getProcessInstanceId() ) );
                placeRequestImpl.addParameter( "variableId", variable.getVariableId() );

                placeManager.goTo( placeRequestImpl );
            }
        } ) );

        CompositeCell<VariableSummary> cell = new CompositeCell<VariableSummary>( cells );
        Column<VariableSummary, VariableSummary> actionsColumn = new Column<VariableSummary, VariableSummary>( cell ) {
                                                              @Override
                                                              public VariableSummary getValue( VariableSummary object ) {
                                                                  return object;
                                                              }
                                                          };
        processDataGrid.addColumn(actionsColumn , new ResizableHeader( constants.Actions(), processDataGrid,
                                                                       actionsColumn ) );
    }

    @Override
    public HTML getProcessInstanceIdText() {
        return this.processInstanceIdText;
    }

    @Override
    public HTML getProcessDefinitionIdText() {
        return this.processDefinitionIdText;
    }

    @Override
    public void setProcessInstance(ProcessInstanceSummary pi) {
        this.processInstance = pi;
    }

    
    
    private class EditVariableActionHasCell implements HasCell<VariableSummary, VariableSummary> {

        private ActionCell<VariableSummary> cell;

        public EditVariableActionHasCell( String text,
                                          Delegate<VariableSummary> delegate ) {
            cell = new ActionCell<VariableSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    VariableSummary value,
                                    SafeHtmlBuilder sb ) {
                    if ( processInstance.getState() == ProcessInstance.STATE_ACTIVE ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.editGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.Edit_Variable() + "'>" );
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
                    }
                }
            };
        }

        @Override
        public Cell<VariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<VariableSummary, VariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public VariableSummary getValue( VariableSummary object ) {
            return object;
        }

    }

    private class VariableHistoryActionHasCell implements HasCell<VariableSummary, VariableSummary> {

        private ActionCell<VariableSummary> cell;

        public VariableHistoryActionHasCell( String text,
                                             Delegate<VariableSummary> delegate ) {
            cell = new ActionCell<VariableSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    VariableSummary value,
                                    SafeHtmlBuilder sb ) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.historyGridIcon() );
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( "<span title='" + constants.Variables_History() + "'>" );
                    mysb.append( imageProto.getSafeHtml() );
                    mysb.appendHtmlConstant( "</span>" );
                    sb.append( mysb.toSafeHtml() );
                }
            };
        }

        @Override
        public Cell<VariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<VariableSummary, VariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public VariableSummary getValue( VariableSummary object ) {
            return object;
        }

    }

    

    public List<NodeInstanceSummary> getActiveNodes() {
        return activeNodes;
    }

    public void setActiveNodes(List<NodeInstanceSummary> activeNodes) {
        this.activeNodes = activeNodes;
    }

    public List<NodeInstanceSummary> getCompletedNodes() {
        return completedNodes;
    }

    public void setCompletedNodes(List<NodeInstanceSummary> completedNodes) {
        this.completedNodes = completedNodes;
    }

    public Path getProcessAssetPath() {
        return processAssetPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

    
    
}
