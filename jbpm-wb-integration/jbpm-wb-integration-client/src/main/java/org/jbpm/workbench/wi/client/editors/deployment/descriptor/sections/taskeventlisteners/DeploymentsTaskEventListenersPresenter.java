/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.taskeventlisteners;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ObjectItemPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.uberfire.client.promise.Promises;

@Dependent
public class DeploymentsTaskEventListenersPresenter extends Section<DeploymentDescriptorModel> {

    private final DeploymentsTaskEventListenersView view;
    private final TaskEventListenersListPresenter taskEventListenerPresenters;
    private final AddSingleValueModal addTaskEventListenerModal;
    private final ItemObjectModelFactory itemObjectModelFactory;

    @Inject
    public DeploymentsTaskEventListenersPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                                  final MenuItem<DeploymentDescriptorModel> menuItem,
                                                  final Promises promises,
                                                  final DeploymentsTaskEventListenersView view,
                                                  final TaskEventListenersListPresenter taskEventListenerPresenters,
                                                  final AddSingleValueModal addTaskEventListenerModal,
                                                  final ItemObjectModelFactory itemObjectModelFactory) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.taskEventListenerPresenters = taskEventListenerPresenters;
        this.addTaskEventListenerModal = addTaskEventListenerModal;
        this.itemObjectModelFactory = itemObjectModelFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {
        addTaskEventListenerModal.setup(LibraryConstants.AddEventListener, LibraryConstants.Id);

        if (model.getTaskEventListeners() == null) {
            model.setTaskEventListeners(new ArrayList<>());
        }

        taskEventListenerPresenters.setup(
                view.getTaskEventListenersTable(),
                model.getTaskEventListeners(),
                (eventListener, presenter) -> presenter.setup(eventListener, this));

        return promises.resolve();
    }

    public void openNewTaskEventListenerModal() {
        addTaskEventListenerModal.show(this::addTaskEventListener);
    }

    void addTaskEventListener(final String name) {
        taskEventListenerPresenters.add(itemObjectModelFactory.newItemObjectModel(name));
        fireChangeEvent();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return taskEventListenerPresenters.getObjectsList().hashCode();
    }

    @Dependent
    public static class TaskEventListenersListPresenter extends ListPresenter<ItemObjectModel, ObjectItemPresenter> {

        @Inject
        public TaskEventListenersListPresenter(final ManagedInstance<ObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
