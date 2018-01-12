/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor2;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.items.NamedObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.items.ObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.items.RequiredRolesListItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model.AuditMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model.PersistenceMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model.Resolver;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model.RuntimeStrategy;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublev.AddDoubleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
public class DeploymentsSectionPresenter extends SettingsPresenter.Section {

    private final View view;

    private final ProjectContext projectContext;
    private final Caller<DDEditorService> ddEditorService;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final MarshallingStrategiesListPresenter marshallingStrategyPresenters;
    private final EventListenersListPresenter eventListenerPresenters;
    private final GlobalsListPresenter globalPresenters;
    private final RequiredRolesListPresenter requiredRolePresenters;
    private final AddSingleValueModal addMarshallingStrategyModal;
    private final AddSingleValueModal addEventListenerModal;
    private final AddDoubleValueModal addGlobalModal;
    private final AddSingleValueModal addRequiredRoleModal;
    private final KieEnumSelectElement<RuntimeStrategy> runtimeStrategiesSelect;
    private final KieEnumSelectElement<PersistenceMode> persistenceModesSelect;
    private final KieEnumSelectElement<AuditMode> auditModesSelect;
    private final Event<NotificationEvent> notificationEvent;

    private ObservablePath pathToDeploymentsXml;
    private ObservablePath.OnConcurrentUpdateEvent concurrentDeploymentsXmlUpdateInfo;
    private DeploymentDescriptorModel model;

    public interface View extends SettingsPresenter.View.Section<DeploymentsSectionPresenter> {

        Element getMarshallingStrategiesTable();

        Element getEventListenersTable();

        Element getGlobalsTable();

        Element getRequiredRolesTable();

        void setPersistenceUnitName(final String persistenceUnitName);

        void setAuditPersistenceUnitName(final String auditPersistenceUnitName);

        Element getRuntimeStrategiesContainer();

        Element getPersistenceModesContainer();

        Element getAuditModesContainer();

        String getConcurrentUpdateMessage();
    }

    @Inject
    public DeploymentsSectionPresenter(final View view,
                                       final SettingsPresenter.MenuItem menuItem,
                                       final AddSingleValueModal addMarshallingStrategyModal,
                                       final AddSingleValueModal addEventListenerModal,
                                       final AddDoubleValueModal addGlobalModal,
                                       final AddSingleValueModal addRequiredRoleModal,
                                       final KieEnumSelectElement<RuntimeStrategy> runtimeStrategiesSelect,
                                       final KieEnumSelectElement<PersistenceMode> persistenceModesSelect,
                                       final KieEnumSelectElement<AuditMode> auditModesSelect,
                                       final ProjectContext projectContext,
                                       final Caller<DDEditorService> ddEditorService,
                                       final ManagedInstance<ObservablePath> observablePaths,
                                       final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                       final MarshallingStrategiesListPresenter marshallingStrategyPresenters,
                                       final EventListenersListPresenter eventListenerPresenters,
                                       final GlobalsListPresenter globalPresenters,
                                       final RequiredRolesListPresenter requiredRolePresenters, Event<NotificationEvent> notificationEvent) {

        super(settingsSectionChangeEvent, menuItem);
        this.view = view;
        this.addMarshallingStrategyModal = addMarshallingStrategyModal;
        this.addEventListenerModal = addEventListenerModal;
        this.addGlobalModal = addGlobalModal;
        this.addRequiredRoleModal = addRequiredRoleModal;
        this.runtimeStrategiesSelect = runtimeStrategiesSelect;
        this.persistenceModesSelect = persistenceModesSelect;
        this.auditModesSelect = auditModesSelect;
        this.projectContext = projectContext;
        this.ddEditorService = ddEditorService;
        this.observablePaths = observablePaths;
        this.marshallingStrategyPresenters = marshallingStrategyPresenters;
        this.eventListenerPresenters = eventListenerPresenters;
        this.globalPresenters = globalPresenters;
        this.requiredRolePresenters = requiredRolePresenters;
        this.notificationEvent = notificationEvent;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel ignore) {
        return setup();
    }

