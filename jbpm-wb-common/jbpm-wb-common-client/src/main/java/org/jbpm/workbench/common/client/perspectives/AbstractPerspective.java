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

package org.jbpm.workbench.common.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.impl.MultiScreenWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

public abstract class AbstractPerspective {

    @Inject
    protected UberfireDocks uberfireDocks;

    protected UberfireDock basicFiltersDock;

    protected UberfireDock advancedFiltersDock;

    protected UberfireDock savedFiltersDock;

    private PlaceRequest placeRequest;

    @PostConstruct
    protected void init() {
        placeRequest = getPlaceRequest();
        setupDocks();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(MultiScreenWorkbenchPanelPresenter.class.getName());
        p.setName(getPerspectiveId());
        p.getRoot().addPart(new PartDefinitionImpl(placeRequest));
        return p;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        placeRequest.getParameters().clear();
        for (final String param : place.getParameterNames()) {
            placeRequest.addParameter(param,
                                      place.getParameter(param,
                                                         null));
        }
    }

    @OnOpen
    public void onOpen() {
        if (basicFiltersDock != null) {
            Scheduler.get().scheduleDeferred(() -> {
                uberfireDocks.show(UberfireDockPosition.WEST,
                                   getPerspectiveId());
                uberfireDocks.open(basicFiltersDock);
            });
        }
    }

    public void onDetailsOpen(@Observes SelectPlaceEvent event) {
        if(event.getPlace().getIdentifier().equals(getDetailsScreenId())){
            uberfireDocks.hide(UberfireDockPosition.WEST,
                               getPerspectiveId());
        }
    }

    public void onDetailsClose(@Observes ClosePlaceEvent event) {
        if(event.getPlace().getIdentifier().equals(getDetailsScreenId())){
            uberfireDocks.show(UberfireDockPosition.WEST,
                               getPerspectiveId());
        }
        if(event.getPlace().getIdentifier().equals(getAdvancedFiltersScreenId())){
            uberfireDocks.close(advancedFiltersDock);
        }
    }

    protected void setupDocks() {
        final String basicFiltersScreenId = getBasicFiltersScreenId();
        if (basicFiltersScreenId != null) {
            basicFiltersDock = new UberfireDock(UberfireDockPosition.WEST,
                                                IconType.FILTER.toString(),
                                                new DefaultPlaceRequest(basicFiltersScreenId),
                                                getPerspectiveId()).withSize(400).withLabel(Constants.INSTANCE.Filters());
            uberfireDocks.add(basicFiltersDock);
        }

        final String advancedFiltersScreenId = getAdvancedFiltersScreenId();
        if (advancedFiltersScreenId != null) {
            advancedFiltersDock = new UberfireDock(UberfireDockPosition.WEST,
                                                   IconType.PLUS_SQUARE_O.toString(),
                                                   new DefaultPlaceRequest(advancedFiltersScreenId),
                                                   getPerspectiveId()).withSize(500).withLabel(Constants.INSTANCE.AdvancedFilters());
            uberfireDocks.add(advancedFiltersDock);
        }

        final String savedFiltersScreenId = getSavedFiltersScreenId();
        if (savedFiltersScreenId != null) {
            savedFiltersDock = new UberfireDock(UberfireDockPosition.WEST,
                                                IconType.STAR_O.toString(),
                                                new DefaultPlaceRequest(savedFiltersScreenId),
                                                getPerspectiveId()).withSize(400).withLabel(Constants.INSTANCE.SavedFilters());
            uberfireDocks.add(savedFiltersDock);
        }
    }

    public abstract String getPerspectiveId();

    public abstract String getBasicFiltersScreenId();

    public abstract String getAdvancedFiltersScreenId();

    public abstract String getSavedFiltersScreenId();

    public abstract String getDetailsScreenId();

    public abstract PlaceRequest getPlaceRequest();
}