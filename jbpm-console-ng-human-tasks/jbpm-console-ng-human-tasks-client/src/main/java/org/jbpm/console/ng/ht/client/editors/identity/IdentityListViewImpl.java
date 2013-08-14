/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.ht.client.editors.identity;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;
import org.jbpm.console.ng.ht.client.editors.identity.IdentityListPresenter.IdentityType;

@Dependent
@Templated(value = "IdentityListViewImpl.html")
public class IdentityListViewImpl extends Composite implements IdentityListPresenter.IdentityListView, RequiresResize {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;

    private IdentityListPresenter presenter;

    @Inject
    @DataField
    public LayoutPanel listContainer;

    @Inject
    @DataField
    public TextBox filterUserText;

    @Inject
    @DataField
    public Button filterUserButton;

    @Inject
    @DataField
    public DataGrid<IdentitySummary> identityListGrid;

    
    @DataField
    public SimplePager pager;
    
    @Inject
    @DataField
    public ListBox identityTypesList;

    @Inject
    private Event<NotificationEvent> notification;
    
    private ListHandler<IdentitySummary> sortHandler;
    
    private static final String ENTITY_TYPE = "Entity type";
    
    private static final String ENTITY_ID = "Entity id";

    public IdentityListViewImpl() {
        pager = new SimplePager(SimplePager.TextLocation.CENTER, false, true);
    }
    
    @Override
    public void onResize() {
        if( (getParent().getOffsetHeight()-120) > 0 ){
            listContainer.setHeight(getParent().getOffsetHeight()-120+"px");
        }
    }
    
    @Override
    public void init( IdentityListPresenter presenter ) {
        this.presenter = presenter;
        initializeList();
        initializeGridView();
    }
    
    private void initializeList(){
        for ( IdentityType type : IdentityType.values() ) {
            identityTypesList.addItem( type.toString(),  type.toString() );
        }
        identityTypesList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                filterUserText.setValue("");
                presenter.refreshIdentityList();
            }
        });
    }
    
    private void initializeGridView(){
        listContainer.add( identityListGrid );

        // Set the message to display when the table is empty.
        identityListGrid.setEmptyTableWidget( new Label( "No User/Groups Available" ) );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<IdentitySummary>( presenter.getDataProvider().getList() );
        identityListGrid.addColumnSortHandler( sortHandler );

        pager.setStyleName("pagination pagination-right pull-right");
        pager.setDisplay( identityListGrid );
        pager.setPageSize( 10 );

        initTableColumns();
        presenter.addDataDisplay( identityListGrid );
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initTableColumns(){
        //id
        Column<IdentitySummary, String> identityIdColumn = new Column<IdentitySummary, String>( new TextCell() ) {
            @Override
            public String getValue( IdentitySummary object ) {
                return object.getId();
            }
        };
        identityListGrid.addColumn( identityIdColumn, new ResizableHeader( ENTITY_ID, identityListGrid, identityIdColumn ) );

        //type
        Column<IdentitySummary, String> identityTypeColumn = new Column<IdentitySummary, String>( new TextCell() ) {
            @Override
            public String getValue( IdentitySummary object ) {
                return object.getType();
            }
        };

        identityListGrid
                .addColumn( identityTypeColumn, new ResizableHeader( ENTITY_TYPE, identityListGrid, identityTypeColumn ) );
    }

    @EventHandler("filterUserButton")
    public void refreshButton( ClickEvent e ) {
        if ( filterUserText.getText() != null && filterUserText.getText().length() > 0 ) {
            this.searchEntity();
        } else {
            presenter.refreshIdentityList();
        }

    }
    
    private void searchEntity(){
        if(identityTypesList.getValue().equals(IdentityType.USERS.toString())){
            presenter.getUserById( filterUserText.getText() );
        }else{
            presenter.getGroupById( filterUserText.getText() );
        }
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );

    }

    @Override
    public TextBox getUserText() {
        return this.filterUserText;
    }

    @Override
    public DataGrid<IdentitySummary> getDataGrid() {
        return this.identityListGrid;
    }

    @Override
    public ListBox getIdentityTypesList() {
        return this.identityTypesList;
    }

}