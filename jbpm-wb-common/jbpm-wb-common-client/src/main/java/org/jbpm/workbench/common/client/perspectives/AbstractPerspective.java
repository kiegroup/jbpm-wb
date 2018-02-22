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

package org.jbpm.workbench.common.client.perspectives;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.panels.impl.MultiScreenWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

public abstract class AbstractPerspective {

    @Inject
    protected UberfireDocks uberfireDocks;

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

    protected void setupDocks() {
        final String basicFiltersScreenId = getBasicFiltersScreenId();
        if (basicFiltersScreenId != null) {
            UberfireDock basicFiltersDock = new UberfireDock(UberfireDockPosition.WEST,
                                                             "fa-filter",
                                                             new DefaultPlaceRequest(basicFiltersScreenId),
                                                             getPerspectiveId()).withSize(400).withLabel(Constants.INSTANCE.Filters());
            uberfireDocks.add(basicFiltersDock);
        }
    }

    public abstract String getPerspectiveId();

    public abstract String getBasicFiltersScreenId();

    public abstract PlaceRequest getPlaceRequest();
}
