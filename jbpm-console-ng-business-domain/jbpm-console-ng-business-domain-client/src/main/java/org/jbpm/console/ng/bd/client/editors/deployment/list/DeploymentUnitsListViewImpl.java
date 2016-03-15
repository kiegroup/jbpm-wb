/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.client.editors.deployment.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.bd.client.editors.deployment.newunit.NewDeploymentPopup;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.events.DeployedUnitChangedEvent;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.gc.client.util.ButtonActionCell;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

@Dependent
public class DeploymentUnitsListViewImpl extends AbstractListView<KModuleDeploymentUnitSummary, DeploymentUnitsListPresenter>
        implements DeploymentUnitsListPresenter.DeploymentUnitsListView {

    public static final String COL_ID_DEPLOYMENT = "Deployment";
    public static final String COL_ID_GROUP = "GroupId";
    public static final String COL_ID_ARTIFACT = "Artifact";
    public static final String COL_ID_VERSION = "Version";
    public static final String COL_ID_KIEBASENAME = "KieBaseName";
    public static final String COL_ID_KIEBSESSIONNAME = "KieSessionName";
    public static final String COL_ID_STRATEGY = "Strategy";
    public static final String COL_ID_STATUS = "Status";
    public static final String COL_ID_ACTIONS = "Actions";

    interface Binder
            extends
            UiBinder<Widget, DeploymentUnitsListViewImpl> {

    }

    private Constants constants = Constants.INSTANCE;

    @Inject
    private NewDeploymentPopup newDeploymentPopup;

    @Override
    public void init( final DeploymentUnitsListPresenter presenter ) {

        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add( COL_ID_DEPLOYMENT );
        bannedColumns.add( COL_ID_ACTIONS );
        List<String> initColumns = new ArrayList<String>();
        initColumns.add( COL_ID_DEPLOYMENT );
        initColumns.add( COL_ID_STRATEGY );
        initColumns.add( COL_ID_STATUS );
        initColumns.add( COL_ID_ACTIONS );

        super.init( presenter, new GridGlobalPreferences( "DeploymentUnitsGrid", initColumns, bannedColumns ) );

        selectionModel = new NoSelectionModel<KModuleDeploymentUnitSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {
                boolean close = false;
                if ( selectedRow == -1 ) {
                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else if ( listGrid.getKeyboardSelectedRow() != selectedRow ) {

                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

            }
        } );

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<KModuleDeploymentUnitSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<KModuleDeploymentUnitSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<KModuleDeploymentUnitSummary> event ) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if ( BrowserEvents.CLICK.equals( nativeEvent.getType() ) ) {
                            // Ignore if the event didn't occur in the correct column.
                            if ( listGrid.getColumnIndex( actionsColumn ) == event.getColumn() ) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                } );
        listGrid.setSelectionModel( selectionModel, noActionColumnManager );

        listGrid.setEmptyTableCaption( constants.No_Deployment_Units_Available() );
        listGrid.setRowStyles( selectedStyles );

        listGrid.getElement().getStyle().setPaddingRight( 20, Style.Unit.PX );
        listGrid.getElement().getStyle().setPaddingLeft( 20, Style.Unit.PX );
    }

    @Override
    public void initColumns( ExtendedPagedTable extendedPagedTable ) {
        Column<KModuleDeploymentUnitSummary, ?> unitIdColumn = idColumn();
        Column<KModuleDeploymentUnitSummary, ?> groupIdColumn = groupIdColumn();
        Column<KModuleDeploymentUnitSummary, ?> artifactIdColumn = artifactIdColumn();
        Column<KModuleDeploymentUnitSummary, ?> versionColumn = versionColumn();
        Column<KModuleDeploymentUnitSummary, ?> kbaseColumn = kbaseColumn();
        Column<KModuleDeploymentUnitSummary, ?> ksessionColumn = ksessionColumn();
        Column<KModuleDeploymentUnitSummary, ?> strategyColumn = strategyColumn();
        Column<KModuleDeploymentUnitSummary, ?> statusColumn = statusColumn();
        actionsColumn = actionsColumn();

        List<ColumnMeta<KModuleDeploymentUnitSummary>> columnMetas = new ArrayList<ColumnMeta<KModuleDeploymentUnitSummary>>();
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( unitIdColumn, constants.Deployment() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( groupIdColumn, constants.GroupID() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( artifactIdColumn, constants.Artifact() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( versionColumn, constants.Version() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( kbaseColumn, constants.KieBaseName() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( ksessionColumn, constants.KieSessionName() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( strategyColumn, constants.Strategy() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( statusColumn, constants.Status() ) );
        columnMetas.add( new ColumnMeta<KModuleDeploymentUnitSummary>( actionsColumn, constants.Actions() ) );
        listGrid.addColumns( columnMetas );
    }

    private Column<KModuleDeploymentUnitSummary, ?> idColumn() {
        Column<KModuleDeploymentUnitSummary, String> unitIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                return unit.getId();
            }
        };
        unitIdColumn.setSortable( true );
        unitIdColumn.setDataStoreName( COL_ID_DEPLOYMENT );
        return unitIdColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> groupIdColumn() {
        Column<KModuleDeploymentUnitSummary, String> groupIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                return unit.getGroupId();
            }
        };
        groupIdColumn.setSortable( true );
        groupIdColumn.setDataStoreName( COL_ID_GROUP );
        return groupIdColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> artifactIdColumn() {
        Column<KModuleDeploymentUnitSummary, String> artifactIdColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                return unit.getArtifactId();
            }
        };
        artifactIdColumn.setSortable( true );
        artifactIdColumn.setDataStoreName( COL_ID_ARTIFACT );
        return artifactIdColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> versionColumn() {
        Column<KModuleDeploymentUnitSummary, String> versionColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                return unit.getVersion();
            }
        };
        versionColumn.setSortable( true );
        versionColumn.setDataStoreName( COL_ID_VERSION );
        return versionColumn;

    }

    private Column<KModuleDeploymentUnitSummary, ?> kbaseColumn() {
        Column<KModuleDeploymentUnitSummary, String> kbaseColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                String kbaseName = unit.getKbaseName();
                if ( kbaseName == null || kbaseName.trim().isEmpty() ) {
                    kbaseName = "DEFAULT";
                }
                return kbaseName;
            }
        };
        kbaseColumn.setDataStoreName( COL_ID_KIEBASENAME );
        kbaseColumn.setSortable( true );
        return kbaseColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> ksessionColumn() {
        Column<KModuleDeploymentUnitSummary, String> ksessionColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                String ksessionName = unit.getKsessionName();
                if ( ksessionName == null || ksessionName.trim().isEmpty() ) {
                    ksessionName = "DEFAULT";
                }
                return ksessionName;
            }
        };
        ksessionColumn.setDataStoreName( COL_ID_KIEBSESSIONNAME );
        ksessionColumn.setSortable( true );
        return ksessionColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> strategyColumn() {
        Column<KModuleDeploymentUnitSummary, String> strategyColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                return unit.getStrategy();
            }
        };
        strategyColumn.setSortable( true );
        strategyColumn.setDataStoreName( COL_ID_STRATEGY );
        return strategyColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> statusColumn() {
        Column<KModuleDeploymentUnitSummary, String> statusColumn = new Column<KModuleDeploymentUnitSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( KModuleDeploymentUnitSummary unit ) {
                if ( unit.isActive() ) {
                    return constants.Active();
                } else {
                    return constants.NotActive();
                }

            }
        };
        statusColumn.setSortable( true );
        statusColumn.setDataStoreName( COL_ID_STATUS );
        return statusColumn;
    }

    private Column<KModuleDeploymentUnitSummary, ?> actionsColumn() {
        List<HasCell<KModuleDeploymentUnitSummary, ?>> cells = new LinkedList<HasCell<KModuleDeploymentUnitSummary, ?>>();

        cells.add( new ActivateDeactivateActionHasCell( new Delegate<KModuleDeploymentUnitSummary>() {
            @Override
            public void execute( KModuleDeploymentUnitSummary unit ) {

                presenter.activateOrDeactivate( unit, !unit.isActive() );
            }

        } ) );

        cells.add( new ButtonActionCell<KModuleDeploymentUnitSummary>( constants.Undeploy(), new Delegate<KModuleDeploymentUnitSummary>() {
            @Override
            public void execute( KModuleDeploymentUnitSummary unit ) {

                if ( Window.confirm( constants.Undeploy_Question() ) ) {
                    presenter.undeployUnit( unit.getId(), unit.getGroupId(), unit.getArtifactId(), unit.getVersion(),
                                            unit.getKbaseName(), unit.getKsessionName() );
                }

            }
        } ) );

        CompositeCell<KModuleDeploymentUnitSummary> cell = new CompositeCell<KModuleDeploymentUnitSummary>( cells );
        Column<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary> actionColum =
                new Column<KModuleDeploymentUnitSummary, KModuleDeploymentUnitSummary>(
                        cell ) {
                    @Override
                    public KModuleDeploymentUnitSummary getValue( KModuleDeploymentUnitSummary object ) {
                        return object;
                    }
                };
        actionColum.setDataStoreName( COL_ID_ACTIONS );
        return actionColum;
    }

    public void refreshOnChangedUnit( @Observes DeployedUnitChangedEvent event ) {
        presenter.refreshGrid();
    }

    private class ActivateDeactivateActionHasCell extends ButtonActionCell<KModuleDeploymentUnitSummary> {

        public ActivateDeactivateActionHasCell( final Delegate<KModuleDeploymentUnitSummary> delegate ) {
            super( delegate );
        }

        @Override
        public String getText( final KModuleDeploymentUnitSummary value ) {
            return value.isActive() ? constants.Deactivate() : constants.Activate();
        }

    }

    @Override
    public void newDeploymentUnit() {
        newDeploymentPopup.show();
    }
}
