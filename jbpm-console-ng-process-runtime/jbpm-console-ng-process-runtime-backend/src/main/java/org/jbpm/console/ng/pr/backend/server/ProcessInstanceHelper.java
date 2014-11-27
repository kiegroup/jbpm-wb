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
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.kie.api.runtime.process.NodeInstance;

public class ProcessInstanceHelper {
    public static Collection<ProcessInstanceSummary> adaptCollection(Collection<ProcessInstanceDesc> processInstances) {
        List<ProcessInstanceSummary> processInstancesSummary = new ArrayList<ProcessInstanceSummary>();
        for (ProcessInstanceDesc pi : processInstances) {
            processInstancesSummary.add(adapt(pi));
        }

        return processInstancesSummary;
    }

    public static ProcessInstanceSummary adapt(ProcessInstanceDesc processInstance) {
        ProcessInstanceSummary processInstanceSummary = new ProcessInstanceSummary(processInstance.getId(), processInstance.getProcessId(),
                processInstance.getDeploymentId(), processInstance.getProcessName(), processInstance.getProcessVersion(),
                processInstance.getState(), processInstance.getDataTimeStamp(), processInstance.getInitiator(),processInstance.getProcessInstanceDescription());

        List<UserTaskSummary> activeTasks = new ArrayList<UserTaskSummary>();
        if (processInstance.getActiveTasks() != null) {

            for (UserTaskInstanceDesc desc : processInstance.getActiveTasks()) {
                activeTasks.add(new UserTaskSummary(desc.getTaskId(), desc.getName(), desc.getActualOwner(), desc.getStatus()));
            }
        }
        processInstanceSummary.setActiveTasks(activeTasks);

        return processInstanceSummary;
    }

    public static Collection<String> collectActiveSignals(Collection<NodeInstance> activeNodes) {
        Collection<String> activeNodesComposite = new ArrayList<String>();
        for (NodeInstance nodeInstance : activeNodes) {
            if (nodeInstance instanceof EventNodeInstance) {
                String type = ((EventNodeInstance) nodeInstance).getEventNode().getType();
                if (type != null && !type.startsWith("Message-")) {
                    activeNodesComposite.add(type);
                }

            }
            if (nodeInstance instanceof CompositeNodeInstance) {
                Collection<NodeInstance> currentNodeInstances = ((CompositeNodeInstance) nodeInstance).getNodeInstances();

                // recursively check current nodes
                activeNodesComposite.addAll(collectActiveSignals(currentNodeInstances));
            }
        }

        return activeNodesComposite;
    }

}
