package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals.DeploymentsGlobalsPresenter.GlobalsListPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsGlobalsPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsGlobalsView view;

    @Mock
    private GlobalsListPresenter globalPresenters;

    @Mock
    private AddDoubleValueModal addGlobalModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactor;

    private Promises promises = new SyncPromises();

    private DeploymentsGlobalsPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsGlobalsPresenter(settingsSectionChangeEvent,
                                                         menuItem,
                                                         promises,
                                                         view,
                                                         globalPresenters,
                                                         addGlobalModal,
                                                         itemObjectModelFactor));
    }

    @Test
    public void testSetupGlobalsTable() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getGlobals());
        verify(addGlobalModal).setup(any(), any(), any());
        verify(globalPresenters).setup(any(), eq(model.getGlobals()), any());
    }

    @Test
    public void testOpenNewGlobalModal() {
        presenter.openNewGlobalModal();
        verify(addGlobalModal).show(any());
    }

    @Test
    public void testAddGlobal() {
        presenter.addGlobal("Name", "Value");
        verify(globalPresenters).add(any());
        verify(presenter).fireChangeEvent();
    }
}