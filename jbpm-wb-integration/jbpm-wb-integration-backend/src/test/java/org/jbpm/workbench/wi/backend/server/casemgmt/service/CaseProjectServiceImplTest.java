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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseProjectServiceImplTest {

    @Mock
    IOService ioService;

    @Mock
    DDEditorService ddEditorService;

    @Mock
    org.uberfire.backend.vfs.Path kmodulePath;

    @Mock
    org.uberfire.backend.vfs.Path projectPath;

    @Mock
    Path ddPath;

    @Mock
    KieModule kieModule;

    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    private CaseProjectServiceImpl caseProjectService;

    @BeforeClass
    public static void setupOnce() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
    }

    @AfterClass
    public static void cleanOnce() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
    }

    @Before
    public void setup() {
        // attach test logging appender
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);

        // configure project and kmodule paths
        when(kmodulePath.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/META-INF/kmodule.xml");
        when(projectPath.toURI()).thenReturn("default://p0/Evaluation");

        // configure deployment descriptor path
        when(ddPath.toUri()).thenReturn(URI.create("default://p0/Evaluation/src/main/resources/META-INF/kie-deployment-descriptor.xml"));
        when(ddPath.getParent()).thenReturn(Mockito.mock(Path.class));
        FileSystem fileSystem = Mockito.mock(FileSystem.class);
        when(fileSystem.supportedFileAttributeViews()).thenReturn(new HashSet<String>());
        when(ddPath.getFileSystem()).thenReturn(fileSystem);

        // configure services
        when(ioService.get(any(URI.class))).thenReturn(ddPath);
        when(ddEditorService.load(any())).thenReturn(new DeploymentDescriptorModel());

        // configure the project
        when(kieModule.getKModuleXMLPath()).thenReturn(kmodulePath);
        when(kieModule.getRootPath()).thenReturn(projectPath);

        caseProjectService = new CaseProjectServiceImpl(ddEditorService,
                                                        ioService);
    }

    @After
    public void tearDown() {
        // detach test logging appender
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

    @Test
    public void testConfigureNewCaseProject() {
        final ArgumentCaptor<DeploymentDescriptorModel> ddArgumentCaptor = ArgumentCaptor.forClass(DeploymentDescriptorModel.class);

        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(new ArrayList().iterator());

        final Branch branch = mock(Branch.class);
        doReturn(projectPath).when(branch).getPath();

        caseProjectService.configureNewCaseProject(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                        mock(Repository.class),
                                                                        branch,
                                                                        kieModule));
        verify(ddEditorService,
               times(1)).save(any(),
                              ddArgumentCaptor.capture(),
                              any(),
                              eq("Updated with case project configuration"));

        DeploymentDescriptorModel updatedDD = ddArgumentCaptor.getValue();
        assertNotNull(updatedDD);
        assertEquals("PER_CASE",
                     updatedDD.getRuntimeStrategy());

        List<ItemObjectModel> marshallingStrategies = updatedDD.getMarshallingStrategies();
        assertEquals(2,
                     marshallingStrategies.size());

        Map<String, String> mappedStrategies = marshallingStrategies.stream().collect(Collectors.toMap(ItemObjectModel::getValue,
                                                                                                       ItemObjectModel::getResolver));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        List<ItemObjectModel> workItemHandlers = updatedDD.getWorkItemHandlers();
        assertEquals(1,
                     workItemHandlers.size());

        ItemObjectModel startCaseHandler = workItemHandlers.get(0);
        assertEquals("mvel",
                     startCaseHandler.getResolver());
        assertEquals(CaseProjectServiceImpl.START_CASE_WORK_ITEM,
                     startCaseHandler.getName());
        assertEquals(CaseProjectServiceImpl.START_CASE_HANDLER,
                     startCaseHandler.getValue());

        verify(ioService,
               times(1)).write(any(),
                               any(byte[].class));
    }

    @Test
    public void testConfigureNewCaseProjectWithPackages() {
        final ArgumentCaptor<DeploymentDescriptorModel> ddArgumentCaptor = ArgumentCaptor.forClass(DeploymentDescriptorModel.class);

        Path packagePath = Mockito.mock(Path.class);
        when(packagePath.toUri()).thenReturn(URI.create("default://p0/Evaluation/src/main/resources/org"));
        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(Arrays.asList(packagePath).iterator());

        final Branch branch = mock(Branch.class);
        doReturn(projectPath).when(branch).getPath();

        caseProjectService.configureNewCaseProject(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                        mock(Repository.class),
                                                                        branch,
                                                                        kieModule));
        verify(ddEditorService,
               times(1)).save(any(),
                              ddArgumentCaptor.capture(),
                              any(),
                              eq("Updated with case project configuration"));

        DeploymentDescriptorModel updatedDD = ddArgumentCaptor.getValue();
        assertNotNull(updatedDD);
        assertEquals("PER_CASE",
                     updatedDD.getRuntimeStrategy());

        List<ItemObjectModel> marshallingStrategies = updatedDD.getMarshallingStrategies();
        assertEquals(2,
                     marshallingStrategies.size());

        Map<String, String> mappedStrategies = marshallingStrategies.stream().collect(Collectors.toMap(ItemObjectModel::getValue,
                                                                                                       ItemObjectModel::getResolver));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        verify(ioService,
               times(1)).write(any(),
                               any(byte[].class));
    }

    @Test
    public void testConfigureNewPackage() {

        Path packagePath = Mockito.mock(Path.class);
        when(packagePath.toUri()).thenReturn(URI.create("default://p0/Evaluation/src/main/resources/org"));
        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(Arrays.asList(packagePath).iterator());

        org.guvnor.common.services.project.model.Package pkg = Mockito.mock(org.guvnor.common.services.project.model.Package.class);
        when(pkg.getModuleRootPath()).thenReturn(projectPath);
        when(pkg.getPackageMainResourcesPath()).thenReturn(projectPath);

        NewPackageEvent event = new NewPackageEvent(pkg);

        caseProjectService.configurePackage(event);

        verify(ioService,
               times(1)).write(any(),
                               any(byte[].class));
    }

    @Test
    public void testConfigureNewCaseProjectNoDeploymentDescriptor() {
        final ArgumentCaptor<DeploymentDescriptorModel> ddArgumentCaptor = ArgumentCaptor.forClass(DeploymentDescriptorModel.class);

        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(new ArrayList().iterator());
        when(ioService.exists(ddPath)).thenReturn(false);

        final Branch branch = mock(Branch.class);
        doReturn(projectPath).when(branch).getPath();

        caseProjectService.configureNewCaseProject(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                        mock(Repository.class),
                                                                        branch,
                                                                        kieModule));
        verify(ddEditorService,
               times(1)).save(any(),
                              ddArgumentCaptor.capture(),
                              any(),
                              eq("Updated with case project configuration"));
        verify(ddEditorService,
               times(1)).createIfNotExists(any());

        DeploymentDescriptorModel updatedDD = ddArgumentCaptor.getValue();
        assertNotNull(updatedDD);
        assertEquals("PER_CASE",
                     updatedDD.getRuntimeStrategy());

        List<ItemObjectModel> marshallingStrategies = updatedDD.getMarshallingStrategies();
        assertEquals(2,
                     marshallingStrategies.size());

        Map<String, String> mappedStrategies = marshallingStrategies.stream().collect(Collectors.toMap(ItemObjectModel::getValue,
                                                                                                       ItemObjectModel::getResolver));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        List<ItemObjectModel> workItemHandlers = updatedDD.getWorkItemHandlers();
        assertEquals(1,
                     workItemHandlers.size());

        ItemObjectModel startCaseHandler = workItemHandlers.get(0);
        assertEquals("mvel",
                     startCaseHandler.getResolver());
        assertEquals(CaseProjectServiceImpl.START_CASE_WORK_ITEM,
                     startCaseHandler.getName());
        assertEquals(CaseProjectServiceImpl.START_CASE_HANDLER,
                     startCaseHandler.getValue());

        verify(ioService,
               times(1)).write(any(),
                               any(byte[].class));
    }

    @Test
    public void testConfigureNewCaseProjectErrorWritingDotFile() {
        // writing marker dot file will fail, but configureNewCaseProject should still run as expected
        when(ioService.write(any(),
                             anyString())).thenThrow(IllegalArgumentException.class);

        final ArgumentCaptor<DeploymentDescriptorModel> ddArgumentCaptor = ArgumentCaptor.forClass(DeploymentDescriptorModel.class);

        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(new ArrayList().iterator());

        final Branch branch = mock(Branch.class);
        doReturn(projectPath).when(branch).getPath();

        caseProjectService.configureNewCaseProject(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                        mock(Repository.class),
                                                                        branch,
                                                                        kieModule));
        verify(ddEditorService,
               times(1)).save(any(),
                              ddArgumentCaptor.capture(),
                              any(),
                              eq("Updated with case project configuration"));

        DeploymentDescriptorModel updatedDD = ddArgumentCaptor.getValue();
        assertNotNull(updatedDD);
        assertEquals("PER_CASE",
                     updatedDD.getRuntimeStrategy());

        List<ItemObjectModel> marshallingStrategies = updatedDD.getMarshallingStrategies();
        assertEquals(2,
                     marshallingStrategies.size());

        Map<String, String> mappedStrategies = marshallingStrategies.stream().collect(Collectors.toMap(ItemObjectModel::getValue,
                                                                                                       ItemObjectModel::getResolver));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertTrue(mappedStrategies.containsKey(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.CASE_FILE_MARSHALLER));
        assertEquals("mvel",
                     mappedStrategies.get(CaseProjectServiceImpl.DOCUMENT_MARSHALLER));

        List<ItemObjectModel> workItemHandlers = updatedDD.getWorkItemHandlers();
        assertEquals(1,
                     workItemHandlers.size());

        ItemObjectModel startCaseHandler = workItemHandlers.get(0);
        assertEquals("mvel",
                     startCaseHandler.getResolver());
        assertEquals(CaseProjectServiceImpl.START_CASE_WORK_ITEM,
                     startCaseHandler.getName());
        assertEquals(CaseProjectServiceImpl.START_CASE_HANDLER,
                     startCaseHandler.getValue());

        // still 1 as it was called (even tho exception was thrown)
        verify(ioService,
               times(1)).write(any(),
                               any(byte[].class));

        verify(mockAppender,
               times(1)).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertEquals(loggingEvent.getLevel(),
                     Level.ERROR);
        assertEquals(loggingEvent.getFormattedMessage(),
                     "Unable to write caseproject marker (dot) file: null");
    }
}