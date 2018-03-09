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
package org.jbpm.workbench.pr.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.workbench.common.client.perspectives.AbstractPerspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.jbpm.workbench.common.client.PerspectiveIds.*;

@ApplicationScoped
@WorkbenchPerspective(identifier = PROCESS_INSTANCES)
public class ProcessInstanceListPerspective extends AbstractPerspective {

    @Override
    public PlaceRequest getPlaceRequest() {
        return new DefaultPlaceRequest(PROCESS_INSTANCE_LIST_SCREEN);
    }

    @Override
    public String getPerspectiveId() {
        return PROCESS_INSTANCES;
    }

    @Override
    public String getBasicFiltersScreenId() {
        return PROCESS_INSTANCE_LIST_BASIC_FILTERS_SCREEN;
    }

    @Override
    public String getSavedFiltersScreenId() {
        return PROCESS_INSTANCE_LIST_SAVED_FILTERS_SCREEN;
    }

    @Override
    public String getDetailsScreenId() {
        return PROCESS_INSTANCE_DETAILS_SCREEN;
    }
}
