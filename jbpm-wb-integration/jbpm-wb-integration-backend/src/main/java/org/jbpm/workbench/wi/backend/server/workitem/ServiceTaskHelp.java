/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.workitem;

import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;

public class ServiceTaskHelp {

    public static ServiceTaskSummary createServiceTaskSummary(ServiceTask serviceTask) {
        return new ServiceTaskSummary(serviceTask.getId(), serviceTask.getIcon(), serviceTask.getName(),
                                      serviceTask.getDescription(), serviceTask.getAdditionalInfo(), serviceTask.getEnabled(),
                                      serviceTask.getInstalledOn(), serviceTask.getParameters(), serviceTask.getReferenceLink(),
                                      serviceTask.getInstalledOnBranch());
    }
}
