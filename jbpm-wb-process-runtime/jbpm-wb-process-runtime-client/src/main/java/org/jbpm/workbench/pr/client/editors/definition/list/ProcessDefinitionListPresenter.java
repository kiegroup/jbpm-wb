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
package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.List;
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.list.AbstractScreenListPresenter;
import org.jbpm.workbench.common.client.list.ListView;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.common.model.PortableQueryFilter;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.PROCESS_DEFINITION_LIST_SCREEN)
public class ProcessDefinitionListPresenter extends AbstractScreenListPresenter<ProcessSummary> {

    @Inject
    PopupFormDisplayerView formDisplayPopUp;

    @Inject
    StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    private ProcessDefinitionListView view;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    protected AuthorizationManager authorizationManager;

    @Inject
    public void setAuthorizationManager(final AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
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

    @WorkbenchPartView
    public UberView<ProcessDefinitionListPresenter> getView() {
        return view;
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
                                final String processDefName) {

        ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(getSelectedServerTemplate(),
                                                                                            deploymentId,
                                                                                            processDefId,
                                                                                            processDefName),
                                                                   processDefName);

        formDisplayPopUp.setTitle(processDefName);

        startProcessDisplayProvider.setup(config,
                                          formDisplayPopUp);
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(Range visibleRange) {
        ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
        if (currentFilter == null) {
            currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                                                    visibleRange.getLength(),
                                                    false,
                                                    "",
                                                    columnSortList.size() > 0 ? columnSortList.get(0).getColumn().getDataStoreName() : "",
                                                    columnSortList.size() == 0 || columnSortList.get(0).isAscending());
        }
        // If we are refreshing after a search action, we need to go back to offset 0
        if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
            currentFilter.setOffset(visibleRange.getStart());
            currentFilter.setCount(visibleRange.getLength());
        } else {
            currentFilter.setOffset(0);
            currentFilter.setCount(view.getListGrid().getPageSize());
        }

        currentFilter.setOrderBy(columnSortList.size() > 0 ? columnSortList.get(0).getColumn().getDataStoreName() : "");
        currentFilter.setIsAscending(columnSortList.size() == 0 || columnSortList.get(0).isAscending());

        processRuntimeDataService.call(new RemoteCallback<List<ProcessSummary>>() {
                                           @Override
                                           public void callback(final List<ProcessSummary> processDefsSums) {
                                               boolean lastPageExactCount = processDefsSums.size() < visibleRange.getLength();
                                               updateDataOnCallback(processDefsSums,
                                                                    visibleRange.getStart(),
                                                                    visibleRange.getStart() + processDefsSums.size(),
                                                                    lastPageExactCount);
                                           }
                                       },
                                       new ErrorCallback<Message>() {
                                           @Override
                                           public boolean error(Message message,
                                                                Throwable throwable) {
                                               return onRuntimeDataServiceError();
                                           }
                                       }).getProcesses(getSelectedServerTemplate(),
                                                       visibleRange.getStart() / visibleRange.getLength(),
                                                       visibleRange.getLength(),
                                                       currentFilter.getOrderBy(),
                                                       currentFilter.isAscending());
    }

    boolean onRuntimeDataServiceError() {
        view.hideBusyIndicator();

        showErrorPopup(constants.ResourceCouldNotBeLoaded(commonConstants.Process_Definitions()));

        return false;
    }

    void showErrorPopup(final String message) {
        ErrorPopup.showMessage(message);
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                .endMenu()
                .build();
    }

    protected void selectProcessDefinition(final ProcessSummary processSummary) {
        setupDetailBreadcrumb(constants.ProcessDefinitionBreadcrumb(processSummary.getName()));
        placeManager.goTo(PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN);
        fireProcessDefSelectionEvent(processSummary);
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
        processInstanceSelected.fire(new ProcessInstanceSelectionEvent(newProcessInstance.getDeploymentId(),
                                                                       newProcessInstance.getNewProcessInstanceId(),
                                                                       newProcessInstance.getNewProcessDefId(),
                                                                       newProcessInstance.getProcessDefName(),
                                                                       newProcessInstance.getNewProcessInstanceStatus(),
                                                                       newProcessInstance.getServerTemplateId()));
    }

    public Predicate<ProcessSummary> getViewProcessInstanceActionCondition() {
        return pis -> isUserAuthorizedForPerspective(PROCESS_INSTANCES);
    }

    public Predicate<ProcessSummary> getStartCondition() {
        return processSummary -> !processSummary.isDynamic();
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

    public interface ProcessDefinitionListView extends ListView<ProcessSummary, ProcessDefinitionListPresenter> {

    }
}