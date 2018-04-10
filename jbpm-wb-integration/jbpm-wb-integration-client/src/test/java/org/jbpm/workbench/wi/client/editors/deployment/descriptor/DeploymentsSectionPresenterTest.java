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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import java.util.Arrays;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.DeploymentsSections;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general.DeploymentsGeneralSettingsPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsSectionPresenterTest {

    private DeploymentsSectionPresenter presenter;

    @Mock
    private DeploymentsSectionPresenter.View view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private DDEditorService ddEditorService;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private EventSourceMock<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private DeploymentsSections deploymentsSections;

    @Mock
    private DeploymentsGeneralSettingsPresenter section1;

    @Mock
    private DeploymentsGeneralSettingsPresenter section2;

    @Mock
    private SectionManager<DeploymentDescriptorModel> sectionManager;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {

        final ObservablePath observablePath = mock(ObservablePath.class);
        doReturn(observablePath).when(observablePath).wrap(any());
        doReturn(observablePath).when(observablePaths).get();

        WorkspaceProject project = mock(WorkspaceProject.class);

        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(project));
        when(project.getRootPath()).thenReturn(mock(Path.class));
        when(project.getRootPath().toURI()).thenReturn("root");

        when(deploymentsSections.getList()).thenReturn(Arrays.asList(section1, section2));
        when(sectionManager.goTo(any())).thenReturn(promises.resolve());
        when(section1.setup(any())).thenReturn(promises.resolve());
        when(section2.setup(any())).thenReturn(promises.resolve());

        presenter = spy(new DeploymentsSectionPresenter(view,
                                                        promises,
                                                        menuItem,
                                                        projectContext,
                                                        new CallerMock<>(ddEditorService),
                                                        observablePaths,
                                                        settingsSectionChangeEvent,
                                                        notificationEvent,
                                                        sectionManager,
                                                        deploymentsSections));

        presenter.init();
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = mock(DeploymentDescriptorModel.class);

        doReturn(promises.resolve()).when(presenter).createIfNotExists();
        doReturn(promises.resolve(model)).when(presenter).loadDeploymentDescriptor();
        when(sectionManager.getCurrentSection()).thenReturn(section1);

        presenter.setup(mock(ProjectScreenModel.class)).catch_(i -> {
            Assert.fail("Promise should've been resolved!\n" + ((Throwable) i).getMessage());
            return promises.resolve();
        });

        verify(presenter).createIfNotExists();
        verify(presenter).loadDeploymentDescriptor();
        verify(view).init(eq(presenter));
        verify(section1).setup(eq(model));
        verify(section2).setup(eq(model));
        verify(sectionManager).goTo(eq(section1));
        verify(sectionManager).resetAllDirtyIndicators();
    }

    @Test
    public void testCreateIfNotExists() {
        doNothing().when(ddEditorService).createIfNotExists(any());

        presenter.createIfNotExists().catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).createIfNotExists(any());
    }

    @Test
    public void testLoadDeploymentDescriptor() {
        doReturn(mock(Path.class)).when(ddEditorService).load(any());

        presenter.loadDeploymentDescriptor().catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).load(any());
    }

    @Test
    public void testSave() {
        doReturn(promises.resolve()).when(presenter).save(eq("Test comment"));

        presenter.save("Test comment", null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(presenter).save(eq("Test comment"));
        verify(presenter, never()).setup();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testSaveWithConcurrentUpdate() {
        presenter.concurrentDeploymentsXmlUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doReturn(promises.resolve()).when(presenter).setup();

        presenter.save("Test comment", null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(presenter, never()).save(any());
        verify(presenter).setup();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSaveModel() {
        doReturn(mock(Path.class)).when(ddEditorService).save(any(), any(), any(), any());

        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setOverview(new Overview());
        presenter.model = model;

        presenter.save("Test comment").catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).save(any(), any(), any(), eq("Test comment"));
    }
}