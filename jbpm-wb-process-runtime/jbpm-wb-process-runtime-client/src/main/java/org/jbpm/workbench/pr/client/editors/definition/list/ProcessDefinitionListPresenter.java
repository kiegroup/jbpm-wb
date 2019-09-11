/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.client.list.MultiGridView;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.NewCaseInstanceEvent;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.jbpm.workbench.common.client.util.DataSetUtils.getColumnStringValue;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.PROCESS_DEFINITION_LIST_SCREEN)
public class ProcessDefinitionListPresenter extends AbstractMultiGridPresenter<ProcessSummary,ProcessDefinitionListPresenter.ProcessDefinitionListView> {

    @Inject
    PopupFormDisplayerView formDisplayPopUp;

    @Inject
    StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @Inject
    private DefaultWorkbenchErrorCallback errorCallback;

    protected ProcessDefinitionListBasicFiltersPresenter processDefinitionListBasicFiltersPresenter;

    @Inject
    public void setAuthorizationManager(final AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Inject
    public void setProcessDefinitionListBasicFiltersPresenter(final ProcessDefinitionListBasicFiltersPresenter processDefinitionListBasicFiltersPresenter){
        this.processDefinitionListBasicFiltersPresenter = processDefinitionListBasicFiltersPresenter;
    }

    @Override
    public void setupActiveSearchFilters() {

    }

    @Override
    public boolean existActiveSearchFilters() {
        return false;
    }

    @Override
    protected void selectSummaryItem(ProcessSummary processSummary) {
        setupDetailBreadcrumb(constants.ProcessDefinitionBreadcrumb(processSummary.getName()));
        placeManager.goTo(PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN);
        fireProcessDefSelectionEvent(processSummary);
    }

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelected;

    private Constants constants = Constants.INSTANCE;

    private final org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;

    public ProcessDefinitionListPresenter() {
        super();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            commonConstants.Manage_Process_Definitions());
    }

    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              commonConstants.Manage_Process_Definitions(),
                              detailLabel,
                              PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN);
    }

    public void openGenericForm(final String processDefId,
                                final String deploymentId,
                                final String processDefName,
                                final boolean isDynamic) {

        ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(getSelectedServerTemplate(),
                                                                                            deploymentId,
                                                                                            processDefId,
                                                                                            processDefName),
                                                                   processDefName,
                                                                   isDynamic);

        formDisplayPopUp.setTitle(processDefName);

        startProcessDisplayProvider.setup(config,
                                          formDisplayPopUp);
    }

    @Inject
    public void setFilterSettingsManager(final ProcessDefinitionListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    private final List<ProcessSummary> myProcessDefinitionsFromDataSet = new ArrayList<ProcessSummary>();
    @Override
    protected DataSetReadyCallback getDataSetReadyCallback(Integer startRange, FilterSettings tableSettings) {
        return errorHandlerBuilder.get().withUUID(tableSettings.getUUID()).withDataSetCallback(
                dataSet -> {
                    if (dataSet != null && dataSetQueryHelper.getCurrentTableSettings().getKey().equals(tableSettings.getKey())) {
                        myProcessDefinitionsFromDataSet.clear();
                        for (int i = 0; i < dataSet.getRowCount(); i++) {
                            myProcessDefinitionsFromDataSet.add(createProcessSummaryFromDataSet(dataSet, i));
                        }

                        boolean lastPage = false;
                        if (dataSet.getRowCount() < view.getListGrid().getPageSize()) {
                            lastPage = true;
                        }

                        updateDataOnCallback(myProcessDefinitionsFromDataSet,
                                             startRange,
                                             startRange + myProcessDefinitionsFromDataSet.size(),
                                             lastPage);
                    }
                    view.hideBusyIndicator();
                })
                .withEmptyResultsCallback(() -> setEmptyResults());
    }

    private ProcessSummary createProcessSummaryFromDataSet(DataSet dataSet, int index) {
        return new ProcessSummary(getColumnStringValue(dataSet, COL_ID_PROCESSDEF, index),
                                  getColumnStringValue(dataSet, COL_ID_PROCESSNAME, index),
                                  getColumnStringValue(dataSet, COL_ID_PROJECT, index),
                                  getColumnStringValue(dataSet, COL_ID_PROCESSVERSION, index),
                                  Boolean.valueOf(getColumnStringValue(dataSet, COL_DYNAMIC, index)));
    }

    boolean onRuntimeDataServiceError(final Throwable throwable) {
        setEmptyResults();
        errorCallback.error(throwable);
        return false;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory
                                     .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                                     .endMenu()
                                     .build());
    }

    private void fireProcessDefSelectionEvent(final ProcessSummary processSummary) {
        processDefSelected.fire(new ProcessDefSelectionEvent(processSummary.getProcessDefId(),
                                                             processSummary.getDeploymentId(),
                                                             getSelectedServerTemplate(),
                                                             processSummary.getProcessDefName(),
                                                             processSummary.isDynamic()));
    }

    public void refreshNewProcessInstance(@Observes NewProcessInstanceEvent newProcessInstance) {
        setupDetailBreadcrumb(placeManager,
                              commonConstants.Manage_Process_Definitions(),
                              constants.ProcessInstanceBreadcrumb(newProcessInstance.getNewProcessInstanceId()),
                              PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN);
        placeManager.goTo(PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN);
        processInstanceSelected.fire(new ProcessInstanceSelectionEvent(newProcessInstance.getServerTemplateId(),
                                                                       newProcessInstance.getDeploymentId(),
                                                                       newProcessInstance.getNewProcessInstanceId(),
                                                                       false));
    }

    public void refreshNewCaseInstance(@Observes NewCaseInstanceEvent newCaseInstance) {

        processRuntimeDataService.call((ProcessInstanceSummary newProcessInstance) -> {
                                           setupDetailBreadcrumb(placeManager,
                                                                 commonConstants.Manage_Process_Definitions(),
                                                                 constants.ProcessInstanceBreadcrumb(newProcessInstance.getId()),
                                                                 PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN);
                                           placeManager.goTo(PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN);
                                           processInstanceSelected.fire(new ProcessInstanceSelectionEvent(newProcessInstance.getServerTemplateId(),
                                                                                                          newProcessInstance.getDeploymentId(),
                                                                                                          newProcessInstance.getId(),
                                                                                                          false));
                                       },
                                       (Message message, Throwable throwable) -> onRuntimeDataServiceError(throwable)
        ).getProcessInstanceByCorrelationKey(newCaseInstance.getServerTemplateId(), newCaseInstance.getNewCaseId());
    }

    public Predicate<ProcessSummary> getViewProcessInstanceActionCondition() {
        return pis -> isUserAuthorizedForPerspective(PROCESS_INSTANCES);
    }

    public Predicate<ProcessSummary> getStartCondition() {
        return processSummary -> !processSummary.isDynamic() || (processSummary.isDynamic() && processSummary.isDynamicFormsEnabled());
    }

    public void viewProcessInstances(String processDefId) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(PROCESS_INSTANCES);
        placeRequestImpl.addParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID,
                                      processDefId);
        placeManager.goTo(placeRequestImpl);
    }

    public boolean isUserAuthorizedForPerspective(final String perspectiveId) {
        final ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                        ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              identity);
    }

    @Inject
    public void setProcessRuntimeDataService(final Caller<ProcessRuntimeDataService> processRuntimeDataService) {
        this.processRuntimeDataService = processRuntimeDataService;
    }

    public interface ProcessDefinitionListView extends MultiGridView<ProcessSummary, ProcessDefinitionListPresenter> {

    }

}
