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
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.*;
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.forms.client.display.process.QuickNewProcessInstancePopup;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.*;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = PROCESS_INSTANCE_LIST_SCREEN)
public class ProcessInstanceListPresenter extends AbstractMultiGridPresenter<ProcessInstanceSummary, ProcessInstanceListPresenter.ProcessInstanceListView> {

    protected final List<ProcessInstanceSummary> myProcessInstancesFromDataSet = new ArrayList<ProcessInstanceSummary>();

    private final Constants constants = Constants.INSTANCE;

    private final org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;

    @Inject
    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    private Caller<ProcessService> processService;

    protected Event<ProcessInstanceSelectionEvent> processInstanceSelectionEvent;

    @Inject
    public void setProcessInstanceSelectedEvent(final Event<ProcessInstanceSelectionEvent> processInstanceSelectionEvent) {
        this.processInstanceSelectionEvent = processInstanceSelectionEvent;
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            commonConstants.Manage_Process_Instances());
    }

    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              commonConstants.Manage_Process_Instances(),
                              detailLabel,
                              PROCESS_INSTANCE_DETAILS_SCREEN);
    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback(final int startRange,
                                                                       final FilterSettings tableSettings,
                                                                       boolean lastPage) {
        return new AbstractDataSetReadyCallback(errorPopup,
                                                view,
                                                tableSettings.getUUID()) {
            @Override
            public void callback(DataSet dataSet) {
                Set<String> columns = new HashSet<String>();
                for (int i = 0; i < dataSet.getRowCount(); i++) {
                    Long processInstanceId = getColumnLongValue(dataSet,
                                                                PROCESS_INSTANCE_ID,
                                                                i);
                    String variableName = getColumnStringValue(dataSet,
                                                               VARIABLE_NAME,
                                                               i);
                    String variableValue = getColumnStringValue(dataSet,
                                                                VARIABLE_VALUE,
                                                                i);

                    for (ProcessInstanceSummary pis : myProcessInstancesFromDataSet) {
                        String initiator = pis.getInitiator();
                        if (pis.getProcessInstanceId().equals(processInstanceId) && !filterInitiator(variableName,
                                                                                                     variableValue,
                                                                                                     initiator)) {
                            pis.addDomainData(variableName,
                                              variableValue);
                            columns.add(variableName);
                        }
                    }
                }
                view.addDomainSpecifColumns(view.getListGrid(),
                                            columns);

                updateDataOnCallback(myProcessInstancesFromDataSet,
                                     startRange,
                                     startRange + myProcessInstancesFromDataSet.size(),
                                     lastPage);
            }
        };
    }

    protected boolean filterInitiator(String variableName,
                                      String variableValue,
                                      String initiator) {
        return variableName.equals("initiator") && variableValue.equals(initiator);
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

                    myProcessInstancesFromDataSet.clear();
                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                        myProcessInstancesFromDataSet.add(createProcessInstanceSummaryFromDataSet(dataSet,
                                                                                                  i));
                    }

                    boolean lastPage = false;
                    if (dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                        lastPage = true;
                    }

                    final String filterValue = isFilteredByProcessId(tableSettings.getDataSetLookup().getOperationList());
                    if (filterValue != null) {
                        getDomainSpecifDataForProcessInstances(startRange,
                                                               filterValue,
                                                               lastPage);
                    } else {
                        updateDataOnCallback(myProcessInstancesFromDataSet,
                                             startRange,
                                             startRange + myProcessInstancesFromDataSet.size(),
                                             lastPage);
                    }
                }
                view.hideBusyIndicator();
            }

            @Override
            public boolean onError(final ClientRuntimeError error) {
                view.hideBusyIndicator();

                showErrorPopup(constants.ResourceCouldNotBeLoaded(commonConstants.Process_Instances()));

                return false;
            }
        };
    }

    void showErrorPopup(final String message) {
        ErrorPopup.showMessage(message);
    }

    protected String isFilteredByProcessId(List<DataSetOp> ops) {
        for (DataSetOp dataSetOp : ops) {
            if (dataSetOp.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> filters = ((DataSetFilter) dataSetOp).getColumnFilterList();

                for (ColumnFilter filter : filters) {

                    if (filter instanceof CoreFunctionFilter) {
                        CoreFunctionFilter coreFilter = ((CoreFunctionFilter) filter);
                        if (filter.getColumnId().toUpperCase().equals(COLUMN_PROCESS_ID.toUpperCase()) &&
                                ((CoreFunctionFilter) filter).getType() == CoreFunctionType.EQUALS_TO) {

                            List parameters = coreFilter.getParameters();
                            if (parameters.size() > 0) {
                                return parameters.get(0).toString();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public void getDomainSpecifDataForProcessInstances(final int startRange,
                                                       String filterValue,
                                                       boolean lastPage) {
        FilterSettings variablesTableSettings = filterSettingsManager.getVariablesFilterSettings(filterValue);
        variablesTableSettings.setServerTemplateId(getSelectedServerTemplate());
        variablesTableSettings.setTablePageSize(-1);

        dataSetQueryHelperDomainSpecific.setDataSetHandler(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setCurrentTableSettings(variablesTableSettings);
        dataSetQueryHelperDomainSpecific.setLastOrderedColumn(PROCESS_INSTANCE_ID);
        dataSetQueryHelperDomainSpecific.setLastSortOrder(SortOrder.ASCENDING);
        dataSetQueryHelperDomainSpecific.lookupDataSet(0,
                                                       createDataSetDomainSpecificCallback(startRange,
                                                                                           variablesTableSettings,
                                                                                           lastPage));
    }

    protected ProcessInstanceSummary createProcessInstanceSummaryFromDataSet(DataSet dataSet,
                                                                             int i) {
        return new ProcessInstanceSummary(
                getColumnLongValue(dataSet,
                                   COLUMN_PROCESS_INSTANCE_ID,
                                   i),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_ID,
                                     i),
                getColumnStringValue(dataSet,
                                     COLUMN_EXTERNAL_ID,
                                     i),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_NAME,
                                     i),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_VERSION,
                                     i),
                getColumnIntValue(dataSet,
                                  COLUMN_STATUS,
                                  i),
                getColumnDateValue(dataSet,
                                   COLUMN_START,
                                   i),
                getColumnDateValue(dataSet,
                                   COLUMN_END,
                                   i),
                getColumnStringValue(dataSet,
                                     COLUMN_IDENTITY,
                                     i),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                     i),
                getColumnStringValue(dataSet,
                                     COLUMN_CORRELATION_KEY,
                                     i),
                getColumnLongValue(dataSet,
                                   COLUMN_PARENT_PROCESS_INSTANCE_ID,
                                   i),
                getColumnDateValue(dataSet,
                                   COLUMN_LAST_MODIFICATION_DATE,
                                   i),
                getColumnIntValue(dataSet,
                                  COLUMN_SLA_COMPLIANCE,
                                  i),
                getColumnDateValue(dataSet,
                                   COLUMN_SLA_DUE_DATE,
                                   i),
                getColumnIntValue(dataSet,
                                  COLUMN_ERROR_COUNT,
                                  i)
        );
    }

    public void newInstanceCreated(@Observes final NewProcessInstanceEvent pi) {
        refreshGrid();
    }

    public void newInstanceCreated(@Observes final ProcessInstancesUpdateEvent pis) {
        refreshGrid();
    }

    public void abortProcessInstance(String containerId,
                                     long processInstanceId) {
        view.displayNotification(constants.Aborting_Process_Instance(processInstanceId));
        processService.call((Void v) -> refreshGrid()).abortProcessInstance(getSelectedServerTemplate(),
                                                                            containerId,
                                                                            processInstanceId);
    }

    public void abortProcessInstances(Map<String, List<Long>> containerInstances) {
        processService.call((Void v) -> refreshGrid()).abortProcessInstances(getSelectedServerTemplate(),
                                                                             containerInstances);
    }

    public void bulkSignal(List<ProcessInstanceSummary> processInstances) {
        if (processInstances == null || processInstances.isEmpty()) {
            return;
        }

        final StringBuilder processIdsParam = new StringBuilder();
        final StringBuilder deploymentIdsParam = new StringBuilder();
        for (ProcessInstanceSummary selected : processInstances) {
            if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                view.displayNotification(constants.Signaling_Process_Instance_Not_Allowed(selected.getId()));
                continue;
            }
            processIdsParam.append(selected.getId() + ",");
            deploymentIdsParam.append(selected.getDeploymentId() + ",");
        }

        if (processIdsParam.length() == 0) {
            return;
        } else {
            // remove last ,
            processIdsParam.deleteCharAt(processIdsParam.length() - 1);
            deploymentIdsParam.deleteCharAt(deploymentIdsParam.length() - 1);
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP);
        placeRequestImpl.addParameter("processInstanceId",
                                      processIdsParam.toString());
        placeRequestImpl.addParameter("deploymentId",
                                      deploymentIdsParam.toString());
        placeRequestImpl.addParameter("serverTemplateId",
                                      getSelectedServerTemplate());

        placeManager.goTo(placeRequestImpl);
    }

    public void bulkAbort(List<ProcessInstanceSummary> processInstances) {
        if (processInstances == null || processInstances.isEmpty()) {
            return;
        }

        final Map<String, List<Long>> containerInstances = new HashMap<>();
        for (ProcessInstanceSummary selected : processInstances) {
            if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                view.displayNotification(constants.Aborting_Process_Instance_Not_Allowed(selected.getId()));
                continue;
            }
            containerInstances.computeIfAbsent(selected.getDeploymentId(), key -> new ArrayList<>()).add(selected.getProcessInstanceId());
            view.displayNotification(constants.Aborting_Process_Instance(selected.getId()));
        }
        if (containerInstances.size() > 0) {
            abortProcessInstances(containerInstances);
        }
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(new PrimaryActionMenuBuilder(constants.New_Process_Instance(),
                                                                    () -> {
                                                                        final String selectedServerTemplate = getSelectedServerTemplate();
                                                                        if (selectedServerTemplate != null && !selectedServerTemplate.isEmpty()) {
                                                                            newProcessInstancePopup.show(selectedServerTemplate);
                                                                        } else {
                                                                            view.displayNotification(constants.SelectServerTemplate());
                                                                        }
                                                                    }
                ))
                .endMenu()
                .build();
    }

    public void signalProcessInstance(final ProcessInstanceSummary processInstance) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP);
        placeRequestImpl.addParameter("processInstanceId",
                                      Long.toString(processInstance.getProcessInstanceId()));
        placeRequestImpl.addParameter("deploymentId",
                                      processInstance.getDeploymentId());
        placeRequestImpl.addParameter("serverTemplateId",
                                      getSelectedServerTemplate());

        placeManager.goTo(placeRequestImpl);
    }

    @Override
    public void selectSummaryItem(final ProcessInstanceSummary summary) {
        setupDetailBreadcrumb(constants.ProcessInstanceBreadcrumb(summary.getProcessInstanceId()));
        placeManager.goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
        processInstanceSelectionEvent.fire(new ProcessInstanceSelectionEvent(summary.getDeploymentId(),
                                                                             summary.getProcessInstanceId(),
                                                                             summary.getProcessId(),
                                                                             summary.getProcessName(),
                                                                             summary.getState(),
                                                                             getSelectedServerTemplate()));
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if (ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP.equals(closed.getPlace().getIdentifier())) {
            refreshGrid();
        }
    }

    @Inject
    public void setFilterSettingsManager(final ProcessInstanceListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    @Inject
    public void setProcessService(final Caller<ProcessService> processService) {
        this.processService = processService;
    }

    @Override
    public void setupActiveSearchFilters() {
        boolean hasSearchParam = false;

        final Optional<String> processDefinitionSearch = getSearchParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID);
        if (processDefinitionSearch.isPresent()) {
            final String processDefinitionId = processDefinitionSearch.get();
            addActiveFilter(equalsTo(COLUMN_PROCESS_ID,
                                     processDefinitionId),
                            constants.Process_Definition_Id(),
                            processDefinitionId,
                            processDefinitionId,
                            v -> removeActiveFilter(equalsTo(COLUMN_PROCESS_ID,
                                                             v))
            );
            hasSearchParam = true;
        }

        final Optional<String> processInstanceSearch = getSearchParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID);
        if (processInstanceSearch.isPresent()) {
            final String processInstanceId = processInstanceSearch.get();
            addActiveFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                     Integer.valueOf(processInstanceId)),
                            constants.Id(),
                            processInstanceId,
                            Integer.valueOf(processInstanceId),
                            v -> removeActiveFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                             v))
            );
            hasSearchParam = true;
        }

        if (!hasSearchParam) {
            setupDefaultActiveSearchFilters();
        }
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        addActiveFilter(equalsTo(COLUMN_STATUS,
                                 ProcessInstance.STATE_ACTIVE),
                        constants.State(),
                        constants.Active(),
                        ProcessInstance.STATE_ACTIVE,
                        v -> removeActiveFilter(equalsTo(COLUMN_STATUS,
                                                         v))
        );
    }

    public void openJobsView(final String pid) {
        navigateToPerspective(JOBS,
                              SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              pid);
    }

    public void openTaskView(final String pid) {
        navigateToPerspective(isUserAuthorizedForPerspective(TASKS_ADMIN) ? TASKS_ADMIN : TASKS,
                              SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              pid);
    }

    @Override
    public void openErrorView(final String pid) {
        final PlaceRequest request = new DefaultPlaceRequest(EXECUTION_ERRORS);
        request.addParameter(SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                             pid);
        request.addParameter(SEARCH_PARAMETER_IS_ERROR_ACK,
                             Boolean.toString(false));
        placeManager.goTo(request);
    }

    public Predicate<ProcessInstanceSummary> getSignalActionCondition() {
        return pis -> pis.getState() == ProcessInstance.STATE_ACTIVE;
    }

    public Predicate<ProcessInstanceSummary> getAbortActionCondition() {
        return pis -> pis.getState() == ProcessInstance.STATE_ACTIVE;
    }

    public Predicate<ProcessInstanceSummary> getViewJobsActionCondition() {
        return pis -> isUserAuthorizedForPerspective(JOBS);
    }

    public Predicate<ProcessInstanceSummary> getViewTasksActionCondition() {
        return pis -> isUserAuthorizedForPerspective(TASKS_ADMIN) || isUserAuthorizedForPerspective(TASKS);
    }

    @Override
    public Predicate<ProcessInstanceSummary> getViewErrorsActionCondition() {
        return pis -> isUserAuthorizedForPerspective(EXECUTION_ERRORS) && pis.getErrorCount() != null && pis.getErrorCount() > 0;
    }

    public interface ProcessInstanceListView extends MultiGridView<ProcessInstanceSummary, ProcessInstanceListPresenter> {

        void addDomainSpecifColumns(ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable,
                                    Set<String> columns);
    }
}
