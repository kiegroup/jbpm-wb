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
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
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

        try {
            if (deploymentService.get() == null) {
                return handlers;
            }
            if (fs != null) {
                fs = fsIin.get();
            }
            DeployedUnit deployedUnit = deploymentService.get().getDeployedUnit(identifier);
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
        } catch (FileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return handlers;
    }

}
