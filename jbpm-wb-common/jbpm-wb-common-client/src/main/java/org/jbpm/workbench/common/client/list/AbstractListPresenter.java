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

import java.util.List;

import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jbpm.workbench.common.client.list.event.DeselectAllItemsEvent;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.model.QueryFilter;
import org.uberfire.paging.PageResponse;

/**
 * @param <T> data type for the AsyncDataProvider
 */
public abstract class AbstractListPresenter<T> implements RefreshMenuBuilder.SupportsRefresh {

    protected AsyncDataProvider<T> dataProvider;

    protected QueryFilter currentFilter;

    private Constants constants = GWT.create(Constants.class);

    public AbstractListPresenter() {
        initDataProvider();
    }

    protected abstract ListView getListView();

    public abstract void getData(Range visibleRange);

    protected void initDataProvider() {
        dataProvider = new AsyncDataProvider<T>() {
            @Override
            protected void onRangeChanged(HasData<T> display) {
                getListView().showBusyIndicator(constants.Loading());
                final Range visibleRange = display.getVisibleRange();
                getData(visibleRange);
            }
        };
    }

    public void updateDataOnCallback(PageResponse response) {
        updateDataOnCallback(response.getPageRowList(),
                             response.getStartRowIndex(),
                             response.getTotalRowSize(),
                             response.isTotalRowSizeExact());
    }

    public void updateDataOnCallback(List<T> instanceSummaries,
                                     int startRange,
                                     int totalRowCount,
                                     boolean isExact) {
        dataProvider.updateRowCount(totalRowCount,
                                    isExact);

        dataProvider.updateRowData(startRange,
                                   instanceSummaries);

        getListView().hideBusyIndicator();
    }

    public void addDataDisplay(final HasData<T> display) {
        if (dataProvider.getDataDisplays().size() == 1) {
            dataProvider.removeDataDisplay(dataProvider.getDataDisplays().iterator().next());
        }
        dataProvider.addDataDisplay(display);
    }

    public AsyncDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    protected void setDataProvider(AsyncDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void onRefresh() {
        refreshGrid();
    }

    public void refreshGrid() {
        if (getListView().getListGrid() != null) {
            getListView().getListGrid().setVisibleRangeAndClearData(getListView().getListGrid().getVisibleRange(),
                                                                    true);
        }
    }

    public void onDeselectAllItemsEvent(@Observes DeselectAllItemsEvent event) {
        deselectAllItems();
    }

    public void deselectAllItems() {
        if (getListView().getListGrid() != null) {
            getListView().getListGrid().deselectAllItems();
        }
    }
}
