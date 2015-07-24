/**
 * Copyright (C) 2014 JBoss Inc
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

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.dashbuilder.renderer.client.table.TableDisplayer;
import org.jbpm.dashboard.renderer.client.panel.ProcessDashboardPresenter;

public class ProcessTableDisplayer extends TableDisplayer {

    protected ProcessDashboardPresenter presenter;

    public ProcessTableDisplayer(DisplayerSettings settings, ProcessDashboardPresenter presenter) {
        super();
        this.presenter = presenter;
        setDisplayerSettings(settings);
        setDataSetHandler(new DataSetHandlerImpl(settings.getDataSetLookup()));
    }

    @Override
    protected void onCellSelected(String columnId, boolean selectable, int rowIndex) {
        presenter.tableCellSelected(columnId, rowIndex);
        super.onCellSelected(columnId, selectable, rowIndex);
    }
}
