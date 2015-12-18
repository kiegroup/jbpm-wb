/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.console.ng.bd.api.FileException;
import org.jbpm.console.ng.bd.api.FileService;
import org.jbpm.console.ng.bd.api.VFSDeploymentUnit;
import org.jbpm.console.ng.bd.api.Vfs;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.api.runtime.process.WorkItemHandler;

import org.uberfire.java.nio.file.Path;

public class VfsMVELWorkItemHandlerProducer implements WorkItemHandlerProducer {

    @Inject
    private Instance<FileService> fsIin;
    @Inject
    @Vfs
    private Instance<DeploymentService> deploymentService;

    private FileService fs;

    public VfsMVELWorkItemHandlerProducer() {
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        DeploymentService deployment = null;
        try {
            // proceed only if there is VFS based deployment service otherwise this producer should not be used
            if (deploymentService.isUnsatisfied()) {
                return handlers;
            }
            deployment = deploymentService.get();
            // if there is no file service already set try one from injection if available
            if (fs == null && !fsIin.isUnsatisfied()) {
                fs = fsIin.get();
            }
            // proceed only if both deployment service and file service is available and file service is active
            if (deployment != null && fs != null && fs.isActive()) {
                DeployedUnit deployedUnit = deployment.getDeployedUnit(identifier);
                if (deployedUnit == null) {
                    return handlers;
                }
                VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) deployedUnit.getDeploymentUnit();
                Path assetFolder = fs.getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
                if (identifier == null || !fs.exists(assetFolder)) {
                    return handlers;
                }
                params.put("fs", fs);

                Iterable<Path> widFiles = fs.loadFilesByType(assetFolder, "conf");

                for (Path widPath : widFiles) {
                    String content = new String(fs.loadFile(widPath), "UTF-8");

                    handlers.putAll((Map<String, WorkItemHandler>) MVELSafeHelper.getEvaluator().eval( content, params ));
                }
            }
        } catch (FileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return handlers;
    }

}
