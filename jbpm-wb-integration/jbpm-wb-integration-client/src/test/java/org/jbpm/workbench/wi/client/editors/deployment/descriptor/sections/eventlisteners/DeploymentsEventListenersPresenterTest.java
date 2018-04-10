package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.eventlisteners;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
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
public class DeploymentsEventListenersPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsEventListenersView view;

    @Mock
    private DeploymentsEventListenersPresenter.EventListenersListPresenter eventListenersListPresenter;

    @Mock
    private AddSingleValueModal addEventListenerModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactory;

    private Promises promises = new SyncPromises();

    private DeploymentsEventListenersPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsEventListenersPresenter(settingsSectionChangeEvent,
                                                                    menuItem,
                                                                    promises,
                                                                    view,
                                                                    eventListenersListPresenter,
                                                                    addEventListenerModal,
                                                                    itemObjectModelFactory));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getEventListeners());
        verify(addEventListenerModal).setup(any(), any());
        verify(eventListenersListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewEventListenerModal();
        verify(addEventListenerModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addEventListener("Name");
        verify(eventListenersListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}