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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.eventlisteners.DeploymentsEventListenersPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general.DeploymentsGeneralSettingsPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals.DeploymentsGlobalsPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.marshallingstrategies.DeploymentsMarshallingStrategiesPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.requiredroles.DeploymentsRequiredRolesPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;

@Dependent
public class DeploymentsSections {

    private final DeploymentsGeneralSettingsPresenter deploymentsGeneralSettingsPresenter;
    private final DeploymentsMarshallingStrategiesPresenter deploymentsMarshallingStrategiesPresenter;
    private final DeploymentsGlobalsPresenter deploymentsGlobalsPresenter;
    private final DeploymentsEventListenersPresenter deploymentsEventListenersPresenter;
    private final DeploymentsRequiredRolesPresenter deploymentsRequiredRolesPresenter;

    @Inject
    public DeploymentsSections(final DeploymentsGeneralSettingsPresenter deploymentsGeneralSettingsPresenter,
                               final DeploymentsMarshallingStrategiesPresenter deploymentsMarshallingStrategiesPresenter,
                               final DeploymentsGlobalsPresenter deploymentsGlobalsPresenter,
                               final DeploymentsEventListenersPresenter deploymentsEventListenersPresenter,
                               final DeploymentsRequiredRolesPresenter deploymentsRequiredRolesPresenter) {

        this.deploymentsGeneralSettingsPresenter = deploymentsGeneralSettingsPresenter;
        this.deploymentsMarshallingStrategiesPresenter = deploymentsMarshallingStrategiesPresenter;
        this.deploymentsGlobalsPresenter = deploymentsGlobalsPresenter;
        this.deploymentsEventListenersPresenter = deploymentsEventListenersPresenter;
        this.deploymentsRequiredRolesPresenter = deploymentsRequiredRolesPresenter;
    }

    public List<Section<DeploymentDescriptorModel>> getList() {
        return Arrays.asList(
                deploymentsGeneralSettingsPresenter,
                deploymentsMarshallingStrategiesPresenter,
                deploymentsGlobalsPresenter,
                deploymentsEventListenersPresenter,
                deploymentsRequiredRolesPresenter
        );
    }
}
