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

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.resources.CommonResources;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * @param <T>
 * @param <V>
 */
public abstract class AbstractListView<T extends GenericSummary, V extends AbstractListPresenter>
        extends Composite implements RequiresResize {

    @Inject
    public User identity;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    protected V presenter;

    protected ExtendedPagedTable<T> listGrid;

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

    protected Column actionsColumn;

    protected DefaultSelectionEventManager<T> noActionColumnManager;

    public void init( final V presenter,
                      final GridGlobalPreferences preferences ) {
        this.presenter = presenter;
        
        listGrid = new ExtendedPagedTable<T>( preferences );
        listGrid.setShowLastPagerButton( true );
        listGrid.setShowFastFordwardPagerButton( true );
        initWidget( listGrid );
        presenter.addDataDisplay( listGrid );
        preferencesService.call( new RemoteCallback<GridPreferencesStore>() {

            @Override
            public void callback( GridPreferencesStore preferencesStore ) {
                listGrid.setPreferencesService( preferencesService );
                if ( preferencesStore == null ) {
                    listGrid.setGridPreferencesStore( new GridPreferencesStore( preferences ) );
                } else {
                    listGrid.setGridPreferencesStore( preferencesStore );
                }
                presenter.onGridPreferencesStoreLoaded();
                initColumns(listGrid);
                listGrid.loadPageSizePreferences();
            }
        } ).loadUserPreferences(preferences.getKey() , UserPreferencesType.GRIDPREFERENCES);

    }

    @Override
    public void onResize() {

    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void showRestoreDefaultFilterConfirmationPopup(){}

    public ExtendedPagedTable<T> getListGrid() {
        return listGrid;
    }

    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    /*
     * For each specific implementation define the
     *  DataGrid columns and how they must be initialized
     */
    public abstract void initColumns(ExtendedPagedTable<T> extendedPagedTable);

}
