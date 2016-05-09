/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.pr.client.editors.diagram;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
@Templated
public class ProcessDiagramWidgetView
        extends Composite
        implements ProcessDiagramPopUpPresenter.View,
        RequiresResize {

    @DataField("processDiagramDiv")
    Element processDiagramDiv = DOM.createDiv();

    public ProcessDiagramWidgetView() {

    }

    public void displayImage(final String svgContent) {
        if (svgContent != null && !svgContent.isEmpty()) {
            processDiagramDiv.setInnerHTML(svgContent);
            processDiagramDiv.getFirstChildElement().setAttribute("overflow", "scroll");
        } else {
            processDiagramDiv.setInnerHTML("<h3>No process diagram found</h3>");
        }
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();

        setPixelSize(width,height);

    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}