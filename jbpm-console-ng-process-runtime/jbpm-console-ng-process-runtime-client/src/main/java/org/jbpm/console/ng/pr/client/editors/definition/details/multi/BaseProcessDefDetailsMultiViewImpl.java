/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.console.ng.pr.client.i18n.Constants;

public abstract class BaseProcessDefDetailsMultiViewImpl extends Composite
        implements BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

    @Override
    public Button getCloseButton() {
        return new Button() {{
            setIcon( IconType.REMOVE );
            setTitle( Constants.INSTANCE.Close() );
            setSize( ButtonSize.SMALL );
            addClickHandler( new ClickHandler() {

                @Override
                public void onClick( ClickEvent event ) {
                    closeDetails();
                }
            } );
        }};
    }

    @Override
    public IsWidget getRefreshButton() {
        return new Button() {{
            setIcon( IconType.REFRESH );
            setTitle( Constants.INSTANCE.Refresh() );
            setSize( ButtonSize.SMALL );
            addClickHandler( new ClickHandler() {

                @Override
                public void onClick( ClickEvent event ) {
                    refresh();
                }
            } );
        }};
    }

    @Override
    public IsWidget getNewInstanceButton() {
        return new Button() {{
            setSize( ButtonSize.SMALL );
            setIcon( IconType.PLAY );
            setText( Constants.INSTANCE.New_Instance() );
            addClickHandler( new ClickHandler() {

                @Override
                public void onClick( ClickEvent event ) {
                    createNewProcessInstance();
                }
            } );
        }};
    }

    protected abstract IsWidget getTabView();

    protected abstract void refresh();

    protected abstract void closeDetails();

    protected abstract void createNewProcessInstance();
}
