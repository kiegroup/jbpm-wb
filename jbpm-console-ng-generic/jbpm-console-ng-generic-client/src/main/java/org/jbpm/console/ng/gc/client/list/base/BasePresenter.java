/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.gc.client.list.base;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.GenericSummary;
import org.jbpm.console.ng.ht.model.events.SearchEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

public abstract class BasePresenter<T extends GenericSummary, V> extends BaseGenericCRUD {

    protected static Constants constants = GWT.create(Constants.class);

    protected ListDataProvider<T> dataProvider = new ListDataProvider<T>();

    protected List<T> allItemsSummaries;

    // menu
    protected Menus menus;
    protected String NEW_ITEM_MENU = constants.New_Item();
    protected String REFRESH_ITEM_MENU = constants.Refresh();

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected V view;

    protected abstract void refreshItems();

    protected abstract void onSearchEvent(@Observes final SearchEvent searchEvent);

    public void addDataDisplay(HasData<T> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    protected void refreshData() {
        dataProvider.refresh();
    }

    protected void makeMenuBar() {
        menus = MenuFactory.newTopLevelMenu(NEW_ITEM_MENU).respondsWith(new Command() {
            @Override
            public void execute() {
                createItem();
            }
        }).endMenu().newTopLevelMenu(REFRESH_ITEM_MENU).respondsWith(new Command() {
            @Override
            public void execute() {
                refreshItems();
            }
        }).endMenu().build();
    }

    protected void filterItems(String text, DataGrid<T> listGrid) {
        ColumnSortList.ColumnSortInfo sortInfo = listGrid.getColumnSortList().size() > 0 ? listGrid.getColumnSortList().get(0)
                : null;
        if (allItemsSummaries != null) {
            this.filterGrid(sortInfo, text, listGrid);
        }
    }

    protected void filterGrid(ColumnSortList.ColumnSortInfo sortInfo, String text, DataGrid<T> listGrid) {
        List<T> filtereditems = Lists.newArrayList();
        if (!text.equals("")) {
            for (T ts : allItemsSummaries) {
                if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
                    filtereditems.add(ts);
                }
            }
        } else {
            filtereditems = allItemsSummaries;
        }
        dataProvider.getList().clear();
        dataProvider.getList().addAll(filtereditems);
        if (sortInfo != null && sortInfo.isAscending()) {
            listGrid.getColumnSortList().clear();
            ColumnSortInfo columnSortInfo = new ColumnSortInfo(sortInfo.getColumn(), sortInfo.isAscending());
            listGrid.getColumnSortList().push(columnSortInfo);
            ColumnSortEvent.fire(listGrid, listGrid.getColumnSortList());
        }
    }

}