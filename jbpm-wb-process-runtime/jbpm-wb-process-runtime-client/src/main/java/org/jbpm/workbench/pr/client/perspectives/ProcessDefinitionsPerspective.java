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
package org.jbpm.workbench.pr.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.workbench.common.client.perspectives.AbstractPerspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.jbpm.workbench.common.client.PerspectiveIds.*;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.PROCESS_DEFINITION_LIST_BASIC_FILTERS_SCREEN;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.PROCESS_DEFINITION_LIST_SAVED_FILTERS_SCREEN;

/**
 * A Perspective to show Process Definitions
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = PROCESS_DEFINITIONS)
public class ProcessDefinitionsPerspective extends AbstractPerspective {

    @Override
    public PlaceRequest getPlaceRequest() {
        return new DefaultPlaceRequest(PROCESS_DEFINITION_LIST_SCREEN);
    }

    @Override
    public String getPerspectiveId() {
        return PROCESS_DEFINITIONS;
    }

    @Override
    public String getBasicFiltersScreenId() {
        return PROCESS_DEFINITION_LIST_BASIC_FILTERS_SCREEN;
    }

    @Override
    public String getAdvancedFiltersScreenId() {
        return null;
    }

    @Override
    public String getSavedFiltersScreenId() {
        return PROCESS_DEFINITION_LIST_SAVED_FILTERS_SCREEN;
    }

    @Override
    public void onDetailsOpen(@Observes SelectPlaceEvent event) {
        if (event.getPlace().getIdentifier().equals(getDetailsScreenId())
                || event.getPlace().getIdentifier().equals(PROCESS_INSTANCE_DETAILS_SCREEN)) {
            uberfireDocks.hide(UberfireDockPosition.WEST, getPerspectiveId());
        }
    }

    @Override
    public void onDetailsClose(@Observes ClosePlaceEvent event) {
        if (event.getPlace().getIdentifier().equals(getDetailsScreenId())
                || event.getPlace().getIdentifier().equals(PROCESS_INSTANCE_DETAILS_SCREEN)) {
            uberfireDocks.show(UberfireDockPosition.WEST,
                               getPerspectiveId());
        }
    }

    @Override
    public String getDetailsScreenId() {
        return PROCESS_DEFINITION_DETAILS_SCREEN;
    }
}
