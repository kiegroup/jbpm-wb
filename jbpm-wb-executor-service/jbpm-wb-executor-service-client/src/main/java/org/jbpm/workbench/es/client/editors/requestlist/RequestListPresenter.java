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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.es.util.RequestStatus;
import org.jbpm.workbench.es.client.editors.jobdetails.JobDetailsPopup;
import org.jbpm.workbench.es.client.editors.quicknewjob.QuickNewJobPopup;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = "Requests List")
public class RequestListPresenter extends AbstractMultiGridPresenter<RequestSummary, RequestListPresenter.RequestListView> {

    public interface RequestListView extends MultiGridView<RequestSummary, RequestListPresenter> {

    }

    private Constants constants = Constants.INSTANCE;

    @Inject
    private Caller<ExecutorService> executorServices;

    @Inject
    private Event<RequestChangedEvent> requestChangedEvent;

    @Inject
    private QuickNewJobPopup quickNewJobPopup;

    @Inject
    private ErrorPopupPresenter errorPopup;

    @Inject
    private JobDetailsPopup jobDetailsPopup;

    public RequestListPresenter() {
        super();
    }

    public RequestListPresenter(RequestListViewImpl view,
            Caller<ExecutorService> executorServices,
            DataSetQueryHelper dataSetQueryHelper,Event<RequestChangedEvent> requestChangedEvent
    ) {
        this.view = view;
        this.executorServices = executorServices;
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.requestChangedEvent = requestChangedEvent;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.RequestsListTitle();
    }

    @Override
    public void getData( final Range visibleRange ) {
        try {
            if(!isAddingDefaultFilters()) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                currentTableSettings.setServerTemplateId(selectedServerTemplate);
                currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                if ( columnSortList != null && columnSortList.size() > 0 ) {
                    dataSetQueryHelper.setLastOrderedColumn( columnSortList.size() > 0 ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                    dataSetQueryHelper.setLastSortOrder( columnSortList.size() > 0 && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                } else {
                    dataSetQueryHelper.setLastOrderedColumn( COLUMN_TIMESTAMP );
                    dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                }

                final List<ColumnFilter> filters = getColumnFilters(textSearchStr);
                if (filters.isEmpty() == false) {
                    if (currentTableSettings.getDataSetLookup().getFirstFilterOp() != null) {
                        currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn(OR(filters));
                    } else {
                        final DataSetFilter filter = new DataSetFilter();
                        filter.addFilterColumn(OR(filters));
                        currentTableSettings.getDataSetLookup().addOperation(filter);
                    }
                }

                dataSetQueryHelper.setDataSetHandler(currentTableSettings);
                dataSetQueryHelper.lookupDataSet(visibleRange.getStart(), new AbstractDataSetReadyCallback( errorPopup, view, currentTableSettings.getUUID() ) {
                    @Override
                    public void callback(DataSet dataSet) {
                        if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(currentTableSettings.getKey())) {
                            List<RequestSummary> myRequestSumaryFromDataSet = new ArrayList<RequestSummary>();

                            for (int i = 0; i < dataSet.getRowCount(); i++) {

                                myRequestSumaryFromDataSet.add(getRequestSummary(dataSet, i));
                            }
                            boolean lastPageExactCount=false;
                            if( dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                                lastPageExactCount=true;
                            }
                            updateDataOnCallback(myRequestSumaryFromDataSet,visibleRange.getStart(),visibleRange.getStart()+ myRequestSumaryFromDataSet.size(), lastPageExactCount);

                        }
                    }
                });
                view.hideBusyIndicator();
            }
        } catch ( Exception e ) {
            view.displayNotification(constants.ErrorRetrievingJobs(e.getMessage()));
            GWT.log( "Error looking up dataset with UUID [ " + REQUEST_LIST_DATASET + " ]" );
        }

    }

