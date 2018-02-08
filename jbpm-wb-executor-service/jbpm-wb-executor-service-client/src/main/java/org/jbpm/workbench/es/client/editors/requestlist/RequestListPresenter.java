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

package org.jbpm.workbench.es.client.editors.requestlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;
import org.jbpm.workbench.es.client.editors.quicknewjob.NewJobPresenter;

import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.RequestStatus;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.util.DataSetUtils.*;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.JOB_LIST_SCREEN)
public class RequestListPresenter extends AbstractMultiGridPresenter<RequestSummary, RequestListPresenter.RequestListView> {

    private Constants constants = Constants.INSTANCE;

    private NewJobPresenter newJobPresenter;

    private Command newJobCommand;

    @Inject
    private Caller<ExecutorService> executorServices;

    @Inject
    private Event<RequestChangedEvent> requestChangedEvent;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private Event<JobSelectedEvent> jobSelectedEvent;

    @Inject
    protected void setNewJobPresenter(NewJobPresenter newJobPresenter) {
        this.newJobPresenter = newJobPresenter;
    }

    public RequestListPresenter() {
        super();
    }

    public RequestListPresenter(RequestListViewImpl view,
                                Caller<ExecutorService> executorServices,
                                DataSetQueryHelper dataSetQueryHelper,
                                Event<RequestChangedEvent> requestChangedEvent,
                                Event<JobSelectedEvent> jobSelectedEvent,
                                PlaceManager placeManager
    ) {
        this.view = view;
        this.executorServices = executorServices;
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.requestChangedEvent = requestChangedEvent;
        this.jobSelectedEvent = jobSelectedEvent;
        this.placeManager = placeManager;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Jobs();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            PerspectiveIds.JOBS,
                            Constants.INSTANCE.Jobs());
    }

    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              PerspectiveIds.JOBS,
                              Constants.INSTANCE.Jobs(),
                              detailLabel,
                              PerspectiveIds.JOB_DETAILS_SCREEN);
    }

    public Command getNewJobCommand() {
        return newJobCommand;
    }

    @Override
    public void getData(final Range visibleRange) {
        try {
            if (!isAddingDefaultFilters()) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                currentTableSettings.setServerTemplateId(getSelectedServerTemplate());
                currentTableSettings.setTablePageSize(view.getListGrid().getPageSize());
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                if (columnSortList != null && columnSortList.size() > 0) {
                    dataSetQueryHelper.setLastOrderedColumn(columnSortList.size() > 0 ? columnSortList.get(0).getColumn().getDataStoreName() : "");
                    dataSetQueryHelper.setLastSortOrder(columnSortList.size() > 0 && columnSortList.get(0).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
                } else {
                    dataSetQueryHelper.setLastOrderedColumn(COLUMN_TIMESTAMP);
                    dataSetQueryHelper.setLastSortOrder(SortOrder.ASCENDING);
                }

                dataSetQueryHelper.setDataSetHandler(currentTableSettings);
                dataSetQueryHelper.lookupDataSet(visibleRange.getStart(),
                                                 new AbstractDataSetReadyCallback(errorPopup,
                                                                                  view,
                                                                                  currentTableSettings.getUUID()) {
                                                     @Override
                                                     public void callback(DataSet dataSet) {
                                                         if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(currentTableSettings.getKey())) {
                                                             List<RequestSummary> myRequestSumaryFromDataSet = new ArrayList<RequestSummary>();

                                                             for (int i = 0; i < dataSet.getRowCount(); i++) {
                                                                 myRequestSumaryFromDataSet.add(getRequestSummary(dataSet,
                                                                                                                  i));
                                                             }
                                                             boolean lastPageExactCount = false;
                                                             if (dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                                                                 lastPageExactCount = true;
                                                             }
                                                             updateDataOnCallback(myRequestSumaryFromDataSet,
                                                                                  visibleRange.getStart(),
                                                                                  visibleRange.getStart() + myRequestSumaryFromDataSet.size(),
                                                                                  lastPageExactCount);
                                                         }
                                                     }
                                                 });
                view.hideBusyIndicator();
            }
        } catch (Exception e) {
            view.displayNotification(constants.ErrorRetrievingJobs(e.getMessage()));
            GWT.log("Error looking up dataset with UUID [ " + REQUEST_LIST_DATASET + " ]");
        }
    }

    protected RequestSummary getRequestSummary(final DataSet dataSet,
                                               final Integer index) {
        return new RequestSummary(
                getColumnLongValue(dataSet,
                                   COLUMN_ID,
                                   index),
                getColumnDateValue(dataSet,
                                   COLUMN_TIMESTAMP,
                                   index),
                RequestStatus.valueOf(getColumnStringValue(dataSet,
                                                           COLUMN_STATUS,
                                                           index)),
                getColumnStringValue(dataSet,
                                     COLUMN_COMMANDNAME,
                                     index),
                getColumnStringValue(dataSet,
                                     COLUMN_MESSAGE,
                                     index),
                getColumnStringValue(dataSet,
                                     COLUMN_BUSINESSKEY,
                                     index),
                getColumnIntValue(dataSet,
                                  COLUMN_RETRIES,
                                  index),
                getColumnIntValue(dataSet,
                                  COLUMN_EXECUTIONS,
                                  index),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_NAME,
                                     index),
                getColumnLongValue(dataSet,
                                   COLUMN_PROCESS_INSTANCE_ID,
                                   index),
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                     index),
                getColumnStringValue(dataSet,
                                     COLUMN_JOB_DEPLOYMENT_ID,
                                     index)
        );
    }

    public void cancelRequest(final String deploymentId,
                              final Long requestId) {
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification(constants.RequestCanceled(requestId));
                requestChangedEvent.fire(new RequestChangedEvent(requestId));
            }
        }).cancelRequest(getSelectedServerTemplate(),
                         deploymentId,
                         requestId);
    }

    public void requeueRequest(final String deploymentId,
                               final Long requestId) {
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification(constants.RequestRequeued(requestId));
                requestChangedEvent.fire(new RequestChangedEvent(requestId));
            }
        }).requeueRequest(getSelectedServerTemplate(),
                          deploymentId,
                          requestId);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        newJobCommand = new Command() {
            @Override
            public void execute() {
                final String selectedServerTemplate = getSelectedServerTemplate();
                if (selectedServerTemplate == null || selectedServerTemplate.trim().isEmpty()) {
                    view.displayNotification(constants.SelectServerTemplate());
                } else {
                    newJobPresenter.openNewJobDialog(selectedServerTemplate);
                }
            }
        };
        return MenuFactory
                .newTopLevelMenu(constants.New_Job())
                .respondsWith(newJobCommand)
                .endMenu()
                .newTopLevelCustomMenu(serverTemplateSelectorMenuBuilder)
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }

    public void selectJob(final RequestSummary job,
                          final Boolean close) {

        if (job.getStatus() != null) {
            PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest(PerspectiveIds.JOB_DETAILS_SCREEN));
            if (status == PlaceStatus.CLOSE) {
                placeManager.goTo(PerspectiveIds.JOB_DETAILS_SCREEN);
                setupDetailBreadcrumb(Constants.INSTANCE.JobBreadcrumb(job.getId()));
                jobSelectedEvent.fire(new JobSelectedEvent(getSelectedServerTemplate(),
                                                           job.getDeploymentId(),
                                                           job.getJobId()));
            } else if (status == PlaceStatus.OPEN && !close) {
                setupDetailBreadcrumb(Constants.INSTANCE.JobBreadcrumb(job.getId()));
                jobSelectedEvent.fire(new JobSelectedEvent(getSelectedServerTemplate(),
                                                           job.getDeploymentId(),
                                                           job.getJobId()));
            } else if (status == PlaceStatus.OPEN && close) {
                placeManager.closePlace(PerspectiveIds.JOB_DETAILS_SCREEN);
            }
        }
    }

    public void requestCreated(@Observes RequestChangedEvent event) {
        refreshGrid();
    }

    @Override
    public void setupAdvancedSearchView() {
        view.addNumericFilter(constants.Process_Instance_Id(),
                              constants.FilterByProcessInstanceId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                                       v))
        );

        view.addTextFilter(constants.BusinessKey(),
                           constants.FilterByBusinessKey(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_BUSINESSKEY,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_BUSINESSKEY,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Type(),
                           constants.FilterByType(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_COMMANDNAME,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_COMMANDNAME,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Process_Instance_Description(),
                           constants.FilterByProcessDescription(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                  v,
                                                                  false))
        );

        final Map<String, String> status = new HashMap<>();
        status.put(RequestStatus.CANCELLED.name(),
                   constants.Canceled());
        status.put(RequestStatus.DONE.name(),
                   constants.Completed());
        status.put(RequestStatus.ERROR.name(),
                   constants.Error());
        status.put(RequestStatus.QUEUED.name(),
                   constants.Queued());
        status.put(RequestStatus.RETRYING.name(),
                   constants.Retrying());
        status.put(RequestStatus.RUNNING.name(),
                   constants.Running());

        view.addSelectFilter(constants.Status(),
                             status,
                             false,
                             v -> addAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                   v)),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );

        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(REQUEST_LIST_DATASET)
                .group(COLUMN_PROCESS_NAME)
                .column(COLUMN_PROCESS_NAME)
                .sort(COLUMN_PROCESS_NAME,
                      SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Process_Name(),
                                    AbstractMultiGridView.TAB_SEARCH,
                                    dataSetLookup,
                                    COLUMN_PROCESS_NAME,
                                    COLUMN_PROCESS_NAME,
                                    v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_NAME,
                                                                          v)),
                                    v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_NAME,
                                                                             v)));

        view.addDateRangeFilter(constants.Due_On(),
                                constants.Due_On_Placeholder(),
                                false,
                                v -> addAdvancedSearchFilter(between(COLUMN_TIMESTAMP,
                                                                     v.getStartDate(),
                                                                     v.getEndDate())),
                                v -> removeAdvancedSearchFilter(between(COLUMN_TIMESTAMP,
                                                                        v.getStartDate(),
                                                                        v.getEndDate()))
        );
    }

    @Override
    public void setupActiveSearchFilters() {
        final Optional<String> processInstIdSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID);
        if (processInstIdSearch.isPresent()) {
            final String processInstId = processInstIdSearch.get();
            view.addActiveFilter(
                    constants.Process_Instance_Id(),
                    processInstId,
                    processInstId,
                    v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                             v))
            );
            addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                             processInstId));
        } else {
            final Optional<String> jobSearch = getSearchParameter(PerspectiveIds.SEARCH_PARAMETER_JOB_ID);
            if (jobSearch.isPresent()) {
                final String jobId = jobSearch.get();
                view.addActiveFilter(constants.JobId(),
                                     jobId,
                                     jobId,
                                     v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                              v))
                );

                addAdvancedSearchFilter(equalsTo(COLUMN_ID,
                                                 jobId));
            } else {
                setupDefaultActiveSearchFilters();
            }
        }
    }

    @Override
    public void setupDefaultActiveSearchFilters() {
        view.addActiveFilter(constants.Status(),
                             constants.Running(),
                             RequestStatus.RUNNING.name(),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );
        addAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                         RequestStatus.RUNNING));
    }

    public void openProcessInstanceView(final String processInstanceId) {
        navigateToPerspective(PerspectiveIds.PROCESS_INSTANCES,
                              PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                              processInstanceId);
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createStatusSettings(null);
    }

    private FilterSettings createStatusSettings(final RequestStatus status) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();
        builder.dataset(REQUEST_LIST_DATASET);
        if (status != null) {
            builder.filter(equalsTo(COLUMN_STATUS,
                                    status.name()));
        }
        builder.setColumn(COLUMN_ID,
                          constants.Id());
        builder.setColumn(COLUMN_TIMESTAMP,
                          constants.Time(),
                          DateUtils.getDateTimeFormatMask());
        builder.setColumn(COLUMN_STATUS,
                          constants.Status());
        builder.setColumn(COLUMN_COMMANDNAME,
                          constants.CommandName());
        builder.setColumn(COLUMN_MESSAGE,
                          constants.Message());
        builder.setColumn(COLUMN_BUSINESSKEY,
                          constants.Key());
        builder.setColumn(COLUMN_RETRIES,
                          constants.Retries());
        builder.setColumn(COLUMN_EXECUTIONS,
                          constants.Executions());
        builder.setColumn(COLUMN_PROCESS_NAME,
                          constants.Process_Name());
        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID,
                          constants.Process_Instance_Id());
        builder.setColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                          constants.Process_Instance_Description());
        builder.setColumn(COLUMN_JOB_DEPLOYMENT_ID,
                          constants.DeploymentId());

        builder.filterOn(true,
                         true,
                         true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_TIMESTAMP,
                                  SortOrder.DESCENDING);
        return builder.buildSettings();
    }

    @Override
    public FilterSettings createSearchTabSettings() {
        return createTableSettingsPrototype();
    }

    public FilterSettings createAllTabSettings() {
        return createStatusSettings(null);
    }

    public FilterSettings createQueuedTabSettings() {
        return createStatusSettings(RequestStatus.QUEUED);
    }

    public FilterSettings createRunningTabSettings() {
        return createStatusSettings(RequestStatus.RUNNING);
    }

    public FilterSettings createRetryingTabSettings() {
        return createStatusSettings(RequestStatus.RETRYING);
    }

    public FilterSettings createErrorTabSettings() {
        return createStatusSettings(RequestStatus.ERROR);
    }

    public FilterSettings createCompletedTabSettings() {
        return createStatusSettings(RequestStatus.DONE);
    }

    public FilterSettings createCanceledTabSettings() {
        return createStatusSettings(RequestStatus.CANCELLED);
    }

    public Predicate<RequestSummary> getCancelActionCondition() {
        return getActionConditionFromStatusList(new RequestStatus[]{
                RequestStatus.QUEUED,
                RequestStatus.RETRYING,
                RequestStatus.RUNNING
        });
    }

    public Predicate<RequestSummary> getRequeueActionCondition() {
        return getActionConditionFromStatusList(new RequestStatus[]{
                RequestStatus.ERROR,
                RequestStatus.RUNNING
        });
    }

    public Predicate<RequestSummary> getViewProcessActionCondition() {
        return job -> (job.getProcessInstanceId() != null);
    }

    private Predicate<RequestSummary> getActionConditionFromStatusList(RequestStatus[] statusList) {
        return value -> Arrays.stream(statusList).anyMatch(
                s -> s.equals(value.getStatus()));
    }

    public interface RequestListView extends MultiGridView<RequestSummary, RequestListPresenter> {

    }
}