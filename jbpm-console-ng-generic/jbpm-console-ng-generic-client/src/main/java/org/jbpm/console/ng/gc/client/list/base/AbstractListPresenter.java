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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import org.jbpm.console.ng.ga.model.QueryFilter;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
//import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.paging.PageResponse;

import javax.enterprise.event.Observes;
import java.util.HashMap;
import java.util.Map;


/**
 * @param <T> data type for the AsyncDataProvider
 * @author salaboy
 */
public abstract class AbstractListPresenter<T> {

    protected AsyncDataProvider<T> dataProvider;

   // private FilterPagedTable filterPagedTable;

    protected QueryFilter currentFilter;

    protected String textSearchStr="";

    private Constants constants = GWT.create(Constants.class);

    protected Timer refreshTimer = null;
    protected boolean autoRefreshEnabled = true;
    protected int autoRefreshSeconds = 7; // This should be loaded from the grid settings (probably the filters)

    protected abstract AbstractListView.ListView getListView();

    public AbstractListPresenter() {
        initDataProvider();
    }

    protected void updateRefreshTimer() {
        if (autoRefreshEnabled && autoRefreshSeconds > 0) {
            if (refreshTimer == null) {
                refreshTimer = new Timer() {
                    public void run() {
                        getData(dataProvider.getDataDisplays().iterator().next().getVisibleRange());
                    }
                };
            }
            refreshTimer.schedule(autoRefreshSeconds * 1000);
        }
        else if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    public abstract void getData(Range visibleRange);

    public void onGridPreferencesStoreLoaded(){};

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
        dataProvider.updateRowCount( response.getTotalRowSize(),
                response.isTotalRowSizeExact() );
        dataProvider.updateRowData( response.getStartRowIndex(),
                response.getPageRowList() );
        updateRefreshTimer();
    }

    public void addDataDisplay( final HasData<T> display ) {
        dataProvider.addDataDisplay( display );
    }

    public AsyncDataProvider<T> getDataProvider(){
        return dataProvider;
    }

    public void refreshGrid() {
        if(dataProvider.getDataDisplays().size()>0) {
            HasData<T> next = dataProvider.getDataDisplays().iterator().next();
            next.setVisibleRangeAndClearData( next.getVisibleRange(), true );
        }
    }

    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        String filterString = searchEvent.getFilter();
        if(filterString!=null && filterString.trim().length()>0){
            textSearchStr=filterString.toLowerCase();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put( "textSearch", textSearchStr );
            if ( currentFilter != null ) {
                currentFilter.setParams( params );
            }
        }

        HasData<T> next = dataProvider.getDataDisplays().iterator().next();
        if ( filterString.equals( "" ) ) {
            next.setVisibleRangeAndClearData( next.getVisibleRange(), true );
        } else {
            next.setVisibleRangeAndClearData( new Range( 0, next.getVisibleRange().getLength() ), true );
        }

    }

 /*   protected void onNewItemEvent( @Observes NewItemEvent newItemEvent ) {
        newItemEventAction();
    }

    protected void onRefreshIntervalChangeEvent( @Observes RefreshIntervalEvent refreshIntervalEvent ) {
        int refreshInterval = refreshIntervalEvent.getRefreshInterval();
        if(refreshInterval == 0 ) {
            refreshGrid();
        } else if(refreshInterval == -1){
            updateRefreshInterval( false, -1 );
        } else{
            updateRefreshInterval( true, refreshInterval );
        }
    }
 */
    protected void updateRefreshInterval(boolean enableAutoRefresh, int newInterval){
        this.autoRefreshEnabled = enableAutoRefresh;
        setAutoRefreshSeconds( newInterval );
        updateRefreshTimer();
    }

    protected int getAutoRefreshSeconds(){
     //   autoRefreshSeconds = filterPagedTable.getMultiGridPreferencesStore().getRefreshInterval();
        return autoRefreshSeconds;
    }

    protected void setAutoRefreshSeconds(int refreshSeconds){
    //    filterPagedTable.saveNewRefreshInterval(refreshSeconds);
        autoRefreshSeconds = refreshSeconds;
    }
   /* public FilterPagedTable getFilterPagedTable() {
        return filterPagedTable;
    }

    public void setFilterPagedTable( FilterPagedTable filterPagedTable ) {
        this.filterPagedTable = filterPagedTable;
    }*/

}
