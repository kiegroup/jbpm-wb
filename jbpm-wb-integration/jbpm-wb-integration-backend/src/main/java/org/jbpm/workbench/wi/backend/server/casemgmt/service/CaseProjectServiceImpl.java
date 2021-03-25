/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.casemgmt.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.model.Parameter;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@Service
@ApplicationScoped
public class CaseProjectServiceImpl implements CaseProjectService {

    protected static final String CASE_FILE_MARSHALLER = "org.jbpm.casemgmt.impl.marshalling.CaseMarshallerFactory.builder().withDoc().get();";
    protected static final String DOCUMENT_MARSHALLER = "new org.jbpm.document.marshalling.DocumentMarshallingStrategy();";
    protected static final String START_CASE_HANDLER = "new org.jbpm.casemgmt.impl.wih.StartCaseWorkItemHandler(ksession);";
    protected static final String START_CASE_WORK_ITEM = "StartCaseInstance";
    private static final Logger logger = LoggerFactory.getLogger(CaseProjectServiceImpl.class);
    private static final String CASE_PROJECT_DOT_FILE = ".caseproject";
    private static final String DEPLOYMENT_DESCRIPTOR_FILE = "kie-deployment-descriptor.xml";
    private static final String WORK_DEFINITION_FILE = "WorkDefinition.wid";
    private static final String GLOBAL_DIRECTORY = "global";
    private static final String BUSINESS_RULE_TASK_ICON = "defaultbusinessruletaskicon.png";
    private static final String EMAIL_ICON = "defaultemailicon.png";
    private static final String DECISION_TASKICON = "defaultdecisiontaskicon.png";
    private static final String LOG_ICON = "defaultlogicon.png";
    private static final String MILESTONE_ICON = "defaultmilestoneicon.png";
    private static final String SERVICE_ICON = "defaultservicenodeicon.png";
    private static final String REST_ICON = "defaultresticon.png";
    private static final String WEB_SERVICE_ICON = "defaultwebserviceicon.png";

    private IOService ioService;

    private DDEditorService ddEditorService;

    public CaseProjectServiceImpl() {
    }

    @Inject
    public CaseProjectServiceImpl(DDEditorService ddEditorService,
                                  @Named("ioStrategy") IOService ioService) {
        this.ddEditorService = ddEditorService;
        this.ioService = ioService;
    }

    @Override
    public void configureNewCaseProject(WorkspaceProject project) {
        logger.debug("configuring case project {}",
                     project);
        final KieModule kieModule = (KieModule) project.getMainModule();
        org.uberfire.backend.vfs.Path rootPath1 = project.getRootPath();
        final String separator = Paths.convert(rootPath1).getFileSystem().getSeparator();

        // add empty .caseproject file as marker if it does not exist already
        try {
            String caseProjectDotFilePathStr = project.getMainModule().getRootPath().toURI().toString() + CASE_PROJECT_DOT_FILE;
            Path caseProjectDotFilePath = ioService.get(new URI(caseProjectDotFilePathStr));
            if (!ioService.exists(caseProjectDotFilePath)) {
                ioService.write(caseProjectDotFilePath,
                                "");
                logger.debug("Added caseproject marker (dot) file at {}",
                             caseProjectDotFilePath);
            } else {
                logger.debug("Caseproject marker (dot) file already exists. Not adding it.");
            }
        } catch (Exception e) {
            logger.error("Unable to write caseproject marker (dot) file: {}",
                         e.getMessage());
        }

        String metaInfPath = Paths.convert(kieModule.getKModuleXMLPath()).getParent().toUri().toString();
        // setup kie-deployemnt-descriptor.xml
        String deploymentDescriptorPath = metaInfPath + separator + DEPLOYMENT_DESCRIPTOR_FILE;
        Path ddVFSPath = ioService.get(URI.create(deploymentDescriptorPath));

        org.uberfire.backend.vfs.Path convertedDDVFSPath = Paths.convert(ddVFSPath);
        if (!ioService.exists(ddVFSPath)) {
            ddEditorService.createIfNotExists(convertedDDVFSPath);
            logger.debug("Created deployment descriptor in {}",
                         convertedDDVFSPath);
        }
        logger.debug("Loading deployment descriptor from {}",
                     convertedDDVFSPath);
        DeploymentDescriptorModel descriptorModel = ddEditorService.load(convertedDDVFSPath);
        descriptorModel.setRuntimeStrategy("PER_CASE");
        List<ItemObjectModel> modelList = descriptorModel.getMarshallingStrategies();
        if (modelList == null) {
            modelList = new ArrayList<>();
        }
        modelList.add(new ItemObjectModel(null,
                                          CASE_FILE_MARSHALLER,
                                          "mvel",
                                          new ArrayList<Parameter>()));
        modelList.add(new ItemObjectModel(null,
                                          DOCUMENT_MARSHALLER,
                                          "mvel",
                                          new ArrayList<Parameter>()));
        descriptorModel.setMarshallingStrategies(modelList);

        List<ItemObjectModel> wiModelList = descriptorModel.getWorkItemHandlers();
        if (wiModelList == null) {
            wiModelList = new ArrayList<>();
        }
        wiModelList.add(new ItemObjectModel(START_CASE_WORK_ITEM,
                                            START_CASE_HANDLER,
                                            "mvel",
                                            new ArrayList<Parameter>()));
        descriptorModel.setWorkItemHandlers(wiModelList);

        Path globalPath = Paths.convert(kieModule.getKModuleXMLPath()).getRoot().resolve(GLOBAL_DIRECTORY);
        if (ioService.notExists(globalPath)) {
            ioService.createDirectory(globalPath);
        }

        logger.debug("Deployment descriptor model updated with case information {}",
                     descriptorModel);
        ddEditorService.save(convertedDDVFSPath,
                             descriptorModel,
                             null,
                             "Updated with case project configuration");
        logger.debug("Updated deployment model saved");

        logger.debug("Adding WorkDefinition.wid file to resources folder {} of the project {}",
                     globalPath,
                     project);
        addWorkDefinitions(kieModule, separator);

        logger.debug("Default WorkItemDefinitions added to {}",
                     globalPath.toString());
    }

    protected void addWorkDefinitions(KieModule kieModule, String separator) {
        String globalPath = Paths.convert(kieModule.getKModuleXMLPath())
                .getParent() // ROOT/src/main/META-INF
                .getParent() // ROOT/src/main/resources
                .getParent() // ROOT/src/main
                .getParent() // ROOT/src
                .getParent() // ROOT
                .toUri().toString() + GLOBAL_DIRECTORY;
        writeFile(globalPath, separator, WORK_DEFINITION_FILE);
        writeFile(globalPath, separator, BUSINESS_RULE_TASK_ICON);
        writeFile(globalPath, separator, EMAIL_ICON);
        writeFile(globalPath, separator, DECISION_TASKICON);
        writeFile(globalPath, separator, LOG_ICON);
        writeFile(globalPath, separator, MILESTONE_ICON);
        writeFile(globalPath, separator, SERVICE_ICON);
        writeFile(globalPath, separator, REST_ICON);
        writeFile(globalPath, separator, WEB_SERVICE_ICON);
    }

    private void writeFile(String location, String separator, String name) {
        try {
            byte[] data = IOUtils.toByteArray(this.getClass().getResourceAsStream("/" + name));
            Path filePath = ioService.get(URI.create(location + separator + name));
            ioService.write(filePath, data);
        } catch (IOException e) {
            logger.error("Error when writing WorkDefinition.wid file in {}",
                         location + separator + name,
                         e);
        }
    }
}
