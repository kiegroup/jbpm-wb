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
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.*;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;


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


    protected V presenter;

    protected FilterPagedTable<T> filterPagedTable;

    protected ExtendedPagedTable<T> currentListGrid;

    private PopupPanel popup = new PopupPanel(true);


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

        Button refreshDropDownButton = new Button();
        createRefreshToggleButton(refreshDropDownButton);
        extendedPagedTable.getRightToolbar().add( refreshDropDownButton );


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

    public void createRefreshToggleButton(final Button refreshIntervalSelector) {

        refreshIntervalSelector.setToggle(true);
        refreshIntervalSelector.setIcon( IconType.LIST_ALT);
        refreshIntervalSelector.setTitle( "Refresh tooltip" );

        popup.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
        popup.addAutoHidePartner(refreshIntervalSelector.getElement());
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (popupPanelCloseEvent.isAutoClosed()) {
                    refreshIntervalSelector.setActive(false);
                }
            }
        });

        refreshIntervalSelector.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!refreshIntervalSelector.isActive() ) {
                    showSelectRefreshIntervalPopup( refreshIntervalSelector.getAbsoluteLeft() + refreshIntervalSelector.getOffsetWidth(),
                            refreshIntervalSelector.getAbsoluteTop() + refreshIntervalSelector.getOffsetHeight(),refreshIntervalSelector);
                } else {
                    popup.hide(false);
                }
            }
        });

    }

    private void showSelectRefreshIntervalPopup(final int left,
                                         final int top,
                                         final Button refreshIntervalSelector) {
        VerticalPanel popupContent = new VerticalPanel();
        
        int configuredSeconds = getMultiGridPreferencesStore().getRefreshInterval();
        if(configuredSeconds>0) {
            presenter.updateRefreshInterval( true,configuredSeconds );
        } else {
            presenter.updateRefreshInterval( false, 0 );
        }

        RadioButton oneMinuteRadioButton = createTimeSelectorRadioButton(10000, "1 Minute", configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton fiveMinuteRadioButton = createTimeSelectorRadioButton(50000, "5 Minutes", configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton tenMinuteRadioButton = createTimeSelectorRadioButton(100000, "10 Minutes", configuredSeconds, refreshIntervalSelector, popupContent);
        
        popupContent.add(oneMinuteRadioButton);
        popupContent.add(fiveMinuteRadioButton);
        popupContent.add(tenMinuteRadioButton);
        
        Button resetButton = new Button( "Disable Autorefresh" );
        resetButton.setSize( ButtonSize.MINI );
        resetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                filterPagedTable.saveNewRegreshInterval( 0 );
                presenter.updateRefreshInterval( false,0 );
                refreshIntervalSelector.setActive( false );
                popup.hide();
            }
        } );

        popupContent.add( resetButton );


        popup.setWidget(popupContent);
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition(finalLeft, top);

    }

    private RadioButton createTimeSelectorRadioButton(int time, String name, int configuredSeconds, final Button refreshIntervalSelector, VerticalPanel popupContent) {
        RadioButton oneMinuteRadioButton = new RadioButton("refreshInterval",name);
        oneMinuteRadioButton.setText( name  );
        final int selectedRefreshTime = time;
        if(configuredSeconds == selectedRefreshTime ) {
            oneMinuteRadioButton.setValue( true );
        }
        
        oneMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                filterPagedTable.saveNewRegreshInterval( selectedRefreshTime );
                presenter.updateRefreshInterval(true, selectedRefreshTime );
                refreshIntervalSelector.setActive( false );
                popup.hide();
                
            }
        } );
        return oneMinuteRadioButton;
    }


}
