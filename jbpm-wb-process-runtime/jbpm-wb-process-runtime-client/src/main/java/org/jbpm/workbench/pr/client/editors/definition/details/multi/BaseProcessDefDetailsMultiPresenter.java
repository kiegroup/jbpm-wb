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

package org.jbpm.workbench.pr.client.editors.definition.details.multi;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.HTMLElement;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramUtil;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.jbpm.workbench.common.client.PerspectiveIds.SEARCH_PARAMETER_PROCESS_DEFINITION_ID;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCES;

public abstract class BaseProcessDefDetailsMultiPresenter<T extends BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView> implements RefreshMenuBuilder.SupportsRefresh {

    @Inject
    protected PopupFormDisplayerView formDisplayPopUp;

    @Inject
    protected T view;

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    protected MenuFactory.CustomMenuBuilder newInstanceMenu;

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest place;

    private String deploymentId = "";

    private String processId = "";

    private String processDefName = "";

    private String serverTemplateId = "";

    private boolean dynamic;

    @PostConstruct
    public void init() {
        newInstanceMenu = new MenuFactory.CustomMenuBuilder() {

            @Override
            public void push(MenuFactory.CustomMenuBuilder element) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<HTMLElement>() {
                    @Override
                    public HTMLElement build() {
                        return view.getNewInstanceButton();
                    }
                };
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public void onProcessSelectionEvent(@Observes final ProcessDefSelectionEvent event) {
        deploymentId = event.getDeploymentId();
        processId = event.getProcessId();
        processDefName = event.getProcessDefName();
        serverTemplateId = event.getServerTemplateId();
        dynamic = event.isDynamic();

        view.setNewInstanceButtonVisible(dynamic == false);

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                               String.valueOf(deploymentId) + " - " + processDefName));
    }

    public void createNewProcessInstance() {
        final ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(serverTemplateId,
                                                                                                  deploymentId,
                                                                                                  processId,
                                                                                                  processDefName),
                                                                         processDefName);

        formDisplayPopUp.setTitle(processDefName);
        startProcessDisplayProvider.setup(config,
                                          formDisplayPopUp);
    }

    public void goToProcessDefModelPopup() {
        if (place != null && !deploymentId.equals("")) {
            placeManager.goTo(ProcessDiagramUtil.buildPlaceRequest(serverTemplateId,
                                                                   deploymentId,
                                                                   processId));
        }
    }

    public void viewProcessInstances() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(PROCESS_INSTANCES);
        placeRequestImpl.addParameter(SEARCH_PARAMETER_PROCESS_DEFINITION_ID,
                                      processId);
        placeManager.goTo(placeRequestImpl);
    }

    @Override
    public void onRefresh() {
        processDefSelectionEvent.fire(new ProcessDefSelectionEvent(processId,
                                                                   deploymentId,
                                                                   serverTemplateId,
                                                                   processDefName,
                                                                   dynamic));
    }

    public void closeDetails() {
        placeManager.forceClosePlace(place);
    }

    public interface BaseProcessDefDetailsMultiView {

        HTMLElement getNewInstanceButton();

        void setNewInstanceButtonVisible(boolean visible);
    }
}
