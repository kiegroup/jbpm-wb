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

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.COMPLETED;
import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.DISABLED;
import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.FAILED;
import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.STARTED;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseProvisioningServiceImplTest {

    @Mock
    private CaseProvisioningExecutor executor;

    @Mock
    private CaseProvisioningSettings settings;

    @InjectMocks
    private CaseProvisioningServiceImpl service;

    @Test
    public void testProvisioningDisabled() {
        when(settings.isProvisioningEnabled()).thenReturn(false);

        service.init();

        verify(executor,
               never()).execute(any(PipelineExecutor.class),
                                any(Pipeline.class),
                                any(Input.class));
        assertEquals(DISABLED,
                     service.getProvisioningStatus());
    }

    @Test
    public void testProvisioningUsingWarPath() {
        final String path = "path/to/file.war";
        when(settings.isProvisioningEnabled()).thenReturn(true);
        when(settings.isDeployFromLocalPath()).thenReturn(true);
        when(settings.getPath()).thenReturn(path);

        service.init();

        ArgumentCaptor<Input> captor = ArgumentCaptor.forClass(Input.class);
        verify(executor).execute(any(PipelineExecutor.class),
                                 any(Pipeline.class),
                                 captor.capture());
        assertEquals(path,
                     captor.getValue().get("war-path"));
    }

    @Test
    public void testProvisioningUsingMaven() {
        final String gav = "org.jbpm:jbpm-wb-case-mgmt-showcase:war:1.0.0";
        when(settings.isProvisioningEnabled()).thenReturn(true);
        when(settings.isDeployFromLocalPath()).thenReturn(false);
        when(settings.getGAV()).thenReturn(gav);

        service.init();

        ArgumentCaptor<Input> captor = ArgumentCaptor.forClass(Input.class);
        verify(executor).execute(any(PipelineExecutor.class),
                                 any(Pipeline.class),
                                 captor.capture());
        assertEquals(gav,
                     captor.getValue().get("artifact"));
    }

    @Test
    public void testOnCaseManagementProvisioningStartedEvent() {
        service.onCaseManagementProvisioningStartedEvent(new CaseProvisioningStartedEvent());

        assertEquals(STARTED,
                     service.getProvisioningStatus());
    }

    @Test
    public void testOnCaseManagementProvisioningCompletedEvent() {
        service.onCaseManagementProvisioningCompletedEvent(new CaseProvisioningCompletedEvent("/context"));

        assertEquals(COMPLETED,
                     service.getProvisioningStatus());
    }

    @Test
    public void testOnCaseManagementProvisioningFailedEvent() {
        service.onCaseManagementProvisioningFailedEvent(new CaseProvisioningFailedEvent());

        assertEquals(FAILED,
                     service.getProvisioningStatus());
    }
}