    protected RequestSummary getRequestSummary(final DataSet dataSet, final Integer index) {
        return new RequestSummary(
                dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_ID, index),
                dataSetQueryHelper.getColumnDateValue(dataSet, COLUMN_TIMESTAMP, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_STATUS, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_COMMANDNAME, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_MESSAGE, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_BUSINESSKEY, index),
                dataSetQueryHelper.getColumnIntValue(dataSet, COLUMN_RETRIES, index),
                dataSetQueryHelper.getColumnIntValue(dataSet, COLUMN_EXECUTIONS, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_PROCESS_NAME, index),
                dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_PROCESS_INSTANCE_ID, index),
                dataSetQueryHelper.getColumnStringValue(dataSet, COLUMN_PROCESS_INSTANCE_DESCRIPTION, index)
        );
    }

    protected List<ColumnFilter> getColumnFilters(final String searchString) {
        final List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
        if (searchString != null && searchString.trim().length() > 0) {
            filters.add( likeTo( COLUMN_COMMANDNAME, "%" + searchString.toLowerCase() + "%", false ) );
            filters.add( likeTo( COLUMN_MESSAGE, "%" + searchString.toLowerCase() + "%", false ) );
            filters.add( likeTo( COLUMN_BUSINESSKEY, "%" + searchString.toLowerCase() + "%", false ) );
        }
        return filters;
    }

    public void cancelRequest( final Long requestId ) {
        executorServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( constants.RequestCancelled(requestId) );
                requestChangedEvent.fire( new RequestChangedEvent( requestId ) );
            }
        } ).cancelRequest( selectedServerTemplate, requestId );
    }

    public void requeueRequest( final Long requestId ) {
        executorServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( constants.RequestCancelled(requestId) );
                requestChangedEvent.fire( new RequestChangedEvent( requestId ) );
            }
        } ).requeueRequest( selectedServerTemplate, requestId );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu(constants.New_Job())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        if (selectedServerTemplate == null || selectedServerTemplate.trim().isEmpty()) {
                            view.displayNotification(constants.SelectServerTemplate());
                        } else {
                            quickNewJobPopup.show(selectedServerTemplate);
                        }

                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(serverTemplateSelectorMenuBuilder)
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }

    public void showJobDetails(final RequestSummary job){
        jobDetailsPopup.show( selectedServerTemplate, String.valueOf( job.getJobId() ) );
    }

    public void requestCreated( @Observes RequestChangedEvent event ) {
        refreshGrid();
    }

    @Override
    public void setupAdvanceSearchView() {
        view.addNumericFilter(constants.Id(),
                              constants.FilterByProcessInstanceId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                                       v))
        );

        view.addTextFilter(constants.BusinessKey(),
                           constants.FilterByBusinessKey(),
                           v -> addAdvancedSearchFilter(equalsTo(COLUMN_BUSINESSKEY,
                                                                 v)),
                           v -> removeAdvancedSearchFilter(equalsTo(COLUMN_BUSINESSKEY,
                                                                    v))
        );

        view.addTextFilter(constants.Type(),
                           constants.FilterByType(),
                           v -> addAdvancedSearchFilter(equalsTo(COLUMN_COMMANDNAME,
                                                                 v)),
                           v -> removeAdvancedSearchFilter(equalsTo(COLUMN_COMMANDNAME,
                                                                    v))
        );

        view.addTextFilter(constants.Process_Description(),
                           constants.FilterByProcessDescription(),
                           v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                 v)),
                           v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                    v))
        );

        final Map<String, String> status = new HashMap<>();
        status.put(RequestStatus.CANCELLED.name(),
                   constants.Cancelled());
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

        //TODO Missing process id and creation date

        final FilterSettings settings = view.getAdvancedSearchFilterSettings();
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_STATUS,
                                        RequestStatus.RUNNING.name()));
        settings.getDataSetLookup().addOperation(filter);
        view.saveAdvancedSearchFilterSettings(settings);

        view.addActiveFilter(constants.Status(),
                             constants.Running(),
                             RequestStatus.RUNNING.name(),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( REQUEST_LIST_DATASET );
        builder.setColumn( COLUMN_ID, constants.Id() );
        builder.setColumn(COLUMN_TIMESTAMP, constants.Time(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_COMMANDNAME, constants.CommandName(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_MESSAGE, constants.Message() );
        builder.setColumn( COLUMN_BUSINESSKEY, constants.Key() );
        builder.setColumn( COLUMN_RETRIES, constants.Retries() );
        builder.setColumn( COLUMN_EXECUTIONS, constants.Executions() );
        builder.setColumn( COLUMN_PROCESS_NAME, constants.Process_Name() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Description() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, SortOrder.DESCENDING );
        builder.tableWidth( 1000 );

        return builder.buildSettings();
    }

    private FilterSettings createStatusSettings(final RequestStatus status) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();
        builder.dataset( REQUEST_LIST_DATASET );
        if ( status != null ) {
            builder.filter( equalsTo( COLUMN_STATUS, status.name() ) );
        }
        builder.setColumn( COLUMN_ID, constants.Id() );
        builder.setColumn( COLUMN_TIMESTAMP, constants.Time(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_COMMANDNAME, constants.CommandName() );
        builder.setColumn( COLUMN_MESSAGE, constants.Message() );
        builder.setColumn( COLUMN_BUSINESSKEY, constants.Key() );
        builder.setColumn( COLUMN_RETRIES, constants.Retries() );
        builder.setColumn( COLUMN_EXECUTIONS, constants.Executions() );
        builder.setColumn( COLUMN_PROCESS_NAME, constants.Process_Name() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Description() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, SortOrder.DESCENDING );

        return builder.buildSettings();
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

    public FilterSettings createCancelledTabSettings() {
        return createStatusSettings(RequestStatus.CANCELLED);
    }
}