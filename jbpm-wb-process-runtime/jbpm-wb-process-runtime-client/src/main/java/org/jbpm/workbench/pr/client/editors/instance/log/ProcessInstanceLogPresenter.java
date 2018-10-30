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

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jbpm.workbench.common.client.filters.active.ClearAllActiveFiltersEvent;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;

import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;

import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_NODE_TYPE;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.COLUMN_LOG_TYPE;

@Dependent
public class ProcessInstanceLogPresenter extends AbstractMultiGridPresenter<ProcessInstanceLogSummary, ProcessInstanceLogPresenter.ProcessInstanceLogView> {

    public static final int PAGE_SIZE = 10;
    private Constants constants = Constants.INSTANCE;

    @Inject
    private ProcessInstanceLogView view;

    private Caller<TaskService> taskService;

    private String serverTemplateId;
    private String containerId;
    int currentPage = 0;

    List<ProcessInstanceLogSummary> visibleLogs = new ArrayList<ProcessInstanceLogSummary>();

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public void setCurrentPage(int i) {
        this.currentPage = i;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    protected DataSetQueryHelper logsDataSetQueryHelper;

    protected ProcessInstanceLogFilterSettingsManager filterSettingsManager;

    private ProcessInstanceLogBasicFiltersPresenter processInstanceLogBasicFiltersPresenter;

    @Inject
    public void setDataSetQueryHelper(final DataSetQueryHelper dataSetQueryHelper) {
        this.logsDataSetQueryHelper = dataSetQueryHelper;
    }

    @Inject
    public void setProcessInstanceLogBasicFiltersPresenter(final ProcessInstanceLogBasicFiltersPresenter pILBasicFiltersPresenter) {
        this.processInstanceLogBasicFiltersPresenter = pILBasicFiltersPresenter;
    }

    @Inject
    public void setFilterSettingsManager(final ProcessInstanceLogFilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getWidget() {
        return view;
    }

    public HTMLElement getBasicFiltersView() {
        return processInstanceLogBasicFiltersPresenter.getView().getElement();
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public DataSetQueryHelper getDataSetQueryHelper() {
        return logsDataSetQueryHelper;
    }

    public void loadProcessInstanceLogs() {
        try {
            final FilterSettings currentTableSettings = logsDataSetQueryHelper.getCurrentTableSettings();
            currentTableSettings.setServerTemplateId(this.serverTemplateId);
            currentTableSettings.setTablePageSize(getPageSize());
            logsDataSetQueryHelper.setCurrentTableSettings(currentTableSettings);
            logsDataSetQueryHelper.setDataSetHandler(currentTableSettings);
            logsDataSetQueryHelper.lookupDataSet(
                    currentPage * getPageSize(),
                    new DataSetReadyCallback() {
                        @Override
                        public void callback(DataSet dataSet) {
                            if (dataSet != null && logsDataSetQueryHelper.getCurrentTableSettings().getKey().equals(currentTableSettings.getKey())) {
                                List<ProcessInstanceLogSummary> logs = new ArrayList<ProcessInstanceLogSummary>();
                                for (int i = 0; i < dataSet.getRowCount(); i++) {
                                    logs.add(new ProcessInstanceLogSummaryDataSetMapper().apply(dataSet,
                                                                                                i));
                                }
                                if (currentPage == 0) {
                                    visibleLogs = new ArrayList();
                                }
                                visibleLogs.addAll(logs);
                                view.hideLoadButton(logs.size() < PAGE_SIZE);
                                view.setLogsList(visibleLogs.stream().collect(Collectors.toList()));
                            }
                        }

                        @Override
                        public void notFound() {
                            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetNotFound(currentTableSettings.getDataSet().getUUID()));
                        }

                        @Override
                        public boolean onError(ClientRuntimeError error) {
                            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.DataSetError(currentTableSettings.getDataSet().getUUID(),
                                                                                                                                   error.getMessage()));
                            return false;
                        }
                    });
        } catch (Exception e) {
            errorPopup.showMessage(org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.UnexpectedError(e.getMessage()));
        }
    }

    public void loadMoreProcessInstanceLogs() {
        setCurrentPage(currentPage + 1);
        loadProcessInstanceLogs();
    }

    public void loadTaskDetails(Long workItemId,
                                final Date logDate,
                                ProcessInstanceLogHumanTaskView humanTaskView) {
        taskService.call(
                (TaskSummary task) -> {
                    humanTaskView.setDetailsData(task,
                                                 logDate);
                }).getTaskByWorkItemId(serverTemplateId,
                                       containerId,
                                       workItemId);
    }

    public void onProcessInstanceSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        this.serverTemplateId = event.getServerTemplateId();
        this.containerId = event.getDeploymentId();
        if (logsDataSetQueryHelper.getCurrentTableSettings() == null) {
            logsDataSetQueryHelper.setCurrentTableSettings(
                    filterSettingsManager.createDefaultFilterSettingsPrototype(event.getProcessInstanceId()));
            setupDefaultActiveSearchFilters();
        } else {
            refreshGrid();
        }
    }

    public void refreshGrid() {
        currentPage = 0;
        loadProcessInstanceLogs();
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        processInstanceLogBasicFiltersPresenter.onClearAllActiveFiltersEvent(new ClearAllActiveFiltersEvent());
        final List<String> types = Arrays.asList(LogUtils.NODE_TYPE_START,
                                                 LogUtils.NODE_TYPE_END,
                                                 LogUtils.NODE_TYPE_HUMAN_TASK,
                                                 LogUtils.NODE_TYPE_ACTION,
                                                 LogUtils.NODE_TYPE_MILESTONE,
                                                 LogUtils.NODE_TYPE_SUBPROCESS,
                                                 LogUtils.NODE_TYPE_RULE_SET,
                                                 LogUtils.NODE_TYPE_WORK_ITEM);
        final List<String> nodeTypeLabels = Arrays.asList(constants.StartNodes(),
                                                          constants.EndNodes(),
                                                          constants.Human_Tasks(),
                                                          constants.ActionNodes(),
                                                          constants.Milestones(),
                                                          constants.SubProcesses(),
                                                          constants.RuleSets(),
                                                          constants.WorkItems());
        addActiveFilter(in(COLUMN_LOG_NODE_TYPE,
                           types),
                        constants.EventNodeType(),
                        String.join(", ",
                                    nodeTypeLabels),
                        types,
                        v -> removeActiveFilter(in(COLUMN_LOG_NODE_TYPE,
                                                   types))
        );

        addActiveFilter(in(COLUMN_LOG_TYPE,
                           Collections.emptyList()),
                        constants.EventType(),
                        "",
                        Collections.emptyList(),
                        v -> removeActiveFilter(in(COLUMN_LOG_TYPE,
                                                   Collections.emptyList())));
    }

    @Override
    public void createListBreadcrumb() {

    }

    @Override
    protected void selectSummaryItem(ProcessInstanceLogSummary summary) {

    }

    @Override
    protected DataSetReadyCallback getDataSetReadyCallback(Integer startRange,
                                                           FilterSettings tableSettings) {
        return null;
    }

    @Inject
    public void setTaskService(Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    public interface ProcessInstanceLogView extends MultiGridView<ProcessInstanceLogSummary, ProcessInstanceLogPresenter> {

        void setLogsList(final List<ProcessInstanceLogSummary> processInstanceLogSummaries);

        void hideLoadButton(boolean hidden);
    }
}