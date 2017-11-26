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

import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.dashbuilder.renderer.client.table.TableDisplayer;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.dashboard.renderer.client.panel.AbstractDashboard;
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
    PlaceManager placeManager;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    DisplayerCoordinator displayerCoordinator;

    @Override
    public AbstractDisplayer createNewDisplayer(DisplayerSettings settings) {
        if (settings.getType().equals(DisplayerType.METRIC)) {
            return initDisplayer(new MetricDisplayer(mock(MetricDisplayer.View.class)),
                                 settings);
        }
        if (settings.getType().equals(DisplayerType.TABLE)) {
            FilterLabelSet filterLabelSet = mock(FilterLabelSet.class);
            TableDisplayer.View tableView = mock(TableDisplayer.View.class);
            return initDisplayer(new TableDisplayer(tableView,
                                                    filterLabelSet),
                                 settings);
        }
        return super.createNewDisplayer(settings);
    }

    @Before
    public void init() throws Exception {
        super.init();

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addListener(displayerListener);

        i18n = mock(DashboardI18n.class,
                    new Answer() {
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

    protected void verifyMetricHeaderText(String process,
                                          MetricDisplayer metricDisplayer,
                                          String expected) {
        metricDisplayer.filterApply();
        assertEquals(getPresenter().getSelectedMetric(),
                     metricDisplayer);
        reset(getView());
        getPresenter().changeCurrentProcess(process);
        verify(getView()).setHeaderText(expected);
    }
}