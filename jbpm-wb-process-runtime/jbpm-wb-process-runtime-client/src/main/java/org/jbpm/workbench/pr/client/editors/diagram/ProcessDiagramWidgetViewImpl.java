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

package org.jbpm.workbench.pr.client.editors.diagram;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.D3;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
@Templated
public class ProcessDiagramWidgetViewImpl extends Composite implements ProcessDiagramWidgetView,
                                                                       RequiresResize {

    @Inject
    @DataField("processDiagramDiv")
    private HTMLDivElement processDiagramDiv;

    @Inject
    @DataField("message")
    @Named("span")
    private HTMLElement heading;

    @Inject
    @DataField
    private HTMLDivElement alert;

    @Inject
    private ZoomControlView zoomControlView;

    public void displayImage(final String svgContent) {

        processDiagramDiv.innerHTML = svgContent;

        final D3 d3 = D3.Builder.get();
        final D3 svg = d3.select("#processDiagramDiv svg");
        D3.CallbackFunction callback = () -> {
            D3.ZoomEvent event = D3.Builder.get().getEvent();
            svg.attr("transform",
                     event.getTransform());
            double zoom = Math.round(100 + (event.getTransform().getK() - 1) * 100);
            zoomControlView.setZoomText(zoom + "%");
        };
        final D3.Zoom zoom = d3.zoom();
        svg.call(zoom.on("zoom",
                         callback));

        zoomControlView.setScaleTo100Command(() -> {
            zoom.transform(svg.transition().duration(500),
                           d3.getZoomIdentity());
        });

        zoomControlView.setScaleTo300Command(() -> {
            zoom.scaleTo(svg.transition().duration(500),
                         3.0);
        });

        zoomControlView.setScaleTo150Command(() -> {
            zoom.scaleTo(svg.transition().duration(500),
                         1.5);
        });

        zoomControlView.setScaleTo50Command(() -> {
            zoom.scaleTo(svg.transition().duration(500),
                         0.5);
        });

        zoomControlView.setScaleMinusCommand(() -> {
            zoom.scaleBy(svg.transition().duration(500),
                         0.95);
        });

        zoomControlView.setScalePlusCommand(() -> {
            zoom.scaleBy(svg.transition().duration(500),
                         1.05);
        });

        processDiagramDiv.appendChild(zoomControlView.getElement());
    }

    public void displayMessage(final String message) {
        alert.classList.remove("hidden");
        heading.textContent = message;
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();

        setPixelSize(width,
                     height);
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}