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

package org.jbpm.console.ng.gc.client.gridexp;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jbpm.console.ng.gc.client.experimental.pagination.DataMockSummary;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Grid Experimental")
public class GridExpListPresenter {

    public interface GridExpListView extends UberView<GridExpListPresenter> {

        void displayNotification( String text );

        void showBusyIndicator( String message );

        void hideBusyIndicator();
    }

    private Menus menus;

    private Constants constants = GWT.create(Constants.class);

    private ListDataProvider<DataMockSummary> dataProvider = new ListDataProvider<DataMockSummary>();

    private List<DataMockSummary> data;

    @Inject
    private GridExpListView view;

    @Inject
    private DataService dataServices;

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Grid Experiment";
    }

    @WorkbenchPartView
    public UberView<GridExpListPresenter> getView() {
        return view;
    }

    public GridExpListPresenter() {
        makeMenuBar();
    }

    public void refreshList() {
        data = dataServices.getData();

        if ( data != null ) {
            dataProvider.getList().clear();
            dataProvider.getList().addAll( new ArrayList<DataMockSummary>( data ) );
            dataProvider.refresh();

        }
    }

    public void addDataDisplay( HasData<DataMockSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<DataMockSummary> getDataProvider() {
        return dataProvider;
    }

    @OnOpen
    public void onOpen() {
        refreshList();
    }

    @OnFocus
    public void onFocus() {
        refreshList();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( "Refresh" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshList();
                        view.displayNotification( "Refreshed" );
                    }
                } )
                .endMenu().
                        build();

    }
}
