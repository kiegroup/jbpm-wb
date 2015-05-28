/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.list.base;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.dashbuilder.dataset.ValidationError;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.jbpm.console.ng.gc.client.displayer.*;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;

import org.uberfire.ext.services.shared.preferences.*;

import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;

import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

/**
 * @param <T>
 * @param <V>
 * @author salaboy
 */
public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractListPresenter>
        extends Composite implements RequiresResize {

    public static String FILTER_TABLE_SETTINGS = "tableSettings";

    @Inject
    public User identity;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @Inject
    TableDisplayerEditorPopup tableDisplayerEditorPopup;

    @Inject
    TableSettingsJSONMarshaller tableSettingsJSONMarshaller;

    //HashMap<String, TableSettings> tableSettingsHashMap = new HashMap<String, TableSettings>(  );

    protected V presenter;

    protected FilterPagedTable<T> filterPagedTable;

    protected ExtendedPagedTable<T> currentListGrid;


    public DisplayerConstraints displayerConstraints;


    protected RowStyles<T> selectedStyles = new RowStyles<T>() {

        @Override
        public String getStyleNames( T row,
                                     int rowIndex ) {
            if ( rowIndex == selectedRow ) {
                return "selected";
            }
            return null;
        }
    };

    protected NoSelectionModel<T> selectionModel;

    protected T selectedItem;

    protected int selectedRow = -1;

    protected Column actionsColumn;

    protected DefaultSelectionEventManager<T> noActionColumnManager;


    public void init( final V presenter,
                      final GridGlobalPreferences preferences,
                      final Button createNewGridButton ) {
        this.presenter = presenter;

        filterPagedTable = new FilterPagedTable<T>();
        initWidget( filterPagedTable.makeWidget() );


        filterPagedTable.setPreferencesService( preferencesService );
        preferencesService.call( new RemoteCallback<MultiGridPreferencesStore>() {

            @Override
            public void callback( MultiGridPreferencesStore multiGridPreferencesStore ) {
                if ( multiGridPreferencesStore == null ) {
                    multiGridPreferencesStore = new MultiGridPreferencesStore( preferences.getKey() );
                }
                String selectedGridId = multiGridPreferencesStore.getSelectedGrid();
                filterPagedTable.setMultiGridPreferencesStore( multiGridPreferencesStore );
                ArrayList<String> existingGrids = multiGridPreferencesStore.getGridsId();

                if ( existingGrids != null && existingGrids.size() > 0 ) {
                    String key;
                    for ( int i = 0; i < existingGrids.size(); i++ ) {
                        key = existingGrids.get( i );
                        final ExtendedPagedTable<T> extendedPagedTable = createGridInstance( preferences, key );
                        currentListGrid = extendedPagedTable;
                        presenter.addDataDisplay( extendedPagedTable );
                        presenter.setSortHandler( extendedPagedTable );
                        extendedPagedTable.setDataProvider( presenter.dataProvider );
                        final String filterKey = key;
                        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( filterKey );
                            }
                        } );
                        if ( currentListGrid != null && key.equals( selectedGridId ) )
                            currentListGrid = extendedPagedTable;
                    }

                    filterPagedTable.addAddTableButton( createNewGridButton );
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
        notification.fire( new NotificationEvent( text ) );
    }
  /*
   * By default all the tables will have a refresh button
   */

    public void initGenericToolBar( ExtendedPagedTable<T> extendedPagedTable ) {
        Button refreshButton = new Button();
        refreshButton.setIcon( IconType.REFRESH );
        refreshButton.setTitle( Constants.INSTANCE.Refresh() );
        refreshButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.refreshGrid();
            }
        } );
        extendedPagedTable.getRightToolbar().add( refreshButton );
    }

    public String getValidKeyForAdditionalListGrid( String baseName ) {
        return filterPagedTable.getValidKeyForAdditionalListGrid( baseName );
    }


    public ExtendedPagedTable<T> createGridInstance( final GridGlobalPreferences preferences, final String key ) {
        final ExtendedPagedTable<T> newListGrid = new ExtendedPagedTable<T>( 10, preferences );
        newListGrid.setShowLastPagerButton( true );
        newListGrid.setShowFastFordwardPagerButton( true );
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
            }
        } ).loadUserPreferences( key, UserPreferencesType.GRIDPREFERENCES );
        initExtraButtons( newListGrid );

        return newListGrid;
    }

    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
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

    public void initDefaultFilters( GridGlobalPreferences preferences, Button createTabButton ) {
    }

    public void applyFilterOnPresenter( String key ) {
    }

    public void showTableSettingsEditor(String popupTitle, final TableSettings tableSettings,final Command drawCommand) {
        TableSettings clone = tableSettings.cloneInstance();
        clone.setKey( tableSettings.getKey() );
        clone.setDataSet( tableSettings.getDataSet());
        tableDisplayerEditorPopup.setTitle( popupTitle );
        tableDisplayerEditorPopup.show( clone, new TableDisplayerEditor.Listener() {

            public void onClose( TableDisplayerEditor editor ) {
            }

            public void onSave( TableDisplayerEditor editor ) {
                TableSettings modifiedSettings = editor.getTableSettings();
                updateTableSettings( modifiedSettings );
                HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();


                tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, modifiedSettings.getTableName() );
                tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, modifiedSettings.getTableDescription() );
                tabSettingsValues.put( FILTER_TABLE_SETTINGS, getTableSettingsToStr( modifiedSettings ) );
                GWT.log( "AbstractMultiGridView json .........." + getTableSettingsToStr( tableSettings ) );

                filterPagedTable.saveNewTabSettings( modifiedSettings.getKey(),tabSettingsValues );
                drawCommand.execute();
            }
        } );
    }


 //   public TableSettings getTableSettingsByKey(String key) {
 //       return tableSettingsHashMap.get( key );
 //   }

    public void addTableSettings(TableSettings settings) {
//        tableSettingsHashMap.put(settings.getKey(),settings);

        // Take the first registered settings as the default one
        if (presenter.getCurrentTableSettings() == null) {
            checkDisplayerSettings( settings );
        }
    }
    public void checkDisplayerSettings(DisplayerSettings displayerSettings) {
        if(displayerConstraints== null){
            displayerConstraints = createDisplayerConstraints();
        }
        if (displayerConstraints != null) {
            ValidationError error = displayerConstraints.check(displayerSettings);
            if (error != null) throw error;
        }
    }
    public void updateTableSettings(TableSettings tableSettings) {
        addTableSettings( tableSettings );
    }

    public DisplayerConstraints createDisplayerConstraints(){
        return null;
    }

    public TableSettings createTableSettingsPrototype() {
        return null;
    }

    public String getTableSettingsToStr(TableSettings tableSettings){
        return tableSettingsJSONMarshaller.toJsonString( tableSettings );
    }
    public TableSettings getStrToTableSettings(String json){
        return tableSettingsJSONMarshaller.fromJsonString( json );
    }

}
