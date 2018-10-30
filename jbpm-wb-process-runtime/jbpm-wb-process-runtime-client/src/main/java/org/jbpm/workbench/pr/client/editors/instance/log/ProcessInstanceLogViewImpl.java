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

package org.jbpm.workbench.pr.client.editors.instance.log;


import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ActiveFilters;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ListTable;

import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated(value = "ProcessInstanceLogViewImpl.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class ProcessInstanceLogViewImpl extends AbstractMultiGridView<ProcessInstanceLogSummary, ProcessInstanceLogPresenter>
        implements ProcessInstanceLogPresenter.ProcessInstanceLogView {

    @Inject
    @DataField("logFilters")
    private Div logFilters;

    @Inject
    @DataField("active-filters")
    protected ActiveFilters filtersLogs;

    @Inject
    @DataField("load-div")
    private Div loadDiv;

    @Inject
    @DataField("load-more-logs")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private Button loadMoreLogs;

    @Inject
    @DataField("reset-filters")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private Button resetFilters;

    @Inject
    @Bound
    @DataField("logs-list")
    @SuppressWarnings("unused")
    private ListComponent<ProcessInstanceLogSummary, ProcessInstanceLogItemView> logs;

    @Inject
    @AutoBound
    private DataBinder<List<ProcessInstanceLogSummary>> logsList;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Override
    public void init(final ProcessInstanceLogPresenter presenter) {
        super.init(presenter);
        logs.addComponentCreationHandler(v -> v.init(presenter));
        logFilters.appendChild(presenter.getBasicFiltersView());
    }

    @Override
    public void setLogsList(final List<ProcessInstanceLogSummary> processInstanceLogSummaries) {
        logsList.setModel(processInstanceLogSummaries);
        if (processInstanceLogSummaries.isEmpty()) {
            removeCSSClass(emptyContainer,
                           "hidden");
        } else {
            addCSSClass(emptyContainer,
                        "hidden");
        }
    }

    @Override
    public void hideLoadButton(boolean hidden) {
        loadDiv.setHidden(hidden);
    }

    @Override
    public void initColumns(ListTable<ProcessInstanceLogSummary> extendedPagedTable) {

    }

    @Override
    public String getEmptyTableCaption() {
        return null;
    }

    @Override
    public List<String> getInitColumns() {
        return null;
    }

    @Override
    public List<String> getBannedColumns() {
        return null;
    }

    @Override
    protected List<ConditionalAction<ProcessInstanceLogSummary>> getConditionalActions() {
        return null;
    }

    @Override
    public void setSaveFilterCallback(final BiConsumer<String, Consumer<String>> filterNameCallback) {
        filtersLogs.setSaveFilterCallback(filterNameCallback);
    }

    @Override
    public <T extends Object> void addActiveFilter(final ActiveFilterItem<T> filter) {
        filtersLogs.addActiveFilter(filter);
    }

    @Override
    public <T extends Object> void removeActiveFilter(final ActiveFilterItem<T> filter) {
        filtersLogs.removeActiveFilter(filter);
    }

    @Override
    public void removeAllActiveFilters() {
        filtersLogs.removeAllActiveFilters();
    }

    @EventHandler("load-more-logs")
    public void loadMoreProcessInstanceLogs(final @ForEvent("click") MouseEvent event) {
        presenter.loadMoreProcessInstanceLogs();
    }

    @EventHandler("reset-filters")
    public void resetFilters(final @ForEvent("click") MouseEvent event) {
        presenter.setupDefaultActiveSearchFilters();
    }
}