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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jbpm.process.workitem.repository.RepositoryEventListener;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.jbpm.process.workitem.repository.service.RepoService;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTaskServiceImplTest {

    private static final String GAV = "org.jbpm.workitem.test:workitem.test:1.0";
    private static final String WORKITEMNAME = "Test";
    private static final int SERVICETASKSSIZE = 94;

    @Mock
    private IOService ioService;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private RepositoryEventListener eventListener;

    private RepoData service;
    private RepositoryStorageVFSImpl storage;

    private RepoService repoService;

    private ServiceTaskServiceImpl serviceTaskServiceImpl;

    @Mock
    private GuvnorM2Repository m2Repository;

    @Before
    public void init() {
        storage = new RepositoryStorageVFSImpl(ioService, fileSystem);
        storage.init();

        repoService = new RepoService(storage, eventListener);

        serviceTaskServiceImpl = new ServiceTaskServiceImpl();
        serviceTaskServiceImpl.setRepoService(repoService);
        serviceTaskServiceImpl.setM2Repository(m2Repository);

        File file = new File(ServiceTaskServiceImplTest.class.getResource("/workitem.test-1.0.jar").getFile());
        when(m2Repository.getArtifactFileFromRepository(new GAV(GAV))).thenReturn(file);
    }

    private void loadServices(String name, List<RepoData> currentServices) {
        service = new RepoData();
        service.setName(name);
        service.setModule("module");
        service.getInstalledOn().add("myspace/myproject");

        currentServices.add(service);
        storage.synchronizeServices(currentServices);
    }

    @Test
    public void testAddServiceTasksByCreated() {
        List<RepoData> currentServices = new ArrayList<>();
        loadServices("Test1", currentServices);
        assertEquals(SERVICETASKSSIZE, serviceTaskServiceImpl.getServiceTasks().size());
        Map<String, List<String>> resultMap = serviceTaskServiceImpl.addServiceTasks(GAV);
        assertEquals(SERVICETASKSSIZE + 1, serviceTaskServiceImpl.getServiceTasks().size());
        assertEquals(WORKITEMNAME, resultMap.get(RepoService.CREATED).get(0));
        assertTrue(resultMap.get(RepoService.SKIPPED).isEmpty());
        Optional optional = serviceTaskServiceImpl.getServiceTasks().stream().filter(serviceTaskSummary -> serviceTaskSummary.getName().equals(WORKITEMNAME)).findFirst();
        assertTrue(optional.isPresent());
    }

    @Test
    public void testAddServiceTasksBySkipped() {
        List<RepoData> currentServices = new ArrayList<>();
        loadServices("Test", currentServices);
        assertEquals(SERVICETASKSSIZE, serviceTaskServiceImpl.getServiceTasks().size());
        Map<String, List<String>> resultMap = serviceTaskServiceImpl.addServiceTasks(GAV);
        assertEquals(SERVICETASKSSIZE, serviceTaskServiceImpl.getServiceTasks().size());
        assertEquals(WORKITEMNAME, resultMap.get(RepoService.SKIPPED).get(0));
        assertTrue(resultMap.get(RepoService.CREATED).isEmpty());
        Optional optional = serviceTaskServiceImpl.getServiceTasks().stream().filter(serviceTaskSummary -> serviceTaskSummary.getName().equals(WORKITEMNAME)).findFirst();
        assertTrue(optional.isPresent());
    }

    @Test
    public void testRemoveServiceTask() {
        List<RepoData> currentServices = new ArrayList<>();
        String stName = "Test1";
        loadServices(stName, currentServices);
        RepoData rd = currentServices.get(0);
        ServiceTaskSummary st = new ServiceTaskSummary();
        st.setName(rd.getName());
        st.setId(rd.getId());
        st.setInstalledOn(Collections.emptySet());
        assertEquals(SERVICETASKSSIZE, serviceTaskServiceImpl.getServiceTasks().size());
        assertEquals(stName, serviceTaskServiceImpl.removeServiceTask(st));
        List<ServiceTaskSummary> afterRemoveAction = serviceTaskServiceImpl.getServiceTasks();
        assertEquals(SERVICETASKSSIZE - 1, afterRemoveAction.size());
        Optional<ServiceTaskSummary> optionalServiceTaskSummary = afterRemoveAction.stream().filter(s -> s.getName().equals(stName)).findFirst();
        assertFalse(optionalServiceTaskSummary.isPresent());
    }
}
