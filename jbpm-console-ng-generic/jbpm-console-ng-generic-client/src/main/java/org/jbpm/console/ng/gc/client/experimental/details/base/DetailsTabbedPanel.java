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
package org.jbpm.console.ng.gc.client.experimental.details.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author salaboy
 */
public class DetailsTabbedPanel extends Composite {

    interface Binder
            extends
            UiBinder<Widget, DetailsTabbedPanel> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    public TabLayoutPanel tabsContainer;

    public DetailsTabbedPanel() {
        initWidget( makeWidget() );
    }

    public void addTab( String placeToGo,
                        String label ) {

        HTMLPanel content = new HTMLPanel( "" );
        tabsContainer.add( content, label );
    }

    public Widget getWidget( int index ) {
        return tabsContainer.getWidget( index );
    }

    public int getWidgetCount() {
        return tabsContainer.getWidgetCount();
    }

    public HandlerRegistration addSelectionHandler( SelectionHandler<Integer> handler ) {
        return tabsContainer.addSelectionHandler( handler );
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    public void selectTab( int index ) {
        tabsContainer.selectTab( index, false );
    }

    @Override
    public void setHeight( String height ) {
        tabsContainer.setHeight( height );
    }

    public int getSelectedIndex() {
        return tabsContainer.getSelectedIndex();
    }
}
