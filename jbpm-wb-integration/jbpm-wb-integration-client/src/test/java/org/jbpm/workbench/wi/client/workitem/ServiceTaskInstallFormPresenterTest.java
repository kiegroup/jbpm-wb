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

package org.jbpm.workbench.wi.client.workitem;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.client.workitem.project.ServiceTaskInstallFormPresenter;
import org.jbpm.workbench.wi.client.workitem.project.ServiceTaskInstallFormView;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import com.google.gwt.user.client.Command;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTaskInstallFormPresenterTest {

    private ServiceTaskInstallFormPresenter presenter;
    
    @Mock
    private ServiceTaskInstallFormView view;
    
    @Mock
    private ServiceTaskService serviceTaskService;

    @Mock
    private WorkspaceProjectContext workspaceProjectContext;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Branch branch;

    @Mock
    private SyncBeanManager iocManager;
    
    @Mock
    private Command cmd;
    
    private String serviceTaskId = "1111";
    private String target = "space/project";
    private List<String> parameters = new ArrayList<>();
    
    @Before
    public void before() {
        
        presenter = spy(new ServiceTaskInstallFormPresenter(view,
                                                           new CallerMock<ServiceTaskService>(serviceTaskService),
                                                           iocManager));
        presenter.init();

        when(workspaceProjectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(workspaceProject));
        when(workspaceProject.getBranch()).thenReturn(branch);
    }
    
    @Test
    public void testInstallServiceTask() {
        when(branch.getName()).thenReturn("master");

        presenter.showView(cmd, serviceTaskId, target, parameters, null);
        presenter.setWorkspaceProjectContext(workspaceProjectContext);
        presenter.installWithParameters(serviceTaskId, target, parameters);
        
        verify(serviceTaskService, times(1)).installServiceTask(eq(serviceTaskId), eq(target), eq(parameters), eq("master"));
        verify(cmd, times(1)).execute();
    }

    @Test
    public void testInstallServiceTaskCustomBranch() {
        when(branch.getName()).thenReturn("customBranch");

        presenter.showView(cmd, serviceTaskId, target, parameters, null);
        presenter.setWorkspaceProjectContext(workspaceProjectContext);
        presenter.installWithParameters(serviceTaskId, target, parameters);

        verify(serviceTaskService, times(1)).installServiceTask(eq(serviceTaskId), eq(target), eq(parameters), eq("customBranch"));
        verify(cmd, times(1)).execute();
    }

}
