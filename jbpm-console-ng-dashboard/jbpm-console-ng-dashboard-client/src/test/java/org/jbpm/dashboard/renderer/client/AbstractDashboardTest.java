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
package org.jbpm.dashboard.renderer.client;

import java.util.Arrays;

import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.dashbuilder.renderer.client.table.TableDisplayer;
import org.jbpm.dashboard.renderer.client.panel.AbstractDashboard;
import org.jbpm.dashboard.renderer.client.panel.DashboardFactory;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardI18n;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public abstract class AbstractDashboardTest extends AbstractDisplayerTest {

    DashboardI18n i18n;

    @Mock
    DisplayerListener displayerListener;

    @Mock
    DashboardFactory dashboardFactory;

    @Mock
    PlaceManager placeManager;

    DisplayerCoordinator displayerCoordinator;

    @Before
    public void init() throws Exception {
        super.init();

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addListener(displayerListener);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return initDisplayer(new MetricDisplayer(mock(MetricDisplayer.View.class)), null);
            }
        }).when(dashboardFactory).createMetricDisplayer();

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return initDisplayer(new TableDisplayer(mock(TableDisplayer.View.class)), null);
            }
        }).when(dashboardFactory).createTableDisplayer();

        i18n = mock(DashboardI18n.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getMethod().getName() +
                        Arrays.deepToString(invocation.getArguments());
            }
        });

        when(getView().getI18nService()).thenReturn(i18n);

        registerDataset();
    }

    protected abstract void registerDataset() throws Exception;

    protected abstract AbstractDashboard.View getView();

    protected abstract AbstractDashboard getPresenter();

    protected void verifyMetricHeaderText(String process, MetricDisplayer metricDisplayer, String expected) {
        metricDisplayer.filterApply();
        assertEquals(getPresenter().getSelectedMetric(), metricDisplayer);
        reset(getView());
        getPresenter().changeCurrentProcess(process);
        verify(getView()).setHeaderText(expected);
    }
}