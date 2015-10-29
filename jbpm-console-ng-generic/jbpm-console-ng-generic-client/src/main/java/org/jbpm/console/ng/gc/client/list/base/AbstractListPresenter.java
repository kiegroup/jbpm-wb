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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import org.jbpm.console.ng.ga.model.QueryFilter;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.uberfire.lifecycle.OnClose;
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

    protected QueryFilter currentFilter;

    protected String textSearchStr="";

    private Constants constants = GWT.create(Constants.class);

    protected  boolean addingDefaultFilters = false;

    protected Timer refreshTimer = null;
    protected boolean autoRefreshEnabled = true;
    protected int autoRefreshSeconds = 60; // This should be loaded from the grid settings (probably the filters)

    public Button menuActionsButton;
    private PopupPanel popup = new PopupPanel(true);

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
            refreshTimer.schedule(autoRefreshSeconds * 1000);
        }
    }

    public abstract void getData(Range visibleRange);

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

    protected void updateRefreshInterval(boolean enableAutoRefresh, int newInterval){
        this.autoRefreshEnabled = enableAutoRefresh;
        setAutoRefreshSeconds( newInterval);
        updateRefreshTimer();
    }

    protected int getAutoRefreshSeconds(){
        return autoRefreshSeconds;
    }

    protected void setAutoRefreshSeconds(int refreshSeconds){
        autoRefreshSeconds = refreshSeconds;
    }

    public void createRefreshToggleButton(final Button refreshIntervalSelector) {

        refreshIntervalSelector.setToggle( true );
        refreshIntervalSelector.setIcon( IconType.COG );
        refreshIntervalSelector.setTitle( Constants.INSTANCE.AutoRefresh() );
        refreshIntervalSelector.setSize( ButtonSize.MINI );

        popup.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        popup.addAutoHidePartner( refreshIntervalSelector.getElement() );
        popup.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
                if ( popupPanelCloseEvent.isAutoClosed() ) {
                    refreshIntervalSelector.setActive( false );
                }
            }
        } );

        refreshIntervalSelector.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( !refreshIntervalSelector.isActive() ) {
                    showSelectRefreshIntervalPopup( refreshIntervalSelector.getAbsoluteLeft() + refreshIntervalSelector.getOffsetWidth(),
                            refreshIntervalSelector.getAbsoluteTop() + refreshIntervalSelector.getOffsetHeight(), refreshIntervalSelector );
                } else {
                    popup.hide( false );
                }
            }
        } );

    }

    private void showSelectRefreshIntervalPopup(final int left,
                                                final int top,
                                                final Button refreshIntervalSelector) {
        VerticalPanel popupContent = new VerticalPanel();

        final Button resetButton = new Button( Constants.INSTANCE.DisableAutorefresh() );
        //int configuredSeconds = presenter.getAutoRefreshSeconds();
        int configuredSeconds = getRefreshValue();

        if(configuredSeconds>10) {
            updateRefreshInterval( true, configuredSeconds );
            resetButton.setEnabled( true );
            resetButton.setActive( false );
            resetButton.setText( Constants.INSTANCE.DisableAutorefresh() );
        } else {
            updateRefreshInterval( false, 0 );
            resetButton.setEnabled( false );
            resetButton.setActive( true );
            resetButton.setText( Constants.INSTANCE.AutorefreshDisabled() );
        }

        RadioButton oneMinuteRadioButton = createTimeSelectorRadioButton(60, "1 "+ Constants.INSTANCE.Minute(), configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton fiveMinuteRadioButton = createTimeSelectorRadioButton(300, "5 "+ Constants.INSTANCE.Minutes(), configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton tenMinuteRadioButton = createTimeSelectorRadioButton(600, "10 "+ Constants.INSTANCE.Minutes(), configuredSeconds, refreshIntervalSelector, popupContent);

        popupContent.add(oneMinuteRadioButton);
        popupContent.add( fiveMinuteRadioButton );
        popupContent.add( tenMinuteRadioButton );

        resetButton.setSize( ButtonSize.MINI );
        resetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                updateRefreshInterval( false, 0 );
                saveRefreshValue( 0 );
                resetButton.setEnabled( false );
                resetButton.setActive( true );
                resetButton.setText( Constants.INSTANCE.AutorefreshDisabled() );
                popup.hide();
            }
        } );

        popupContent.add( resetButton );


        popup.setWidget( popupContent );
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition( finalLeft, top );

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
                updateRefreshInterval( true, selectedRefreshTime );
                saveRefreshValue( selectedRefreshTime );
                refreshIntervalSelector.setActive( false );
                popup.hide();

            }
        } );
        return oneMinuteRadioButton;
    }

    protected void saveRefreshValue(int newValue){
    }
    protected int getRefreshValue(){
        return 0;
    }

    @OnClose
    public void onClose() {
        if(refreshTimer!=null) {
            refreshTimer.cancel();
        }
    }

}
