/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.Range;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;

import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "DataSet Process Instance List With Variables")
public class DataSetProcessInstanceWithVariablesListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {

    public interface DataSetProcessInstanceWithVariablesListView extends ListView<ProcessInstanceSummary, DataSetProcessInstanceWithVariablesListPresenter> {

        public int getRefreshValue();

        public void restoreTabs();

        public void saveRefreshValue(int newValue);

        public FilterSettings getVariablesTableSettings(String processName);

        public void addDomainSpecifColumns(ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable, Set<String> columns);
    }

    @Inject
    private DataSetProcessInstanceWithVariablesListView view;

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private ErrorPopupPresenter errorPopup;

    public Button menuActionsButton;
    private PopupPanel popup = new PopupPanel(true);

    public Button menuRefreshButton = new Button();
    public Button menuResetTabsButton = new Button();

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    private Constants constants = GWT.create(Constants.class);

    public DataSetProcessInstanceWithVariablesListPresenter() {
        super();
    }

    public void filterGrid(FilterSettings tableSettings) {
        dataSetQueryHelper.setCurrentTableSetting(tableSettings);
        refreshGrid();
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(final Range visibleRange) {
        try {
            final FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            if (currentTableSettings != null) {
                currentTableSettings.setTablePageSize(view.getListGrid().getPageSize());
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                //GWT.log( "processInstances getData "+columnSortList.size() +"currentTableSettings table name "+ currentTableSettings.getTableName() );
                if (columnSortList != null && columnSortList.size() > 0) {
                    dataSetQueryHelper.setLastOrderedColumn((columnSortList.size() > 0) ? columnSortList.get(0).getColumn().getDataStoreName() : "");
                    dataSetQueryHelper.setLastSortOrder((columnSortList.size() > 0) && columnSortList.get(0).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
                } else {
                    dataSetQueryHelper.setLastOrderedColumn(DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_START);
                    dataSetQueryHelper.setLastSortOrder(SortOrder.ASCENDING);
                }
                dataSetQueryHelper.setDataSetHandler(currentTableSettings);
                dataSetQueryHelper.lookupDataSet(visibleRange.getStart(), new DataSetReadyCallback() {
                    @Override
                    public void callback(DataSet dataSet) {
                        if (dataSet != null) {
                            final List<ProcessInstanceSummary> myProcessInstancesFromDataSet = new ArrayList<ProcessInstanceSummary>();

                            for (int i = 0; i < dataSet.getRowCount(); i++) {
                                GWT.log(" PID: "+dataSetQueryHelper.getColumnLongValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSINSTANCEID, i)+"- ");
                                GWT.log(" Process Name: "+dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSID, i)+"- ");
                                myProcessInstancesFromDataSet.add(new ProcessInstanceSummary(
                                        dataSetQueryHelper.getColumnLongValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSINSTANCEID, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSID, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_EXTERNALID, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSNAME, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSVERSION, i),
                                        dataSetQueryHelper.getColumnIntValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_STATUS, i),
                                        dataSetQueryHelper.getColumnDateValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_START, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_IDENTITY, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION, i),
                                        dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_CORRELATIONKEY, i),
                                        dataSetQueryHelper.getColumnLongValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PARENTPROCESSINSTANCEID, i)));

                            }
                            List<DataSetOp> ops = currentTableSettings.getDataSetLookup().getOperationList();
                            String filterValue = null;
                            for (DataSetOp dataSetOp : ops) {
                                if (dataSetOp.getType().equals(DataSetOpType.FILTER)) {
                                    List<ColumnFilter> filters = ((DataSetFilter) dataSetOp).getColumnFilterList();
                                    for (ColumnFilter filter : filters) {
                                        CoreFunctionFilter coreFilter = ((CoreFunctionFilter) filter);
//                                        GWT.log("ProcessID filter filterColum" + coreFilter.getColumnId());
//                                        GWT.log("ProcessID filter type filterColum" + coreFilter.getType().toString());
//                                        GWT.log("ProcessID filter type filterColum" + coreFilter.getParameters());
                                        if (filter.getColumnId().equals("processId")) {

                                            List parameters = coreFilter.getParameters();
                                            if (parameters.size() > 0) {
                                                filterValue = parameters.get(0).toString();
                                            }
                                        }
                                    }
                                }
                            }
                            if (filterValue != null) {
                                final int rowCountNotTrimmed = dataSet.getRowCountNonTrimmed();
                                dataSetQueryHelper.setDataSetHandler(view.getVariablesTableSettings(filterValue));

                                dataSetQueryHelper.setLastOrderedColumn("pname");
                                dataSetQueryHelper.setLastSortOrder(SortOrder.ASCENDING);
                                dataSetQueryHelper.lookupDataSet(0, new DataSetReadyCallback() {
                                    @Override
                                    public void callback(DataSet dataSet) {

                                        Set<String> columns = new HashSet<String>();
                                        for (int i = 0; i < dataSet.getRowCount(); i++) {
                                            Long processInstanceId = dataSetQueryHelper.getColumnLongValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.PROCESS_INSTANCE_ID, i);
                                            String variableName = dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.VARIABLE_NAME, i);
                                            String variableValue = dataSetQueryHelper.getColumnStringValue(dataSet, DataSetProcessInstanceWithVariablesListViewImpl.VARIABLE_VALUE, i);
                                            GWT.log("processInstanceId: "+processInstanceId + " - "+ "Variable Name: "+variableName + " - "+ "Variable Value: "+variableValue);

                                            for (ProcessInstanceSummary pis : myProcessInstancesFromDataSet) {
                                                if (pis.getProcessInstanceId().equals(processInstanceId)) {
                                                    pis.addDomainData(variableName, variableValue);
                                                    columns.add(variableName);
                                                }

                                            }

                                        }
                                        view.addDomainSpecifColumns(view.getListGrid(), columns);

                                        PageResponse<ProcessInstanceSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceSummary>();
                                        processInstanceSummaryPageResponse.setPageRowList(myProcessInstancesFromDataSet);
                                        processInstanceSummaryPageResponse.setStartRowIndex(visibleRange.getStart());
                                        processInstanceSummaryPageResponse.setTotalRowSize(rowCountNotTrimmed);
                                        processInstanceSummaryPageResponse.setTotalRowSizeExact(true);
                                        if ( visibleRange.getStart() + myProcessInstancesFromDataSet.size() == rowCountNotTrimmed ) {
                                            processInstanceSummaryPageResponse.setLastPage(true);
                                        } else {
                                            processInstanceSummaryPageResponse.setLastPage(false);
                                        }
                                        DataSetProcessInstanceWithVariablesListPresenter.this.updateDataOnCallback(processInstanceSummaryPageResponse);
                                    }

                                    @Override
                                    public void notFound() {
                                        view.hideBusyIndicator();
                                        errorPopup.showMessage("Not found DataSet with UUID [  variables ] ");
                                        GWT.log("DataSet with UUID [  variables ] not found.");
                                    }

                                    @Override
                                    public boolean onError(final ClientRuntimeError error) {
                                        view.hideBusyIndicator();
                                        errorPopup.showMessage("DataSet with UUID [  variables ] error: " + error.getThrowable());
                                        GWT.log("DataSet with UUID [  variables ] error: ", error.getThrowable());
                                        return false;
                                    }
                                }
                                );
                            } else {
                                PageResponse<ProcessInstanceSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceSummary>();
                                processInstanceSummaryPageResponse.setPageRowList(myProcessInstancesFromDataSet);
                                processInstanceSummaryPageResponse.setStartRowIndex(visibleRange.getStart());
                                processInstanceSummaryPageResponse.setTotalRowSize(dataSet.getRowCountNonTrimmed());
                                processInstanceSummaryPageResponse.setTotalRowSizeExact(true);
                                if (visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed()) {
                                    processInstanceSummaryPageResponse.setLastPage(true);
                                } else {
                                    processInstanceSummaryPageResponse.setLastPage(false);
                                }
                                DataSetProcessInstanceWithVariablesListPresenter.this.updateDataOnCallback(processInstanceSummaryPageResponse);
                            }

                        }

                        view.hideBusyIndicator();
                    }

                    @Override
                    public void notFound() {
                        view.hideBusyIndicator();
                        errorPopup.showMessage("Not found DataSet with UUID [  jbpmProcessInstances ] ");
                        GWT.log("DataSet with UUID [  jbpmProcessInstances ] not found.");
                    }

                    @Override
                    public boolean onError(final ClientRuntimeError error) {
                        view.hideBusyIndicator();
                        errorPopup.showMessage("DataSet with UUID [  jbpmProcessInstances ] error: " + error.getThrowable());
                        GWT.log("DataSet with UUID [  jbpmProcessInstances ] error: ", error.getThrowable());
                        return false;
                    }
                });

            } else {
                view.hideBusyIndicator();
            }
        } catch (Exception e) {
            GWT.log("Error looking up dataset with UUID [ jbpmProcessInstances ]");
        }

    }

    public void newInstanceCreated(@Observes NewProcessInstanceEvent pi) {
        refreshGrid();
    }

    public void newInstanceCreated(@Observes ProcessInstancesUpdateEvent pis) {
        refreshGrid();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnFocus
    public void onFocus() {
        refreshGrid();
    }

    @OnOpen
    public void onOpen() {
        refreshGrid();
    }

    public void abortProcessInstance(long processInstanceId) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).abortProcessInstance(processInstanceId);
    }

    public void abortProcessInstance(List<Long> processInstanceIds) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).abortProcessInstances(processInstanceIds);
    }

    public void suspendProcessInstance(String processDefId,
            long processInstanceId) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshGrid();

            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).suspendProcessInstance(processInstanceId);
    }

    public void bulkSignal(List<ProcessInstanceSummary> processInstances) {
        StringBuilder processIdsParam = new StringBuilder();
        if (processInstances != null) {

            for (ProcessInstanceSummary selected : processInstances) {
                if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                    view.displayNotification(constants.Signaling_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                            + ")");
                    continue;
                }
                processIdsParam.append(selected.getId() + ",");
            }
            // remove last ,
            if (processIdsParam.length() > 0) {
                processIdsParam.deleteCharAt(processIdsParam.length() - 1);
            }
        } else {
            processIdsParam.append("-1");
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
        placeRequestImpl.addParameter("processInstanceId", processIdsParam.toString());

        placeManager.goTo(placeRequestImpl);
        view.displayNotification(constants.Signaling_Process_Instance());

    }

    public void bulkAbort(List<ProcessInstanceSummary> processInstances) {
        if (processInstances != null) {
            if (Window.confirm("Are you sure that you want to abort the selected process instances?")) {
                List<Long> ids = new ArrayList<Long>();
                for (ProcessInstanceSummary selected : processInstances) {
                    if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                        view.displayNotification(constants.Aborting_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                                + ")");
                        continue;
                    }
                    ids.add(selected.getProcessInstanceId());

                    view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")");
                }
                abortProcessInstance(ids);

            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<DataSetProcessInstanceWithVariablesListPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        setupButtons();

        return MenuFactory
                .newTopLevelMenu(Constants.INSTANCE.New_Process_Instance())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        newProcessInstancePopup.show();
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                menuRefreshButton.addClickHandler(new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        refreshGrid();
                                    }
                                });
                                return menuRefreshButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled(boolean enabled) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceListPresenter#menuRefreshButton";
                            }

                        };
                    }
                }).endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return menuActionsButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled(boolean enabled) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceList#menuActionsButton";
                            }

                        };
                    }
                }).endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                menuResetTabsButton.addClickHandler(new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        view.restoreTabs();
                                    }
                                });
                                return menuResetTabsButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled(boolean enabled) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceList#menuResetTabsButton";
                            }

                        };
                    }
                }).endMenu()
                .build();

    }

    public void setupButtons() {
        menuActionsButton = new Button();
        createRefreshToggleButton(menuActionsButton);

        menuRefreshButton.setIcon(IconType.REFRESH);
        menuRefreshButton.setSize(ButtonSize.MINI);
        menuRefreshButton.setTitle(Constants.INSTANCE.Refresh());

        menuResetTabsButton.setIcon(IconType.TH_LIST);
        menuResetTabsButton.setSize(ButtonSize.MINI);
        menuResetTabsButton.setTitle(Constants.INSTANCE.RestoreDefaultFilters());
    }

    public void createRefreshToggleButton(final Button refreshIntervalSelector) {

        refreshIntervalSelector.setToggle(true);
        refreshIntervalSelector.setIcon(IconType.COG);
        refreshIntervalSelector.setTitle(Constants.INSTANCE.AutoRefresh());
        refreshIntervalSelector.setSize(ButtonSize.MINI);

        popup.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
        popup.addAutoHidePartner(refreshIntervalSelector.getElement());
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (popupPanelCloseEvent.isAutoClosed()) {
                    refreshIntervalSelector.setActive(false);
                }
            }
        });

        refreshIntervalSelector.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!refreshIntervalSelector.isActive()) {
                    showSelectRefreshIntervalPopup(refreshIntervalSelector.getAbsoluteLeft() + refreshIntervalSelector.getOffsetWidth(),
                            refreshIntervalSelector.getAbsoluteTop() + refreshIntervalSelector.getOffsetHeight(), refreshIntervalSelector);
                } else {
                    popup.hide(false);
                }
            }
        });

    }

    private void showSelectRefreshIntervalPopup(final int left,
            final int top,
            final Button refreshIntervalSelector) {
        VerticalPanel popupContent = new VerticalPanel();

        //int configuredSeconds = presenter.getAutoRefreshSeconds();
        int configuredSeconds = view.getRefreshValue();
        if (configuredSeconds > 0) {
            updateRefreshInterval(true, configuredSeconds);
        } else {
            updateRefreshInterval(false, 0);
        }

        RadioButton oneMinuteRadioButton = createTimeSelectorRadioButton(60, "1 Minute", configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton fiveMinuteRadioButton = createTimeSelectorRadioButton(300, "5 Minutes", configuredSeconds, refreshIntervalSelector, popupContent);
        RadioButton tenMinuteRadioButton = createTimeSelectorRadioButton(600, "10 Minutes", configuredSeconds, refreshIntervalSelector, popupContent);

        popupContent.add(oneMinuteRadioButton);
        popupContent.add(fiveMinuteRadioButton);
        popupContent.add(tenMinuteRadioButton);

        Button resetButton = new Button(Constants.INSTANCE.Disable());
        resetButton.setSize(ButtonSize.MINI);
        resetButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateRefreshInterval(false, 0);
                view.saveRefreshValue(0);
                refreshIntervalSelector.setActive(false);
                popup.hide();
            }
        });

        popupContent.add(resetButton);

        popup.setWidget(popupContent);
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition(finalLeft, top);

    }

    private RadioButton createTimeSelectorRadioButton(int time, String name, int configuredSeconds, final Button refreshIntervalSelector, VerticalPanel popupContent) {
        RadioButton oneMinuteRadioButton = new RadioButton("refreshInterval", name);
        oneMinuteRadioButton.setText(name);
        final int selectedRefreshTime = time;
        if (configuredSeconds == selectedRefreshTime) {
            oneMinuteRadioButton.setValue(true);
        }

        oneMinuteRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateRefreshInterval(true, selectedRefreshTime);
                view.saveRefreshValue(selectedRefreshTime);
                refreshIntervalSelector.setActive(false);
                popup.hide();

            }
        });
        return oneMinuteRadioButton;
    }
}
