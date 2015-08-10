/**
 * Copyright (C) 2015 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.dashboard.renderer.client.panel.widgets;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.bundles.DataSetClientResources;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.widgets.DisplayerError;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;

public class DisplayerContainerView extends Composite
        implements DisplayerContainer.View {

    interface Binder extends UiBinder<Widget, DisplayerContainerView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    protected Panel rootPanel;

    @UiField
    protected Panel headerPanel;

    @UiField
    protected Panel separatorPanel;

    @UiField
    protected Label titleLabel;

    @UiField
    protected ListBox displayerList;

    @UiField
    protected Panel displayerPanel;

    protected Displayer currentDisplayer;
    protected DisplayerContainer presenter;
    protected DisplayerError errorWidget = new DisplayerError();

    public DisplayerContainerView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( DisplayerContainer presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setHeaderVisible( boolean visible ) {
        headerPanel.setVisible( visible );
        separatorPanel.setVisible( visible );
    }

    @Override
    public void setHeaderText( String text ) {
        titleLabel.setText( text );
    }

    @Override
    public void setSelectorVisible( boolean visible ) {
        displayerList.setVisible( visible );
    }

    @Override
    public void setDisplayerList( Set<String> displayerNames ) {
        displayerList.clear();
        for ( String name : displayerNames ) {
            displayerList.addItem( name );
        }
    }

    @Override
    public void setDisplayerHeight( int h ) {
        displayerPanel.setHeight( h + "px" );
    }

    @Override
    public void showLoading( Displayer displayer ) {
        currentDisplayer = null;
        VerticalPanel centeredPanel = new VerticalPanel();
        centeredPanel.setWidth( "100%" );
        centeredPanel.setHeight( "100%" );
        centeredPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
        centeredPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

        Image loadingIcon = new Image( DataSetClientResources.INSTANCE.images().loadingIcon().getSafeUri() );
        loadingIcon.setTitle( DashboardConstants.INSTANCE.loading() );
        loadingIcon.setAltText( DashboardConstants.INSTANCE.loading() );
        centeredPanel.add( loadingIcon );

        displayerPanel.clear();
        displayerPanel.add( centeredPanel );
    }

    @Override
    public void showDisplayer( Displayer displayer ) {
        if ( displayer != currentDisplayer ) {
            displayerPanel.clear();
            displayerPanel.add( displayer );
            currentDisplayer = displayer;
        }
    }

    @Override
    public void showEmpty( Displayer displayer ) {
        currentDisplayer = null;
        VerticalPanel centeredPanel = new VerticalPanel();
        centeredPanel.setWidth( "100%" );
        centeredPanel.setHeight( "100%" );
        centeredPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
        centeredPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

        Label noDataLabel = new Label( DashboardConstants.INSTANCE.noData() );
        centeredPanel.add( noDataLabel );

        displayerPanel.clear();
        displayerPanel.add( centeredPanel );
    }

    @Override
    public void showError( String message,
                           String cause ) {
        currentDisplayer = null;
        displayerPanel.clear();
        displayerPanel.add( errorWidget );
        errorWidget.show( message, cause );
    }

    @UiHandler("displayerList")
    protected void onDisplayerSelected( ChangeEvent event ) {
        presenter.selectDisplayer( displayerList.getSelectedValue() );
    }

    @Override
    public Style getHeaderStyle() {
        return headerPanel.getElement().getStyle();
    }

    @Override
    public Style getBodyStyle() {
        return displayerPanel.getElement().getStyle();
    }
}
