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

package org.jbpm.workbench.es.client.editors.requestlist;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.workbench.common.client.filters.advanced.AdvancedFiltersPresenter;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.jbpm.workbench.common.client.PerspectiveIds.JOB_LIST_ADVANCED_FILTERS_SCREEN;

@ApplicationScoped
@WorkbenchScreen(identifier = JOB_LIST_ADVANCED_FILTERS_SCREEN)
public class JobListAdvancedFiltersPresenter extends AdvancedFiltersPresenter {

    @Inject
    public void setFilterSettingsManager(final JobListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    @Override
    public String getAdvancedFiltersEditorScreenId (){
        return JOB_LIST_ADVANCED_FILTERS_SCREEN;
    }
}
