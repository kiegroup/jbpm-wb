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

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.Displayer;

public interface TaskDashboardView extends IsWidget {

    void init(TaskDashboardPresenter presenter,
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
            Displayer tasksTable);

    void showBreadCrumb(String processName);
    void hideBreadCrumb();
    void setHeaderText(String text);
    void showLoading();
    void hideLoading();
}