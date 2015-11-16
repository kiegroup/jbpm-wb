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

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.console.ng.ga.model.QueryFilter;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.paging.PageResponse;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import java.util.HashMap;
import java.util.Map;


/**
 * @param <T> data type for the AsyncDataProvider
 * @author salaboy
 */
public abstract class AbstractListPresenter<T> {

    protected AsyncDataProvider<T> dataProvider;

    protected QueryFilter currentFilter;

    protected String textSearchStr="";

    private Constants constants = GWT.create(Constants.class);

    protected  boolean addingDefaultFilters = false;

    protected Timer refreshTimer = null;
    protected boolean autoRefreshEnabled = true;
    protected int autoRefreshSeconds = 0; // This should be loaded from the grid settings (probably the filters)


    protected Button menuRefreshButton = GWT.create( Button.class );
    protected Button menuResetTabsButton = GWT.create( Button.class );

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
    protected void updateRefreshTimer() {
        if (refreshTimer == null) {
            refreshTimer = new Timer() {
                public void run() {
                    getData(dataProvider.getDataDisplays().iterator().next().getVisibleRange());
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
        dataProvider.updateRowCount(response.getTotalRowSize(),
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

    protected void updateRefreshInterval(boolean enableAutoRefresh, int newInterval){
        this.autoRefreshEnabled = enableAutoRefresh;
        setAutoRefreshSeconds( newInterval );
        updateRefreshTimer();
    }

    protected int getAutoRefreshSeconds(){
        return autoRefreshSeconds;
    }

    protected void setAutoRefreshSeconds(int refreshSeconds){
        autoRefreshSeconds = refreshSeconds;
    }

    @PostConstruct
    public void setupButtons() {
        menuRefreshButton.setIcon( IconType.REFRESH );
        menuRefreshButton.setSize( ButtonSize.SMALL );
        menuRefreshButton.setTitle( Constants.INSTANCE.Refresh() );

        menuResetTabsButton.setIcon( IconType.TH_LIST );
        menuResetTabsButton.setSize( ButtonSize.SMALL );
        menuResetTabsButton.setTitle( Constants.INSTANCE.RestoreDefaultFilters() );
    }

    @OnClose
   public void onClose() {
       if(refreshTimer!=null) {
           refreshTimer.cancel();
       }
   }
}
