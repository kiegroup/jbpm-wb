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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryStorageVFSImplTest {

    @Mock
    private IOService ioService;
    
    @Mock
    private FileSystem fileSystem;
    
    @Mock
    private RepositoryRemovedEvent repositoryRemovedEvent;
    
    @Mock
    private RepoRemovedFromOrganizationalUnitEvent spaceRepositoryRemovedEvent;
    
    @Mock
    private Repository repo;
    
    private RepoData service;
    private RepositoryStorageVFSImpl storage;
    
    @Before
    public void setup() {
        storage = new RepositoryStorageVFSImpl(ioService, fileSystem);
        storage.init();
        
        service = new RepoData(); 
        service.setName("test");
        service.setModule("module");
        service.getInstalledOn().add("myspace/myproject");
        
        List<RepoData> currentServices = new ArrayList<>();
        currentServices.add(service);
        storage.synchronizeServices(currentServices);
    }
    
    @Test
    public void testOnProjectRemoved() {
       
        assertEquals(1, service.getInstalledOn().size());
        
        when(repo.getUri()).thenReturn("default://myspace/myproject");
        when(repositoryRemovedEvent.getRepository()).thenReturn(repo);
        
        storage.onProjectDeleted(repositoryRemovedEvent);
        
        assertEquals(0, service.getInstalledOn().size());       
    }
    
    @Test
    public void testOnSpaceRemoved() {
        
        assertEquals(1, service.getInstalledOn().size());
        
        when(repo.getUri()).thenReturn("default://myspace/myproject");
        when(spaceRepositoryRemovedEvent.getRepository()).thenReturn(repo);
        
        storage.onSpaceDeleted(spaceRepositoryRemovedEvent);
        
        assertEquals(0, service.getInstalledOn().size());       
    }
    
}
