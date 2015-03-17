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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserDataGridPreferencesService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * @param <T>
 * @param <V>
 * @author salaboy
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
    private Caller<UserDataGridPreferencesService> preferencesService;

    protected V presenter;

    protected ExtendedPagedTable<T> listGrid;

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

    public interface BasicListView<T extends GenericSummary> extends IsWidget {

        void showBusyIndicator( String message );

        void hideBusyIndicator();

        void displayNotification( String text );

        ExtendedPagedTable<T> getListGrid();

    }

    public interface ListView<T extends GenericSummary, V> extends BasicListView<T>,
                                                                   UberView<V> {

    }

    public void init( V presenter,
                      final GridGlobalPreferences preferences ) {
        this.presenter = presenter;

        listGrid = new ExtendedPagedTable<T>( 10, preferences );
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
                initColumns();
                initGenericToolBar();
                initFilters();
                listGrid.setPageSizeValue();
            }
        } ).loadGridPreferences( preferences.getKey() );

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

    public void initGenericToolBar() {
        Button refreshButton = new Button();
        refreshButton.setIcon( IconType.REFRESH );
        refreshButton.setTitle( Constants.INSTANCE.Refresh() );
        refreshButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.refreshGrid();
            }
        } );
        listGrid.getRightToolbar().add( refreshButton );
    }

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
    public abstract void initColumns();

    public abstract void initFilters();
}
