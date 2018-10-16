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
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;

import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.model.ProcessInstanceLogSummary;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

@Dependent
public class ProcessInstanceLogPresenter {

    public static final int PAGE_SIZE = 10;

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

    protected DataSetQueryHelper dataSetQueryHelper;

    protected ProcessInstanceLogFilterSettingsManager filterSettingsManager;

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    public void setDataSetQueryHelper(final DataSetQueryHelper dataSetQueryHelper) {
        this.dataSetQueryHelper = dataSetQueryHelper;
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
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public void loadProcessInstanceLogs() {
        try {
            final FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            currentTableSettings.setServerTemplateId(this.serverTemplateId);
            currentTableSettings.setTablePageSize(PAGE_SIZE);
            dataSetQueryHelper.setCurrentTableSettings(currentTableSettings);
            dataSetQueryHelper.setDataSetHandler(currentTableSettings);
            dataSetQueryHelper.lookupDataSet(
                    currentPage * getPageSize(),
                    new DataSetReadyCallback() {
                        @Override
                        public void callback(DataSet dataSet) {
                            if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(currentTableSettings.getKey())) {
                                List<ProcessInstanceLogSummary> logs = new ArrayList<ProcessInstanceLogSummary>();
                                for (int i = 0; i < dataSet.getRowCount(); i++) {
                                    logs.add(new ProcessInstanceLogSummaryDataSetMapper().apply(dataSet,
                                                                                                i));
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

    public void resetLogsList() {
        currentPage = 0;
        visibleLogs = new ArrayList<>();
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
        resetLogsList();
        dataSetQueryHelper.setCurrentTableSettings(filterSettingsManager.createDefaultFilterSettingsPrototype(event.getProcessInstanceId()));
        loadProcessInstanceLogs();
    }

    @Inject
    public void setTaskService(Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    public interface ProcessInstanceLogView extends UberElement<ProcessInstanceLogPresenter> {

        void setLogsList(final List<ProcessInstanceLogSummary> processInstanceLogSummaries);

        void hideLoadButton(boolean hidden);
    }
}