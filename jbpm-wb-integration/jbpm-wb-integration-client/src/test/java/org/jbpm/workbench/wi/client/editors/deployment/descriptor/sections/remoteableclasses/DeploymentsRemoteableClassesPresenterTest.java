package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.remoteableclasses;

import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsRemoteableClassesPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsRemoteableClassesView view;

    @Mock
    private DeploymentsRemoteableClassesPresenter.RemoteableClassesListPresenter remoteableClassListPresenter;

    @Mock
    private AddSingleValueModal addRemoteableClassModal;

    private Promises promises = new SyncPromises();

    private DeploymentsRemoteableClassesPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsRemoteableClassesPresenter(settingsSectionChangeEvent,
                                                                       menuItem,
                                                                       promises,
                                                                       view,
                                                                       remoteableClassListPresenter,
                                                                       addRemoteableClassModal));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getRemotableClasses());
        verify(addRemoteableClassModal).setup(any(), any());
        verify(remoteableClassListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewRemoteableClassModal();
        verify(addRemoteableClassModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addRemoteableClass("Name");
        verify(remoteableClassListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}