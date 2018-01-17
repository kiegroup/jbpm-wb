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

package org.jbpm.workbench.wi.client.editors.deployment.descriptornew;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptornew.model.AuditMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptornew.model.PersistenceMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptornew.model.RuntimeStrategy;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    private DeploymentsSectionPresenter deploymentsSectionPresenter;

    @Mock
    private DeploymentsSectionPresenter.View view;

    @Mock
    private SettingsPresenter.MenuItem menuItem;

    @Mock
    private AddSingleValueModal addMarshallingStrategyModal;

    @Mock
    private AddSingleValueModal addEventListenerModal;

    @Mock
    private AddDoubleValueModal addGlobalModal;

    @Mock
    private AddSingleValueModal addRequiredRoleModal;

    @Mock
    private KieEnumSelectElement<RuntimeStrategy> runtimeStrategiesSelect;

    @Mock
    private KieEnumSelectElement<PersistenceMode> persistenceModesSelect;

    @Mock
    private KieEnumSelectElement<AuditMode> auditModesSelect;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private DDEditorService ddEditorService;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private Event<SettingsSectionChange> settingsSectionChangeEvent;

    @Mock
    private DeploymentsSectionPresenter.MarshallingStrategiesListPresenter marshallingStrategyPresenters;

    @Mock
    private DeploymentsSectionPresenter.EventListenersListPresenter eventListenerPresenters;

    @Mock
    private DeploymentsSectionPresenter.GlobalsListPresenter globalPresenters;

    @Mock
    private DeploymentsSectionPresenter.RequiredRolesListPresenter requiredRolePresenters;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {

        final ObservablePath observablePath = mock(ObservablePath.class);
        doReturn(observablePath).when(observablePath).wrap(any());
        doReturn(observablePath).when(observablePaths).get();

        when(projectContext.getActiveProject()).thenReturn(mock(Project.class));
        when(projectContext.getActiveProject().getRootPath()).thenReturn(mock(Path.class));
        when(projectContext.getActiveProject().getRootPath().toURI()).thenReturn("root");

        deploymentsSectionPresenter = spy(new DeploymentsSectionPresenter(view,
                                                                          promises,
                                                                          menuItem,
                                                                          addMarshallingStrategyModal,
                                                                          addEventListenerModal,
                                                                          addGlobalModal,
                                                                          addRequiredRoleModal,
                                                                          runtimeStrategiesSelect,
                                                                          persistenceModesSelect,
                                                                          auditModesSelect,
                                                                          projectContext,
                                                                          new CallerMock<>(ddEditorService),
                                                                          observablePaths,
                                                                          settingsSectionChangeEvent,
                                                                          marshallingStrategyPresenters,
                                                                          eventListenerPresenters,
                                                                          globalPresenters,
                                                                          requiredRolePresenters,
                                                                          notificationEvent));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = mock(DeploymentDescriptorModel.class);

        doReturn(promises.resolve()).when(deploymentsSectionPresenter).createIfNotExists();
        doReturn(promises.resolve(model)).when(deploymentsSectionPresenter).loadDeploymentDescriptor();

        doNothing().when(deploymentsSectionPresenter).setupAuditModeSelect(eq(model));
        doNothing().when(deploymentsSectionPresenter).setupPersistenceModesSelect(eq(model));
        doNothing().when(deploymentsSectionPresenter).setupRuntimeStrategiesSelect(eq(model));

        doNothing().when(deploymentsSectionPresenter).setupMarshallingStrategiesTable(eq(model));
        doNothing().when(deploymentsSectionPresenter).setupEventListenersTable(eq(model));
        doNothing().when(deploymentsSectionPresenter).setupGlobalsTable(eq(model));
        doNothing().when(deploymentsSectionPresenter).setupRequiredRolesTable(eq(model));

        deploymentsSectionPresenter.setup(mock(ProjectScreenModel.class));

        verify(view).init(eq(deploymentsSectionPresenter));

        verify(view).setAuditPersistenceUnitName(any());
        verify(view).setPersistenceUnitName(any());

        verify(deploymentsSectionPresenter).setupRuntimeStrategiesSelect(eq(model));
        verify(deploymentsSectionPresenter).setupPersistenceModesSelect(eq(model));
        verify(deploymentsSectionPresenter).setupAuditModeSelect(eq(model));
        verify(deploymentsSectionPresenter).setupMarshallingStrategiesTable(eq(model));
        verify(deploymentsSectionPresenter).setupEventListenersTable(eq(model));
        verify(deploymentsSectionPresenter).setupGlobalsTable(eq(model));
        verify(deploymentsSectionPresenter).setupRequiredRolesTable(eq(model));
    }

    @Test
    public void testSetupRuntimeStrategiesSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setRuntimeStrategy("SINGLETON");

        deploymentsSectionPresenter.setupRuntimeStrategiesSelect(model);

        verify(runtimeStrategiesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetupPersistenceModesSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setPersistenceMode("JPA");

        deploymentsSectionPresenter.setupPersistenceModesSelect(model);

        verify(persistenceModesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetupAuditModeSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setAuditMode("JPA");

        deploymentsSectionPresenter.setupAuditModeSelect(model);

        verify(auditModesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetupMarshallingStrategiesTable() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        deploymentsSectionPresenter.setupMarshallingStrategiesTable(model);

        assertNotNull(model.getMarshallingStrategies());
        verify(addMarshallingStrategyModal).setup(any(), any());
        verify(marshallingStrategyPresenters).setup(any(), any(), any());
    }

    @Test
    public void testSetupEventListenersTable() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        deploymentsSectionPresenter.setupEventListenersTable(model);

        assertNotNull(model.getEventListeners());
        verify(addEventListenerModal).setup(any(), any());
        verify(eventListenerPresenters).setup(any(), any(), any());
    }

    @Test
    public void testSetupGlobalsTable() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        deploymentsSectionPresenter.setupGlobalsTable(model);

        assertNotNull(model.getGlobals());
        verify(addGlobalModal).setup(any(), any(), any());
        verify(globalPresenters).setup(any(), any(), any());
    }

    @Test
    public void testSetupRequiredRolesTable() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        deploymentsSectionPresenter.setupRequiredRolesTable(model);

        assertNotNull(model.getRequiredRoles());
        verify(addRequiredRoleModal).setup(any(), any());
        verify(requiredRolePresenters).setup(any(), any(), any());
    }

    @Test
    public void testCreateIfNotExists() {
        doNothing().when(ddEditorService).createIfNotExists(any());

        deploymentsSectionPresenter.createIfNotExists().catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).createIfNotExists(any());
    }

    @Test
    public void testLoadDeploymentDescriptor() {
        doReturn(mock(Path.class)).when(ddEditorService).load(any());

        deploymentsSectionPresenter.loadDeploymentDescriptor().catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).load(any());
    }

    @Test
    public void testOpenNewMarshallingStrategyModal() {
        deploymentsSectionPresenter.openNewMarshallingStrategyModal();
        verify(addMarshallingStrategyModal).show(any());
    }

    @Test
    public void testOpenNewEventListenerModal() {
        deploymentsSectionPresenter.openNewEventListenerModal();
        verify(addEventListenerModal).show(any());
    }

    @Test
    public void testOpenNewGlobalModal() {
        deploymentsSectionPresenter.openNewGlobalModal();
        verify(addGlobalModal).show(any());
    }

    @Test
    public void testOpenNewRequiredRoleModal() {
        deploymentsSectionPresenter.openNewRequiredRoleModal();
        verify(addRequiredRoleModal).show(any());
    }

    @Test
    public void testAddMarshallingStrategy() {
        deploymentsSectionPresenter.addMarshallingStrategy("Name");
        verify(marshallingStrategyPresenters).add(any());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }

    @Test
    public void testAddEventListener() {
        deploymentsSectionPresenter.addEventListener("Name");
        verify(eventListenerPresenters).add(any());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }

    @Test
    public void testAddGlobal() {
        deploymentsSectionPresenter.addGlobal("Name", "Value");
        verify(globalPresenters).add(any());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }

    @Test
    public void testAddRequiredRole() {
        deploymentsSectionPresenter.addRequiredRole("Name");
        verify(requiredRolePresenters).add(any());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }

    @Test
    public void testSave() {
        doReturn(promises.resolve()).when(deploymentsSectionPresenter).save(eq("Test comment"));

        deploymentsSectionPresenter.save("Test comment", null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(deploymentsSectionPresenter).save(eq("Test comment"));
        verify(deploymentsSectionPresenter, never()).setup();
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testSaveWithConcurrentUpdate() {
        deploymentsSectionPresenter.concurrentDeploymentsXmlUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doReturn(promises.resolve()).when(deploymentsSectionPresenter).setup();

        deploymentsSectionPresenter.save("Test comment", null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(deploymentsSectionPresenter, never()).save(any());
        verify(deploymentsSectionPresenter).setup();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSaveModel() {
        doReturn(mock(Path.class)).when(ddEditorService).save(any(), any(), any(), any());

        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setOverview(new Overview());
        deploymentsSectionPresenter.model = model;

        deploymentsSectionPresenter.save("Test comment").catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(ddEditorService).save(any(), any(), any(), eq("Test comment"));
    }

    @Test
    public void testNewObjectItemModel() {
        final ItemObjectModel model = deploymentsSectionPresenter.newObjectModelItem("Value");

        assertEquals("Value", model.getValue());
        assertEquals("mvel", model.getResolver());
        assertTrue(model.getParameters().isEmpty());
    }

    @Test
    public void testNewNamedObjectItemModel() {
        final ItemObjectModel model = deploymentsSectionPresenter.newNamedObjectModelItem("Name", "Value");

        assertEquals("Name", model.getName());
        assertEquals("Value", model.getValue());
        assertEquals("mvel", model.getResolver());
        assertTrue(model.getParameters().isEmpty());
    }

    @Test
    public void testSetPersistenceUnitName() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        deploymentsSectionPresenter.model = model;

        deploymentsSectionPresenter.setPersistenceUnitName("Name");

        assertEquals("Name", model.getPersistenceUnitName());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }

    @Test
    public void testSetAuditPersistenceUnitName() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        deploymentsSectionPresenter.model = model;

        deploymentsSectionPresenter.setAuditPersistenceUnitName("Name");

        assertEquals("Name", model.getAuditPersistenceUnitName());
        verify(deploymentsSectionPresenter).fireChangeEvent();
    }
}