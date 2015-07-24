/**
 * Copyright (C) 2015 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel.widgets;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.renderer.client.metric.MetricView;

public class MetricViewExt extends Composite implements MetricView {

    @UiField
    protected FocusPanel centerPanel;

    @UiField
    protected Paragraph titlePanel;

    @UiField
    protected Panel metricPanel;

    @UiField
    protected Heading metricHeading;

    protected DisplayerSettings displayerSettings;

    interface Binder extends UiBinder<Widget, MetricViewExt> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    public MetricViewExt() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void applySettings(DisplayerSettings displayerSettings) {
        this.displayerSettings = displayerSettings;
        int w = displayerSettings.getChartWidth();
        int h = displayerSettings.getChartHeight();
        int mtop = displayerSettings.getChartMarginTop();
        int mbottom = displayerSettings.getChartMarginBottom();
        int mleft = displayerSettings.getChartMarginLeft();
        int mright = displayerSettings.getChartMarginRight();

        // Hero panel (size)
        Style style = centerPanel.getElement().getStyle();
        style.setPadding(0, Style.Unit.PX);
        style.setWidth(w, Style.Unit.PX);
        style.setHeight(h, Style.Unit.PX);
        style.setTextAlign(Style.TextAlign.CENTER);
        style.setVerticalAlign(Style.VerticalAlign.MIDDLE);
        if (!StringUtils.isBlank(displayerSettings.getChartBackgroundColor())) {
            style.setBackgroundColor("#" + displayerSettings.getChartBackgroundColor());
        }

        // Center panel (paddings)
        style = centerPanel.getElement().getStyle();
        style.setPaddingTop(mtop, Style.Unit.PX);
        style.setPaddingBottom(mbottom, Style.Unit.PX);
        style.setPaddingLeft(mleft, Style.Unit.PX);
        style.setPaddingRight(mright, Style.Unit.PX);

        // Title panel
        titlePanel.setVisible(displayerSettings.isTitleVisible());
        titlePanel.setText(displayerSettings.getTitle());
    }

    public void updateMetric(String value) {
        metricHeading.setText(value);
    }

    public void filterOn() {
        Style style = titlePanel.getElement().getStyle();
        style.setColor("#FFFFFF");
        style = metricHeading.getElement().getStyle();
        style.setColor("#FFFFFF");
        style = centerPanel.getElement().getStyle();
        style.setBackgroundColor("blue");
    }

    public void filterOff() {
        Style style = titlePanel.getElement().getStyle();
        style.setColor("black");
        style = metricHeading.getElement().getStyle();
        style.setColor("black");
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        Style style = centerPanel.getElement().getStyle();
        style.setCursor(Style.Cursor.POINTER);
        return centerPanel.addClickHandler(clickHandler);
    }
}
