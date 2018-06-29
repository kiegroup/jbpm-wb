/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.es.client.editors.errordetails.ExecutionErrorDetailsPresenter;
import org.jbpm.workbench.es.client.editors.events.ExecutionErrorSelectedEvent;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.ExecutionErrorType;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.EXECUTION_ERROR_LIST_SCREEN)
public class ExecutionErrorListPresenter extends AbstractMultiGridPresenter<ExecutionErrorSummary, ExecutionErrorListPresenter.ExecutionErrorListView> {

    private final Constants constants = Constants.INSTANCE;

    private final org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private Caller<ExecutorService> executorService;

    @Inject
    private Event<ExecutionErrorSelectedEvent> executionErrorSelectedEvent;

    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            commonConstants.Manage_ExecutionErrors());
    }

    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              commonConstants.Manage_ExecutionErrors(),
                              detailLabel,
                              PerspectiveIds.EXECUTION_ERROR_DETAILS_SCREEN);
    }

    @Override
    protected DataSetReadyCallback getDataSetReadyCallback(final Integer startRange,
                                                           final FilterSettings tableSettings) {
        return new AbstractDataSetReadyCallback(errorPopup,
                                                view,
                                                tableSettings.getUUID()) {
            @Override
            public void callback(DataSet dataSet) {
                if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(tableSettings.getKey())) {
                    List<ExecutionErrorSummary> visibleExecutionErrors = new ArrayList<ExecutionErrorSummary>();
                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        visibleExecutionErrors.add(createExecutionErrorSummaryFromDataSet(dataSet,
                                                                                          i));
                    }

                    boolean lastPageExactCount = false;
                    if (dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                        lastPageExactCount = true;
                    }
                    updateDataOnCallback(visibleExecutionErrors,
                                         startRange,
                                         startRange + visibleExecutionErrors.size(),
                                         lastPageExactCount);
                }
            }
        };
    }

    protected ExecutionErrorSummary createExecutionErrorSummaryFromDataSet(final DataSet dataSet,
                                                                           final Integer index) {
        return new ExecutionErrorSummary(
                getColumnStringValue(dataSet,
                                     COLUMN_ERROR_ID,
                                     index),
                getColumnStringValue(dataSet,
                                     COLUMN_ERROR_TYPE,
                                     index),
                getColumnStringValue(dataSet,
                                     COLUMN_DEPLOYMENT_ID,
                                     index),
                getColumnLongValue(dataSet,
                                   COLUMN_PROCESS_INST_ID,
                                   index),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_ID,
                                     index),
                getColumnLongValue(dataSet,
                                   COLUMN_ACTIVITY_ID,
                                   index),
                getColumnStringValue(dataSet,
                                     COLUMN_ACTIVITY_NAME,
                                     index),
                getColumnLongValue(dataSet,
                                   COLUMN_JOB_ID,
                                   index),
                getColumnStringValue(dataSet,
                                     COLUMN_ERROR_MSG,
                                     index),
                getColumnIntValue(dataSet,
                                  COLUMN_ERROR_ACK,
                                  index).shortValue(),
                getColumnStringValue(dataSet,
                                     COLUMN_ERROR_ACK_BY,
                                     index),
                getColumnDateValue(dataSet,
                                   COLUMN_ERROR_ACK_AT,
                                   index),
                getColumnDateValue(dataSet,
                                   COLUMN_ERROR_DATE,
                                   index)
        );
    }

    public void acknowledgeExecutionError(final String executionErrorId,
                                          final String deploymentId) {
        executorService.call((Void nothing) -> {
            view.displayNotification(constants.ExecutionErrorAcknowledged(executionErrorId));
            refreshGrid();
        }).acknowledgeError(getSelectedServerTemplate(),
                            deploymentId,
                            executionErrorId);
    }

    public void goToJob(ExecutionErrorSummary errorSummary) {
        navigateToPerspective(PerspectiveIds.JOBS,
                              PerspectiveIds.SEARCH_PARAMETER_JOB_ID,
                              errorSummary.getJobId().toString());
    }

    public Predicate<ExecutionErrorSummary> getAcknowledgeActionCondition() {
        return pis -> !pis.isAcknowledged();
    }

    public Predicate<ExecutionErrorSummary> getViewJobActionCondition() {
        return pis -> isUserAuthorizedForPerspective(PerspectiveIds.JOBS) && pis.getJobId() != null;
    }

    public Predicate<ExecutionErrorSummary> getViewProcessInstanceActionCondition() {
        return pis -> isUserAuthorizedForPerspective(PerspectiveIds.PROCESS_INSTANCES) && pis.getProcessInstanceId() != null;
    }

    public Predicate<ExecutionErrorSummary> getViewTaskActionCondition() {
        return pis -> ((isUserAuthorizedForPerspective(PerspectiveIds.TASKS_ADMIN) ||
                isUserAuthorizedForPerspective(PerspectiveIds.TASKS)) &&
                pis.getActivityId() != null && ExecutionErrorType.TASK.getType().equals(pis.getType().getType()));
    }

    public void goToProcessInstance(final ExecutionErrorSummary errorSummary) {
        navigateToPerspective(PerspectiveIds.PROCESS_INSTANCES,
                              PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              errorSummary.getProcessInstanceId().toString());
    }

    public void goToTask(final ExecutionErrorSummary errorSummary) {
        navigateToPerspective(isUserAuthorizedForPerspective(PerspectiveIds.TASKS_ADMIN) ?
                                      PerspectiveIds.TASKS_ADMIN :
                                      PerspectiveIds.TASKS,
                              PerspectiveIds.SEARCH_PARAMETER_TASK_ID,
                              errorSummary.getActivityId().toString());
    }

    public void bulkAcknowledge(List<ExecutionErrorSummary> execErrorsSelected) {
        if (execErrorsSelected == null || execErrorsSelected.isEmpty()) {
            return;
        }
        for (ExecutionErrorSummary selected : execErrorsSelected) {
            if (selected.isAcknowledged()) {
                //TODO i18n
                view.displayNotification("Error " + selected.getErrorId() + "is already acknowledge");
                continue;
            } else {
                acknowledgeExecutionError(selected.getErrorId(),
                                          selected.getDeploymentId());
            }
        }
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .build();
    }

    public void selectExecutionError(final ExecutionErrorSummary summary) {
        setupDetailBreadcrumb(constants.ExecutionErrorBreadcrumb(ExecutionErrorDetailsPresenter.getErrorDetailTitle(summary)));
        placeManager.goTo(PerspectiveIds.EXECUTION_ERROR_DETAILS_SCREEN);
        executionErrorSelectedEvent.fire(new ExecutionErrorSelectedEvent(getSelectedServerTemplate(),
                                                                         summary.getDeploymentId(),
                                                                         summary.getErrorId()));
    }

    @Inject
    public void setFilterSettingsManager(final ExecutionErrorListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    @Inject
    public void setExecutorService(final Caller<ExecutorService> executorService) {
        this.executorService = executorService;
    }

    @Override
    public void setupActiveSearchFilters() {
        boolean isDefaultFilters = true;

        final Optional<String> processInstanceSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID);
        if (processInstanceSearch.isPresent()) {
            final String processInstanceId = processInstanceSearch.get();
            addActiveFilter(equalsTo(COLUMN_PROCESS_INST_ID,
                                     Integer.valueOf(processInstanceId)),
                            constants.Process_Instance_Id(),
                            processInstanceId,
                            Integer.valueOf(processInstanceId),
                            v -> removeActiveFilter(equalsTo(COLUMN_PROCESS_INST_ID,
                                                             v))
            );
            isDefaultFilters = false;
        }

        final Optional<String> taskIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_TASK_ID);
        if (taskIdSearch.isPresent()) {
            final String taskId = taskIdSearch.get();
            addActiveFilter(equalsTo(COLUMN_ACTIVITY_ID,
                                     Integer.valueOf(taskId)),
                            constants.Task(),
                            taskId,
                            Integer.valueOf(taskId),
                            v -> removeActiveFilter(equalsTo(COLUMN_ACTIVITY_ID,
                                                             v))
            );

            isDefaultFilters = false;
        }

        final Optional<String> errorTypeSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_ERROR_TYPE);
        if (errorTypeSearch.isPresent()) {
            final String errorType = errorTypeSearch.get();
            addActiveFilter(equalsTo(COLUMN_ERROR_TYPE,
                                     errorType),
                            constants.Type(),
                            errorType,
                            errorType,
                            v -> removeActiveFilter(equalsTo(COLUMN_ERROR_TYPE,
                                                             v))
            );

            isDefaultFilters = false;
        }

        final Optional<String> isErrorAckSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_IS_ERROR_ACK);
        if (isErrorAckSearch.isPresent()) {
            final boolean isErrorAck = isErrorAckSearch.get().equalsIgnoreCase(Boolean.toString(true));
            Integer errorAckValue = (isErrorAck ? 1 : 0);
            String valueLabel = (isErrorAck ?
                    commonConstants.Yes()
                    :
                    commonConstants.No());

            addActiveFilter(
                    equalsTo(COLUMN_ERROR_ACK,
                             errorAckValue),
                    constants.Acknowledged(),
                    valueLabel,
                    errorAckValue,
                    v -> removeActiveFilter(equalsTo(COLUMN_ERROR_ACK,
                                                     v))
            );

            isDefaultFilters = false;
        }

        if (isDefaultFilters) {
            setupDefaultActiveSearchFilters();
        }
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        addActiveFilter(
                equalsTo(COLUMN_ERROR_ACK,
                         0),
                constants.Acknowledged(),
                commonConstants.No(),
                0,
                v -> removeActiveFilter(equalsTo(COLUMN_ERROR_ACK,
                                                 v))
        );
    }

    public void setExecutionErrorSelectedEvent(Event<ExecutionErrorSelectedEvent> executionErrorSelectedEvent) {
        this.executionErrorSelectedEvent = executionErrorSelectedEvent;
    }

    public interface ExecutionErrorListView extends MultiGridView<ExecutionErrorSummary, ExecutionErrorListPresenter> {

    }
}
