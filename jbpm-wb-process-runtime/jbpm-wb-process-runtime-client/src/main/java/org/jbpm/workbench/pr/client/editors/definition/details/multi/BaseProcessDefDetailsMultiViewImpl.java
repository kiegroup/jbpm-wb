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

package org.jbpm.workbench.pr.client.editors.definition.details.multi;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.workbench.pr.client.i18n.Constants;

public abstract class BaseProcessDefDetailsMultiViewImpl extends Composite
        implements BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

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

    protected abstract void closeDetails();

    protected abstract void createNewProcessInstance();
}
