package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.taskeventlisteners;

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
public class DeploymentsTaskEventListenersPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsTaskEventListenersView view;

    @Mock
    private DeploymentsTaskEventListenersPresenter.TaskEventListenersListPresenter taskEventListenersListPresenter;

    @Mock
    private AddSingleValueModal addTaskEventListenerModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactory;

    private Promises promises = new SyncPromises();

    private DeploymentsTaskEventListenersPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsTaskEventListenersPresenter(settingsSectionChangeEvent,
                                                                        menuItem,
                                                                        promises,
                                                                        view,
                                                                        taskEventListenersListPresenter,
                                                                        addTaskEventListenerModal,
                                                                        itemObjectModelFactory));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getTaskEventListeners());
        verify(addTaskEventListenerModal).setup(any(), any());
        verify(taskEventListenersListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewTaskEventListenerModal();
        verify(addTaskEventListenerModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addTaskEventListener("Name");
        verify(taskEventListenersListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}