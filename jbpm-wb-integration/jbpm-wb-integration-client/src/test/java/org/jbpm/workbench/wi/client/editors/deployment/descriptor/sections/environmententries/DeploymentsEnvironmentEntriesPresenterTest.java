package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.environmententries;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsEnvironmentEntriesPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsEnvironmentEntriesView view;

    @Mock
    private DeploymentsEnvironmentEntriesPresenter.EnvironmentEntriesListPresenter environmentEntriesListPresenter;

    @Mock
    private AddDoubleValueModal addEnvironmentEntryModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactory;

    private Promises promises = new SyncPromises();

    private DeploymentsEnvironmentEntriesPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsEnvironmentEntriesPresenter(settingsSectionChangeEvent,
                                                                      menuItem,
                                                                      promises,
                                                                      view,
                                                                      environmentEntriesListPresenter,
                                                                      addEnvironmentEntryModal,
                                                                      itemObjectModelFactory));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getEnvironmentEntries());
        verify(addEnvironmentEntryModal).setup(any(), any(), any());
        verify(environmentEntriesListPresenter).setup(any(), eq(model.getEnvironmentEntries()), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewEnvironmentEntryModal();
        verify(addEnvironmentEntryModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addEnvironmentEntry("Name", "Value");
        verify(environmentEntriesListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}