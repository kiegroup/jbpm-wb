/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.common.client.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.resources.CommonResources;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractMultiGridPresenter>
        extends Composite implements RequiresResize, MultiGridView<T, V> {

    public static String FILTER_TABLE_SETTINGS = "tableSettings";
    public static String USER_DEFINED = "ud_";

    interface Binder extends UiBinder<Widget, AbstractMultiGridView> {
    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    public User identity;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected DataSetEditorManager dataSetEditorManager;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @UiField
    org.gwtbootstrap3.client.ui.Column column;

    protected V presenter;

    protected FilterPagedTable<T> filterPagedTable;

    protected ExtendedPagedTable<T> currentListGrid;

    protected RowStyles<T> selectedStyles = new RowStyles<T>() {

        @Override
        public String getStyleNames( T row,
                int rowIndex ) {
            if ( rowIndex == selectedRow ) {
                return CommonResources.INSTANCE.css().selected();
            }
            return null;
        }
    };

    protected NoSelectionModel<T> selectionModel;

    protected T selectedItem;

    protected int selectedRow = -1;

    protected DefaultSelectionEventManager<T> noActionColumnManager;

    public GridGlobalPreferences currentGlobalPreferences;
    public Button createTabButton;

    public AbstractMultiGridView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void init( final V presenter,
            final GridGlobalPreferences preferences,
            final Button createNewGridButton ) {
        this.presenter = presenter;
        this.currentGlobalPreferences = preferences;
        this.createTabButton = createNewGridButton;

        filterPagedTable = GWT.create(FilterPagedTable.class);
        column.add( filterPagedTable.makeWidget() );

        filterPagedTable.setPreferencesService( preferencesService );
        preferencesService.call( new RemoteCallback<MultiGridPreferencesStore>() {

            @Override
            public void callback( MultiGridPreferencesStore multiGridPreferencesStore ) {
                if ( multiGridPreferencesStore == null ) {
                    multiGridPreferencesStore = new MultiGridPreferencesStore( preferences.getKey() );
                }
                String selectedGridId = multiGridPreferencesStore.getSelectedGrid();
                filterPagedTable.setMultiGridPreferencesStore( multiGridPreferencesStore );
                presenter.onGridPreferencesStoreLoaded();
                ArrayList<String> existingGrids = multiGridPreferencesStore.getGridsId();

                if ( existingGrids != null && existingGrids.size() > 0 ) {
                    resetDefaultFilterTitleAndDescription();
                    presenter.setAddingDefaultFilters( true );
                    for ( int i = 0; i < existingGrids.size(); i++ ) {
                        String key = existingGrids.get( i );
                        final ExtendedPagedTable<T> extendedPagedTable = loadGridInstance( preferences, key );
                        currentListGrid = extendedPagedTable;
                        extendedPagedTable.setDataProvider( presenter.getDataProvider());
                        final String filterKey = key;
                        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( filterKey );
                            }
                        } );
                        if ( currentListGrid != null && key.equals( selectedGridId ) ) {
                            currentListGrid = extendedPagedTable;
                        }
                    }

                    filterPagedTable.addAddTableButton( createNewGridButton );
                    presenter.setAddingDefaultFilters( false );
                    if ( selectedGridId != null ) {
                        multiGridPreferencesStore.setSelectedGrid( selectedGridId );
                        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
                        filterPagedTable.setSelectedTab();
                    }

                } else {
                    initDefaultFilters( preferences, createNewGridButton );
                }
                initSelectionModel();
            }

        } ).loadUserPreferences( preferences.getKey(), UserPreferencesType.MULTIGRIDPREFERENCES );
    }

    @Override
    public void onResize() {

    }

    public void displayNotification( String text ) {
        notification.fire(new NotificationEvent(text));
    }

    public void showRestoreDefaultFilterConfirmationPopup() {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(Constants.INSTANCE.RestoreDefaultFilters(),
                Constants.INSTANCE.AreYouSureRestoreDefaultFilters(),
                new Command() {
                    @Override public void execute() {
                        showBusyIndicator(Constants.INSTANCE.Loading());
                        restoreTabs();
                    }
                },
                null,
                new Command() {
                    @Override public void execute() {
                    }
                });
        yesNoCancelPopup.show();
    }

    public void restoreTabs(){
        ArrayList<String> existingGrids = getMultiGridPreferencesStore().getGridsId();
        ArrayList<String> allTabs = new ArrayList<String>( existingGrids.size() );

        presenter.setAddingDefaultFilters( true );
        if ( existingGrids != null && existingGrids.size() > 0 ) {

            for ( int i = 0; i < existingGrids.size(); i++ ) {
                allTabs.add( existingGrids.get( i ) );
            }

            for ( int i = 0; i < allTabs.size(); i++ ) {
                filterPagedTable.removeTab( allTabs.get( i ) );
            }

        }
        filterPagedTable.removeTab( 0 );
        initDefaultFilters( currentGlobalPreferences, createTabButton );

    }

  /*
   * By default all the tables will have a refresh button
   */

    public void initGenericToolBar( ExtendedPagedTable<T> extendedPagedTable ) {
    }

    public String getValidKeyForAdditionalListGrid( String baseName ) {
        return filterPagedTable.getValidKeyForAdditionalListGrid( baseName + USER_DEFINED  );
    }

    public ExtendedPagedTable<T> createGridInstance( final GridGlobalPreferences preferences,
            final String key ) {
        final ExtendedPagedTable<T> newListGrid = new ExtendedPagedTable<T>( 10, preferences );
        newListGrid.setShowLastPagerButton( false );
        newListGrid.setShowFastFordwardPagerButton( false );
        newListGrid.setPreferencesService( preferencesService );
        newListGrid.setGridPreferencesStore( new GridPreferencesStore( preferences ) );
        initColumns( newListGrid );
        initGenericToolBar( newListGrid );
        newListGrid.loadPageSizePreferences();
        newListGrid.createPageSizesListBox(5, 20, 5);
        initExtraButtons(newListGrid);

        return newListGrid;
    }


    public ExtendedPagedTable<T> loadGridInstance( final GridGlobalPreferences preferences,
                                                     final String key ) {
        final ExtendedPagedTable<T> newListGrid = new ExtendedPagedTable<T>( 10, preferences );
        newListGrid.setShowLastPagerButton(false);
        newListGrid.setShowFastFordwardPagerButton(false);
        preferencesService.call( new RemoteCallback<GridPreferencesStore>() {

            @Override
            public void callback( GridPreferencesStore preferencesStore ) {
                newListGrid.setPreferencesService( preferencesService );
                if ( preferencesStore == null ) {
                    newListGrid.setGridPreferencesStore( new GridPreferencesStore( preferences ) );
                } else {
                    newListGrid.setGridPreferencesStore( preferencesStore );
                }
                initColumns( newListGrid );
                initGenericToolBar( newListGrid );
                newListGrid.loadPageSizePreferences();
                newListGrid.createPageSizesListBox(5, 20, 5);
            }
        } ).loadUserPreferences( key, UserPreferencesType.GRIDPREFERENCES );
        initExtraButtons( newListGrid );

        return newListGrid;
    }

    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage(message);
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public ExtendedPagedTable<T> getListGrid() {
        return currentListGrid;
    }

    /*
     * For each specific implementation define the
     *  DataGrid columns and how they must be initialized
     */
    public abstract void initColumns( ExtendedPagedTable<T> extendedPagedTable );

    public abstract void initSelectionModel();

    public MultiGridPreferencesStore getMultiGridPreferencesStore() {
        if ( filterPagedTable != null ) {
            return filterPagedTable.getMultiGridPreferencesStore();
        }
        return null;
    }

    public void initExtraButtons( ExtendedPagedTable<T> extendedPagedTable ) {
    }

    public void initDefaultFilters( GridGlobalPreferences preferences,
            Button createTabButton ) {
    }

    public void selectFirstTabAndEnableQueries( final String firsTabKey ) {
        presenter.setAddingDefaultFilters( false );
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getMultiGridPreferencesStore().setSelectedGrid( firsTabKey );
                filterPagedTable.setSelectedTab();
            }
        });
    }

    public void applyFilterOnPresenter( HashMap<String, Object> params ) {
        String tableSettingsJSON = (String) params.get( FILTER_TABLE_SETTINGS );
        FilterSettings tableSettings = dataSetEditorManager.getStrToTableSettings(tableSettingsJSON );
        presenter.filterGrid( tableSettings );
    }

    public void applyFilterOnPresenter( String key ) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }

    public void setIdentity(User identity){
        this.identity = identity;
    }

    public void setPreferencesService(Caller<UserPreferencesService> preferencesService){
        this.preferencesService = preferencesService;
    }

    public abstract void resetDefaultFilterTitleAndDescription();

    protected void saveTabSettings(final String key, final String name, final String description){
        final HashMap<String, Object> tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(key);
        if (tabSettingsValues != null) {
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                                  name);
            tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                                  description);
            filterPagedTable.saveTabSettings(key,
                                             tabSettingsValues);
        }
    }

    public FilterPagedTable<T> getFilterPagedTable() {
        return filterPagedTable;
    }

    public void setFilterPagedTable(FilterPagedTable<T> filterPagedTable) {
        this.filterPagedTable = filterPagedTable;
    }

    public Column<T, String> createTextColumn(final String columnId, final Function<T, String> valueFunction) {
        Column<T, String> column = new Column<T, String>( new TextCell() ) {
            @Override
            public String getValue( T domain ) {
                return valueFunction.apply( domain );
            }
        };
        column.setSortable( true );
        column.setDataStoreName( columnId );
        return column;
    }

    public Column<T, Number> createNumberColumn(final String columnId, final Function<T, Number> valueFunction) {
        Column<T, Number> column = new Column<T, Number>( new NumberCell() ) {
            @Override
            public Number getValue( T domain ) {
                return valueFunction.apply( domain );
            }
        };
        column.setSortable( true );
        column.setDataStoreName( columnId );
        return column;
    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue( int newValue ) {
        filterPagedTable.saveNewRefreshInterval( newValue );
    }
    
}