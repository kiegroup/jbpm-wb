/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.pr.backend.server;

import java.util.List;

import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.kie.internal.identity.IdentityProvider;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.pr.model.ProcessInstanceDataSetConstants.*;

/**
 * Ensure any Process Instances data set lookup call is constrained
 * according the process deployment ids the user may see.
 */
public class DeploymentIdsPreprocessor implements DataSetPreprocessor {

    @Inject
    DeploymentRolesManager deploymentRolesManager;

    @Inject
    IdentityProvider identityProvider;

    @Override
    public void preprocess(DataSetLookup lookup) {
        List<String> deploymentIds = deploymentRolesManager.getDeploymentsForUser(identityProvider);
        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(in(COLUMN_EXTERNAL_ID, deploymentIds));
        lookup.addOperation(0, filter);
    }
}
