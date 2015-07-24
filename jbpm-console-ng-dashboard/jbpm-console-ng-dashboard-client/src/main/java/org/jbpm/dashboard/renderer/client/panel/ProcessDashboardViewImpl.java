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
package org.jbpm.dashboard.renderer.client.panel;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.Displayer;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.jbpm.dashboard.renderer.client.panel.widgets.DisplayerContainer;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
public class ProcessDashboardViewImpl extends Composite implements ProcessDashboardView {

    interface Binder extends UiBinder<Widget, ProcessDashboardViewImpl> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Label headerLabel;

    @UiField
    Panel dashboardPanel;

    @UiField
    Panel instancesPanel;

    @UiField
    Anchor instancesAnchor;

    @UiField
    FluidRow processBreadCrumbRow;

    @UiField
    ProcessBreadCrumb processBreadCrumb;

    @UiField(provided = true)
    DisplayerContainer totalContainer;

    @UiField(provided = true)
    DisplayerContainer activeContainer;

    @UiField(provided = true)
    DisplayerContainer pendingContainer;

    @UiField(provided = true)
    DisplayerContainer suspendedContainer;

    @UiField(provided = true)
    DisplayerContainer abortedContainer;

    @UiField(provided = true)
    DisplayerContainer completedContainer;

    @UiField(provided = true)
    DisplayerContainer container1;

    @UiField(provided = true)
    DisplayerContainer container2;

    @UiField(provided = true)
    DisplayerContainer container3;

    @UiField(provided = true)
    DisplayerContainer container4;

    @UiField(provided = true)
    DisplayerContainer container5;

    @UiField(provided = true)
    DisplayerContainer container6;

    @UiField(provided = true)
    Displayer processesTable;

    ProcessDashboardPresenter presenter;

    public void init(ProcessDashboardPresenter presenter,
            Displayer totalMetric,
            Displayer activeMetric,
            Displayer pendingMetric,
            Displayer suspendedMetric,
            Displayer abortedMetric,
            Displayer completedMetric,
            Displayer processesByType,
            Displayer processesByUser,
            Displayer processesByStartDate,
            Displayer processesByEndDate,
            Displayer processesByRunningTime,
            Displayer processesByVersion,
            Displayer processesTable) {

        this.presenter = presenter;
        this.processesTable = processesTable;

        Map<String,Displayer> dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.total(), totalMetric);
        totalContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.processStatusActive(), activeMetric);
        activeContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.processStatusPending(), pendingMetric);
        pendingContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.processStatusSuspended(), suspendedMetric);
        suspendedContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.processStatusAborted(), abortedMetric);
        abortedContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.processStatusCompleted(), completedMetric);
        completedContainer = createMetricContainer(dmap, false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byType(), processesByType);
        container1 = createChartContainer(dmap, true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byStartDate(), processesByStartDate);
        container2 = createChartContainer(dmap, true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byUser(), processesByUser);
        container3 = createChartContainer(dmap, true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byRunningTime(), processesByRunningTime);
        container4 = createChartContainer(dmap, true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byEndDate(), processesByEndDate);
        container5 = createChartContainer(dmap, true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byVersion(), processesByVersion);
        container6 = createChartContainer(dmap, true);

        initWidget(uiBinder.createAndBindUi(this));

        processBreadCrumb.addListener(presenter);
    }

    protected DisplayerContainer createMetricContainer(Map<String,Displayer> m, boolean showHeader) {
        DisplayerContainer container = new DisplayerContainer(m, showHeader);
        Style s = container.getView().getHeaderStyle();
        s.setBackgroundColor("white");
        return container;
    }

    protected DisplayerContainer createChartContainer(Map<String,Displayer> m, boolean showHeader) {
        DisplayerContainer container = new DisplayerContainer(m, showHeader);
        Style s = container.getView().getHeaderStyle();
        s.setBackgroundColor("white");
        s = container.getView().getBodyStyle();
        s.setBackgroundColor("white");
        s.setPaddingBottom(30, Style.Unit.PX);
        return container;
    }

    @Override
    public void showLoading() {
        BusyPopup.showMessage(DashboardConstants.INSTANCE.loadingDashboard());
    }

    @Override
    public void hideLoading() {
        BusyPopup.close();
    }

    @Override
    public void setHeaderText(String text) {
        headerLabel.setText(text);
    }

    @Override
    public void showBreadCrumb(String processName) {
        processBreadCrumbRow.setVisible(true);
        processBreadCrumb.setProcessName(processName);
    }

    @Override
    public void hideBreadCrumb() {
        processBreadCrumbRow.setVisible(false);
    }

    @UiHandler("instancesAnchor")
    protected void onShowInstances(ClickEvent event) {
        if (dashboardPanel.isVisible()) {
            instancesAnchor.setText(DashboardConstants.INSTANCE.showDashboard());
            dashboardPanel.setVisible(false);
            instancesPanel.setVisible(true);
            presenter.showProcessesTable();

        } else {
            instancesAnchor.setText(DashboardConstants.INSTANCE.showInstances());
            dashboardPanel.setVisible(true);
            instancesPanel.setVisible(false);
            presenter.showDashboard();
        }
    }
}
