package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.marshallingstrategies;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
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
public class DeploymentsMarshallingStrategiesPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsMarshallingStrategiesView view;

    @Mock
    private DeploymentsMarshallingStrategiesPresenter.MarshallingStrategiesListPresenter marshallingStrategiesListPresenter;

    @Mock
    private AddSingleValueModal addMarshallingStrategyModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactory;

    private Promises promises = new SyncPromises();

    private DeploymentsMarshallingStrategiesPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsMarshallingStrategiesPresenter(settingsSectionChangeEvent,
                                                                           menuItem,
                                                                           promises,
                                                                           view,
                                                                           marshallingStrategiesListPresenter,
                                                                           addMarshallingStrategyModal,
                                                                           itemObjectModelFactory));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getMarshallingStrategies());
        verify(addMarshallingStrategyModal).setup(any(), any());
        verify(marshallingStrategiesListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewMarshallingStrategyModal();
        verify(addMarshallingStrategyModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addMarshallingStrategy("Name");
        verify(marshallingStrategiesListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}