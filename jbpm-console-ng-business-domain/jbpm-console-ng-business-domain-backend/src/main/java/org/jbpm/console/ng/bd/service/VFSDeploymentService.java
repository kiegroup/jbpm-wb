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

package org.jbpm.console.ng.bd.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jbpm.console.ng.bd.api.FileException;
import org.jbpm.console.ng.bd.api.FileService;
import org.jbpm.console.ng.bd.api.VFSDeploymentUnit;
import org.jbpm.console.ng.bd.api.Vfs;
import org.jbpm.kie.services.impl.AbstractDeploymentService;
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Vfs
public class VFSDeploymentService extends AbstractDeploymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(VFSDeploymentService.class);

    @Inject
    private BeanManager beanManager;
    @Inject
    private Instance<FileService> fileServiceIn;


    @Inject
    private IdentityProvider identityProvider; 
    @Inject
    private DefinitionService bpmn2Service;

    private FileService fs;

    @Override
    public void deploy(DeploymentUnit unit) {
        super.deploy(unit);
        if (!(unit instanceof VFSDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName());
        }
        
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
        VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
        // Create Runtime Manager Based on the Reference
        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(getEmf());

        AuditEventBuilder auditLoggerBuilder = setupAuditLogger(identityProvider, vfsUnit.getIdentifier());
        
        if (beanManager != null) {
            builder.registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, auditLoggerBuilder));
        }
        loadProcesses(vfsUnit, builder, deployedUnit);
        loadRules(vfsUnit, builder, deployedUnit); 
        
        
        commonDeploy(vfsUnit, deployedUnit, builder.get(), (KieContainer)builder.get().getEnvironment().get("kieContainer") );
    }


    
    protected void loadProcesses(VFSDeploymentUnit vfsUnit, RuntimeEnvironmentBuilder builder, DeployedUnitImpl deployedUnit) {
        Iterable<Path> loadProcessFiles = null;

        try {
            Path processFolder = getFs().getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
            loadProcessFiles = getFs().loadFilesByType(processFolder, ".+bpmn[2]?$");
        } catch (FileException ex) {
            logger.error("Error while loading process files", ex);
        }
        for (Path p : loadProcessFiles) {
            String processString = "";
            try {
                processString = new String(getFs().loadFile(p));
                builder.addAsset(ResourceFactory.newByteArrayResource(processString.getBytes()), ResourceType.BPMN2);
                ProcessAssetDesc process = (ProcessAssetDesc) bpmn2Service.buildProcessDefinition("unknown", processString, null, false);
                process.setOriginalPath(p.toUri().toString());
                process.setDeploymentId(vfsUnit.getIdentifier());
                deployedUnit.addAssetLocation(process.getId(), process);
                
            } catch (Exception ex) {
                logger.error("Error while reading process files", ex);
            }
        }
    }
    
    protected void loadRules(VFSDeploymentUnit vfsUnit, RuntimeEnvironmentBuilder builder, DeployedUnitImpl deployedUnit) {
        Iterable<Path> loadRuleFiles = null;

        try {
            Path rulesFolder = getFs().getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
            loadRuleFiles = getFs().loadFilesByType(rulesFolder, ".+drl");
        } catch (FileException ex) {
            logger.error("Error while loading rule files", ex);
        }
        for (Path p : loadRuleFiles) {
            String ruleString = "";
            try {
                ruleString = new String(getFs().loadFile(p));
                builder.addAsset(ResourceFactory.newByteArrayResource(ruleString.getBytes()), ResourceType.DRL);                
                
            } catch (Exception ex) {
                logger.error("Error while reading rule files", ex);
            }
        }
    }


    public FileService getFs() {
        if (fs == null) {
            fs = fileServiceIn.get();
        }
        return fs;
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public DefinitionService getBpmn2Service() {
        return bpmn2Service;
    }

    public void setBpmn2Service(DefinitionService bpmn2Service) {
        this.bpmn2Service = bpmn2Service;
    }

    public void activate(String deploymentId) {

    }

    public void deactivate(String deploymentId) {

    }
}
