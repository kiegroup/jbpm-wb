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
package org.jbpm.workbench.common.client.list.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jbpm.workbench.common.client.i18n.Constants;
import org.jbpm.workbench.common.client.list.base.events.SearchEvent;
import org.jbpm.workbench.common.model.QueryFilter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.paging.PageResponse;


/**
 * @param <T> data type for the AsyncDataProvider
 */
public abstract class AbstractListPresenter<T> implements RefreshMenuBuilder.SupportsRefresh,
        RefreshSelectorMenuBuilder.SupportsRefreshInterval,
        RestoreDefaultFiltersMenuBuilder.SupportsRestoreDefaultFilters {

    protected AsyncDataProvider<T> dataProvider;

    protected QueryFilter currentFilter;

    protected String textSearchStr = "";

    private Constants constants = GWT.create(Constants.class);

    protected boolean addingDefaultFilters = false;

    protected Timer refreshTimer = null;
    protected boolean autoRefreshEnabled = false;
    protected int autoRefreshSeconds = 0; // This should be loaded from the grid settings (probably the filters)


    protected abstract AbstractListView.ListView getListView();

    public AbstractListPresenter() {
        initDataProvider();
    }

    public boolean isAddingDefaultFilters() {
        return addingDefaultFilters;
    }

    public void setAddingDefaultFilters( boolean addingDefaultFilters ) {
        this.addingDefaultFilters = addingDefaultFilters;
    }

    public void setRefreshTimer(Timer refreshTimer) {
        this.refreshTimer = refreshTimer;
    }

    public Timer getRefreshTimer() {
        return refreshTimer;
    }

    public void setAutoRefreshEnabled(boolean autoRefreshEnabled) {
        this.autoRefreshEnabled = autoRefreshEnabled;
    }

    public boolean isAutoRefreshEnabled() {
        return autoRefreshEnabled;
    }

    protected void updateRefreshTimer() {
        if (refreshTimer == null) {
            refreshTimer = new Timer() {
                public void run() {
                    getData(getListView().getListGrid().getVisibleRange());
                }
            };
        }else{
            refreshTimer.cancel();
        }
        if (autoRefreshEnabled && autoRefreshSeconds > 10) {
            refreshTimer.schedule( autoRefreshSeconds * 1000 );
        }
    }

    public abstract void getData(Range visibleRange);

    public void onGridPreferencesStoreLoaded(){}

    protected void initDataProvider(){

        dataProvider = new AsyncDataProvider<T>() {
            @Override
            protected void onRangeChanged(HasData<T> display) {
                getListView().showBusyIndicator(constants.Loading());
                final Range visibleRange = display.getVisibleRange();
                getData(visibleRange);
            }
        } ;

    }

    public void updateDataOnCallback(PageResponse response){
        getListView().hideBusyIndicator();
        dataProvider.updateRowCount(response.getTotalRowSize(),
                response.isTotalRowSizeExact() );
        dataProvider.updateRowData( response.getStartRowIndex(),
                response.getPageRowList() );
        updateRefreshTimer();
    }

    public void updateDataOnCallback(List<T> instanceSummaries, int startRange, int totalRowCount, boolean isExact){

        getListView().hideBusyIndicator();
        dataProvider.updateRowCount(totalRowCount,
                isExact);
        dataProvider.updateRowData(startRange,
                instanceSummaries);

        updateRefreshTimer();
    }

    public void addDataDisplay( final HasData<T> display ) {
        dataProvider.addDataDisplay(display);
    }

    public AsyncDataProvider<T> getDataProvider(){
        return dataProvider;
    }

    @Override
    public void onRefresh() {
        refreshGrid();
    }

    public void refreshGrid() {
        if(getListView().getListGrid()!=null) {
            getListView().getListGrid().setVisibleRangeAndClearData(getListView().getListGrid().getVisibleRange(), true);
        }
    }

    protected void onSearchEvent(@Observes SearchEvent searchEvent) {
        String filterString = searchEvent.getFilter();
        textSearchStr = filterString == null ? "" : filterString.toLowerCase();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("textSearch", textSearchStr);
        if (currentFilter != null) {
            currentFilter.setParams(params);
        }

        final HasData<T> next = dataProvider.getDataDisplays().iterator().next();
        if (Strings.isNullOrEmpty(filterString)) {
            next.setVisibleRangeAndClearData(next.getVisibleRange(), true);
        } else {
            next.setVisibleRangeAndClearData(new Range(0, next.getVisibleRange().getLength()), true);
        }

    }

    @Override
    public void onRestoreDefaultFilters() {
        getListView().showRestoreDefaultFilterConfirmationPopup();
    }

    @Override
    public void onUpdateRefreshInterval(boolean enableAutoRefresh, int newInterval) {
        this.autoRefreshEnabled = enableAutoRefresh;
        setAutoRefreshSeconds(newInterval);
        updateRefreshTimer();
    }

    protected int getAutoRefreshSeconds(){
        return autoRefreshSeconds;
    }

    protected void setAutoRefreshSeconds(int refreshSeconds){
        autoRefreshSeconds = refreshSeconds;
    }

    @OnClose
    public void onClose() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    public String getTextSearchStr(){
        return textSearchStr;
    }

    protected void setDataProvider(AsyncDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }
}
