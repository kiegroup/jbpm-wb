/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.Displayer;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardI18n;
import org.jbpm.dashboard.renderer.client.panel.widgets.DisplayerContainer;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
public class TaskDashboardView extends Composite implements TaskDashboard.View {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Heading headerLabel;

    @UiField
    Container dashboardPanel;

    @UiField
    Panel instancesPanel;

    @UiField
    Row processBreadCrumbRow;

    @UiField(provided = true)
    ProcessBreadCrumb processBreadCrumb;

    @UiField(provided = true)
    DisplayerContainer header1;

    @UiField(provided = true)
    DisplayerContainer header2;

    @UiField(provided = true)
    DisplayerContainer header3;

    @UiField(provided = true)
    DisplayerContainer header4;

    @UiField(provided = true)
    DisplayerContainer header5;

    @UiField(provided = true)
    DisplayerContainer header6;

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
    Displayer tasksTable;

    TaskDashboard presenter;

    @Override
    public void init(TaskDashboard presenter,
                     Displayer totalMetric,
                     Displayer createdMetric,
                     Displayer readyMetric,
                     Displayer reservedMetric,
                     Displayer inProgressMetric,
                     Displayer suspendedMetric,
                     Displayer completedMetric,
                     Displayer failedMetric,
                     Displayer errorMetric,
                     Displayer exitedMetric,
                     Displayer obsoleteMetric,
                     Displayer tasksByProcess,
                     Displayer tasksByOwner,
                     Displayer tasksByCreationDate,
                     Displayer tasksByEndDate,
                     Displayer tasksByRunningTime,
                     Displayer tasksByStatus,
                     Displayer tasksTable) {

        this.presenter = presenter;
        this.tasksTable = tasksTable;
        this.processBreadCrumb = presenter.getProcessBreadCrumb();

        Map<String, Displayer> dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.total(),
                 totalMetric);
        header1 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.taskStatusReady(),
                 readyMetric);
        header2 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.taskStatusReserved(),
                 reservedMetric);
        header3 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.taskStatusInProgress(),
                 inProgressMetric);
        header4 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.taskStatusSuspended(),
                 suspendedMetric);
        header5 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.taskStatusCompleted(),
                 completedMetric);
        header6 = createMetricContainer(dmap,
                                        false);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byProcess(),
                 tasksByProcess);
        container1 = createChartContainer(dmap,
                                          true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byCreationDate(),
                 tasksByCreationDate);
        container2 = createChartContainer(dmap,
                                          true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byUser(),
                 tasksByOwner);
        container3 = createChartContainer(dmap,
                                          true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byRunningTime(),
                 tasksByRunningTime);
        container4 = createChartContainer(dmap,
                                          true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byEndDate(),
                 tasksByEndDate);
        container5 = createChartContainer(dmap,
                                          true);

        dmap = new HashMap<String, Displayer>();
        dmap.put(DashboardConstants.INSTANCE.byStatus(),
                 tasksByStatus);
        container6 = createChartContainer(dmap,
                                          true);

        initWidget(uiBinder.createAndBindUi(this));
    }

    protected DisplayerContainer createMetricContainer(Map<String, Displayer> m,
                                                       boolean showHeader) {
        DisplayerContainer container = new DisplayerContainer(m,
                                                              showHeader);
        Style s = container.getView().getHeaderStyle();
        s.setBackgroundColor("white");
        return container;
    }

    protected DisplayerContainer createChartContainer(Map<String, Displayer> m,
                                                      boolean showHeader) {
        DisplayerContainer container = new DisplayerContainer(m,
                                                              showHeader);
        Style s = container.getView().getHeaderStyle();
        s.setBackgroundColor("white");
        s = container.getView().getBodyStyle();
        s.setBackgroundColor("white");
        s.setPaddingBottom(30,
                           Style.Unit.PX);
        return container;
    }

    @Override
    public DashboardI18n getI18nService() {
        return DashboardConstants.INSTANCE;
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
        processBreadCrumb.setRootTitle(DashboardConstants.INSTANCE.tasks());
        processBreadCrumb.setProcessName(processName);
    }

    @Override
    public void hideBreadCrumb() {
        processBreadCrumbRow.setVisible(false);
    }

    @Override
    public void showDashboard() {
        dashboardPanel.setVisible(true);
        instancesPanel.setVisible(false);
    }

    @Override
    public void showInstances() {
        dashboardPanel.setVisible(false);
        instancesPanel.setVisible(true);
    }

    interface Binder extends UiBinder<Widget, TaskDashboardView> {

    }
}
