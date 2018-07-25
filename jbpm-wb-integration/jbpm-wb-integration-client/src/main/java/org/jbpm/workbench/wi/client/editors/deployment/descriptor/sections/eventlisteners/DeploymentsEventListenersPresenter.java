/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.eventlisteners;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ObjectItemPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.promise.Promises;

import elemental2.promise.Promise;

@Dependent
public class DeploymentsEventListenersPresenter extends Section<DeploymentDescriptorModel> {

    private final DeploymentsEventListenersView view;
    private final EventListenersListPresenter eventListenerPresenters;
    private final AddSingleValueModal addEventListenerModal;
    private final ItemObjectModelFactory itemObjectModelFactory;

    @Inject
    public DeploymentsEventListenersPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                              final MenuItem<DeploymentDescriptorModel> menuItem,
                                              final Promises promises,
                                              final DeploymentsEventListenersView view,
                                              final EventListenersListPresenter eventListenerPresenters,
                                              final AddSingleValueModal addEventListenerModal,
                                              final ItemObjectModelFactory itemObjectModelFactory) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.eventListenerPresenters = eventListenerPresenters;
        this.addEventListenerModal = addEventListenerModal;
        this.itemObjectModelFactory = itemObjectModelFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {
        addEventListenerModal.setup(LibraryConstants.AddEventListener, LibraryConstants.Id);

        if (model.getEventListeners() == null) {
            model.setEventListeners(new ArrayList<>());
        }

        eventListenerPresenters.setup(
                view.getEventListenersTable(),
                model.getEventListeners(),
                (eventListener, presenter) -> presenter.setupSectionConfig(eventListener, 
                                                                           this,
                                                                           LibraryConstants.EditEventListener,
                                                                           LibraryConstants.Id));

        return promises.resolve();
    }

    public void openNewEventListenerModal() {
        addEventListenerModal.show(this::addEventListener);
    }

    void addEventListener(final String name) {
        eventListenerPresenters.add(itemObjectModelFactory.newItemObjectModel(name));
        fireChangeEvent();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return eventListenerPresenters.getObjectsList().hashCode();
    }

    @Dependent
    public static class EventListenersListPresenter extends ListPresenter<ItemObjectModel, ObjectItemPresenter> {

        @Inject
        public EventListenersListPresenter(final ManagedInstance<ObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
