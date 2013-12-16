/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.bd.util;

import javax.inject.Inject;
import org.jbpm.designer.service.BPMN2DataServices;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;

/**
 *
 * @author salaboy
 */
public class BPMN2DataServicesImpl implements BPMN2DataServices{

    
    @Inject
    RuntimeDataService dataService;

    @Override
    public String getProcessSources(String deploymentId, String processId) {
        ProcessAssetDesc processDesc = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        String encodedProcessSource = "";
        if(processDesc != null){
           encodedProcessSource = processDesc.getEncodedProcessSource();
        }
        return (encodedProcessSource == null)?"":encodedProcessSource;
    }
    
}
