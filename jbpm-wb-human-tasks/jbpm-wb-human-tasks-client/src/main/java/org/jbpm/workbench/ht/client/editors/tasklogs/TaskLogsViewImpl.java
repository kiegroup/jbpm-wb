/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.tasklogs;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.jbpm.workbench.ht.model.TaskEventSummary;


@Dependent
@Templated(value = "TaskLogsViewImpl.html")
public class TaskLogsViewImpl implements TaskLogsPresenter.TaskLogsView {

    @Inject
    @DataField("container")
    private HTMLDivElement container;

    @Inject
    @Bound
    @DataField("logs-list")
    @SuppressWarnings("unused")
    private ListComponent<TaskEventSummary, TaskLogItemView> logs;

    @Inject
    @AutoBound
    private DataBinder<List<TaskEventSummary>> logsList;

    @Inject
    @DataField("load-div")
    private HTMLDivElement loadDiv;

    @Inject
    @DataField("load-more-logs")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLButtonElement loadMoreLogs;

    @Inject
    @DataField("empty-list-item")
    private HTMLDivElement emptyContainer;

    private TaskLogsPresenter presenter;

    @Override
    public void init(final TaskLogsPresenter presenter) {
        this.presenter = presenter;
        logs.addComponentCreationHandler(v -> v.init(presenter));
    }

    @Override
    public void setLogs(final List<TaskEventSummary> taskEventSummaries) {
        if (taskEventSummaries != null && taskEventSummaries.isEmpty()) {
            emptyContainer.hidden = false;
        } else {
            logsList.setModel(taskEventSummaries);
            emptyContainer.hidden = true;
        }
    }

    @Override
    public void hideLoadButton(boolean hidden) {
        loadDiv.hidden = hidden;
    }

    @EventHandler("load-more-logs")
    public void loadMoreProcessInstanceLogs(final @ForEvent("click") MouseEvent event) {
        presenter.loadMoreProcessInstanceLogs();
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }

}
