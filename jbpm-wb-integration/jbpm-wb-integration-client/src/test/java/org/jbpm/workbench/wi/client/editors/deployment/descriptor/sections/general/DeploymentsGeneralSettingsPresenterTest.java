package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.AuditMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.PersistenceMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.RuntimeStrategy;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsGeneralSettingsPresenterTest {

    @Mock
    private KieEnumSelectElement<AuditMode> auditModesSelect;

    @Mock
    private KieEnumSelectElement<PersistenceMode> persistenceModesSelect;

    @Mock
    private KieEnumSelectElement<RuntimeStrategy> runtimeStrategiesSelect;

    @Mock
    private DeploymentsGeneralSettingsView view;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    private Promises promises = new SyncPromises();

    private DeploymentsGeneralSettingsPresenter presenter;

    @Before
    public void before() {
        presenter = spy(new DeploymentsGeneralSettingsPresenter(settingsSectionChangeEvent,
                                                            menuItem,
                                                            promises,
                                                            view,
                                                            runtimeStrategiesSelect,
                                                            persistenceModesSelect,
                                                            auditModesSelect));
    }

    @Test
    public void testSetup() {

    }

    @Test
    public void testSetupRuntimeStrategiesSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setRuntimeStrategy("SINGLETON");

        presenter.setupRuntimeStrategiesSelect(model);

        verify(runtimeStrategiesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetupPersistenceModesSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setPersistenceMode("JPA");

        presenter.setupPersistenceModesSelect(model);

        verify(persistenceModesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetupAuditModeSelect() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        model.setAuditMode("JPA");

        presenter.setupAuditModeSelect(model);

        verify(auditModesSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testSetPersistenceUnitName() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        presenter.model = model;

        presenter.setPersistenceUnitName("Name");

        assertEquals("Name", model.getPersistenceUnitName());
        verify(presenter).fireChangeEvent();
    }

    @Test
    public void testSetAuditPersistenceUnitName() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();
        presenter.model = model;

        presenter.setAuditPersistenceUnitName("Name");

        assertEquals("Name", model.getAuditPersistenceUnitName());
        verify(presenter).fireChangeEvent();
    }
}