    private Promise<Void> setup() {
        view.init(this);

        final String deploymentsXmlUri = projectContext.getActiveProject()
                .getRootPath().toURI() + "/src/main/resources/META-INF/kie-deployment-descriptor.xml";

        pathToDeploymentsXml = observablePaths.get().wrap(PathFactory.newPath(
                "kie-deployment-descriptor.xml",
                deploymentsXmlUri));

        pathToDeploymentsXml.onConcurrentUpdate(info -> concurrentDeploymentsXmlUpdateInfo = info);
        concurrentDeploymentsXmlUpdateInfo = null;

        return createIfNotExists().then(ignore -> loadDeploymentDescriptor().then(model -> {

            this.model = model;

            addMarshallingStrategyModal.setup(LibraryConstants.AddMarshallingStrategy, LibraryConstants.Id);
            addEventListenerModal.setup(LibraryConstants.AddEventListener, LibraryConstants.Id);
            addGlobalModal.setup(LibraryConstants.AddGlobal, LibraryConstants.Name, LibraryConstants.Value);
            addRequiredRoleModal.setup(LibraryConstants.AddRequiredRole, LibraryConstants.Role);

            runtimeStrategiesSelect.setup(view.getRuntimeStrategiesContainer(), RuntimeStrategy.values());
            runtimeStrategiesSelect.setValue(RuntimeStrategy.valueOf(model.getRuntimeStrategy()));
            runtimeStrategiesSelect.onChange(runtimeStrategy -> {
                model.setRuntimeStrategy(runtimeStrategy.name());
                fireChangeEvent();
            });

            persistenceModesSelect.setup(view.getPersistenceModesContainer(), PersistenceMode.values());
            persistenceModesSelect.setValue(PersistenceMode.valueOf(model.getPersistenceMode()));
            persistenceModesSelect.onChange(persistenceMode -> {
                model.setPersistenceMode(persistenceMode.name());
                fireChangeEvent();
            });

            auditModesSelect.setup(view.getAuditModesContainer(), AuditMode.values());
            auditModesSelect.setValue(AuditMode.valueOf(model.getAuditMode()));
            auditModesSelect.onChange(auditMode -> {
                model.setAuditMode(auditMode.name());
                fireChangeEvent();
            });

            view.setPersistenceUnitName(model.getPersistenceUnitName());
            view.setAuditPersistenceUnitName(model.getAuditPersistenceUnitName());

            if (model.getMarshallingStrategies() == null) {
                model.setMarshallingStrategies(new ArrayList<>());
            }
            marshallingStrategyPresenters.setup(
                    view.getMarshallingStrategiesTable(),
                    model.getMarshallingStrategies(),
                    (marshallingStrategy, presenter) -> presenter.setup(marshallingStrategy, this));

            if (model.getEventListeners() == null) {
                model.setEventListeners(new ArrayList<>());
            }
            eventListenerPresenters.setup(
                    view.getEventListenersTable(),
                    model.getEventListeners(),
                    (eventListener, presenter) -> presenter.setup(eventListener, this));

            if (model.getGlobals() == null) {
                model.setGlobals(new ArrayList<>());
            }
            globalPresenters.setup(
                    view.getGlobalsTable(),
                    model.getGlobals(),
                    (global, presenter) -> presenter.setup(global, this));

            if (model.getRequiredRoles() == null) {
                model.setRequiredRoles(new ArrayList<>());
            }
            requiredRolePresenters.setup(
                    view.getRequiredRolesTable(),
                    model.getRequiredRoles(),
                    (requiredRole, presenter) -> presenter.setup(requiredRole, this));

            return resolve();
        }));
    }

    private Promise<DeploymentDescriptorModel> loadDeploymentDescriptor() {
        return Promises.promisify(ddEditorService, s -> s.load(pathToDeploymentsXml));
    }

    private Promise<Object> createIfNotExists() {
        return Promises.promisify(ddEditorService, s -> s.createIfNotExists(pathToDeploymentsXml), this::noOp);
    }

    private void noOp(final Void ignore, final Throwable throwable) {
    }

    public void openNewMarshallingStrategyModal() {
        addMarshallingStrategyModal.show(iname -> {
            marshallingStrategyPresenters.add(newObjectModelItem(iname));
            fireChangeEvent();
        });
    }

    public void openNewEventListenerModal() {
        addEventListenerModal.show(name -> {
            eventListenerPresenters.add(newObjectModelItem(name));
            fireChangeEvent();
        });
    }

    public void openNewGlobalModal() {
        addGlobalModal.show((name, value) -> {
            globalPresenters.add(newNamedObjectModelItem(name, value));
            fireChangeEvent();
        });
    }

    public void openNewRequiredRoleModal() {
        addRequiredRoleModal.show(role -> {
            requiredRolePresenters.add(role);
            fireChangeEvent();
        });
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentDeploymentsXmlUpdateInfo == null) {
            return save(comment);
        } else {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup();
        }
    }

    private Promise<Void> save(String comment) {
        return Promises.promisify(
                ddEditorService,
                s -> s.save(pathToDeploymentsXml,
                            model,
                            model.getOverview().getMetadata(), comment)).then(i -> resolve());
    }

    private ItemObjectModel newObjectModelItem(final String name) {
        final ItemObjectModel model = new ItemObjectModel();
        model.setValue(name);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
    }

    private ItemObjectModel newNamedObjectModelItem(final String name,
                                                    final String value) {

        final ItemObjectModel model = new ItemObjectModel();
        model.setName(name);
        model.setValue(value);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
    }

    public void setPersistenceUnitName(final String persistenceUnitName) {
        model.setPersistenceUnitName(persistenceUnitName);
        fireChangeEvent();
    }

    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        model.setAuditPersistenceUnitName(auditPersistenceUnitName);
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return model.hashCode();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Dependent
    public static class MarshallingStrategiesListPresenter extends ListPresenter<ItemObjectModel, ObjectItemPresenter> {

        @Inject
        public MarshallingStrategiesListPresenter(final ManagedInstance<ObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class EventListenersListPresenter extends ListPresenter<ItemObjectModel, ObjectItemPresenter> {

        @Inject
        public EventListenersListPresenter(final ManagedInstance<ObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class GlobalsListPresenter extends ListPresenter<ItemObjectModel, NamedObjectItemPresenter> {

        @Inject
        public GlobalsListPresenter(final ManagedInstance<NamedObjectItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class RequiredRolesListPresenter extends ListPresenter<String, RequiredRolesListItemPresenter> {

        @Inject
        public RequiredRolesListPresenter(final ManagedInstance<RequiredRolesListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
