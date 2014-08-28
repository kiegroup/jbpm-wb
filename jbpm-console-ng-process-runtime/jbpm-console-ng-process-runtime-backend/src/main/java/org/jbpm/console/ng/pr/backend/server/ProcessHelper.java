/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.model.ProcessDefinition;

public class ProcessHelper {
    public static Collection<ProcessSummary> adaptCollection(Collection<ProcessDefinition> processes) {
        List<ProcessSummary> processesSummary = new ArrayList<ProcessSummary>();
        for (ProcessDefinition p : processes) {
            processesSummary.add(new ProcessSummary(p.getId(), p.getName(), ((ProcessAssetDesc)p).getDeploymentId(), p.getPackageName(), p
                    .getType(), p.getVersion(), p.getOriginalPath(), ((ProcessAssetDesc)p).getEncodedProcessSource()));
        }

        return processesSummary;
    }

    public static ProcessSummary adapt(ProcessDefinition p) {
        if (p == null) {
            return null;
        }
        return new ProcessSummary(p.getId(), p.getName(), ((ProcessAssetDesc)p).getDeploymentId(), p.getPackageName(), p.getType(), p.getVersion(),
                p.getOriginalPath(), ((ProcessAssetDesc)p).getEncodedProcessSource());
    }

}
