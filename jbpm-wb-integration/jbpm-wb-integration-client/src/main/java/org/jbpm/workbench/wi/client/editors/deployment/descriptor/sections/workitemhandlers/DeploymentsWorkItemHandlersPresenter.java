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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.workitemhandlers;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.NamedObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.Resolver;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.uberfire.client.promise.Promises;

@Dependent
public class DeploymentsWorkItemHandlersPresenter extends Section<DeploymentDescriptorModel> {

    @Inject
    private DeploymentsWorkItemHandlersView view;

    @Inject
    private WorkItemHandlersListPresenter workItemHandlersListPresenter;

    @Inject
    private AddDoubleValueModal addWorkItemHandlerModal;

    @Inject
    public DeploymentsWorkItemHandlersPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                                final MenuItem<DeploymentDescriptorModel> menuItem,
                                                final Promises promises) {

        super(settingsSectionChangeEvent, menuItem, promises);
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {

        addWorkItemHandlerModal.setup(LibraryConstants.AddWorkItemHandler, LibraryConstants.Name, LibraryConstants.Value);

        if (model.getWorkItemHandlers() == null) {
            model.setWorkItemHandlers(new ArrayList<>());
        }

        workItemHandlersListPresenter.setup(
                view.getWorkItemHandlersTable(),
                model.getWorkItemHandlers(),
                (workItemHandler, presenter) -> presenter.setup(workItemHandler, this));

        return promises.resolve();
    }

    public void openNewWorkItemHandlerModal() {
        addWorkItemHandlerModal.show(this::addWorkItemHandler);
    }

    void addWorkItemHandler(final String name, final String value) {
        workItemHandlersListPresenter.add(newNamedObjectModelItem(name, value));
        fireChangeEvent();
    }

    ItemObjectModel newNamedObjectModelItem(final String name,
                                            final String value) {

        final ItemObjectModel model = new ItemObjectModel();
        model.setName(name);
        model.setValue(value);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
    }

    @Override
    public SectionView getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return workItemHandlersListPresenter.getObjectsList().hashCode();
    }

    @Dependent
    public static class WorkItemHandlersListPresenter extends ListPresenter<ItemObjectModel, NamedObjectItemPresenter> {

        @Inject
        public WorkItemHandlersListPresenter(final ManagedInstance<NamedObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
