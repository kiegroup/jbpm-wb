/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.NamedObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ObjectItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.RequiredRolesListItemPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.AuditMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.PersistenceMode;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.Resolver;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.model.RuntimeStrategy;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general.DeploymentsMarshallingStrategiesPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.general.DeploymentsSections;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionManager;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
public class DeploymentsSectionPresenter extends Section<ProjectScreenModel> {

    private final View view;

    private final WorkspaceProjectContext projectContext;
    private final Caller<DDEditorService> ddEditorService;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final EventListenersListPresenter eventListenerPresenters;
    private final GlobalsListPresenter globalPresenters;
    private final RequiredRolesListPresenter requiredRolePresenters;
    private final AddSingleValueModal addEventListenerModal;
    private final AddDoubleValueModal addGlobalModal;
    private final AddSingleValueModal addRequiredRoleModal;
    private final Event<NotificationEvent> notificationEvent;

    private ObservablePath pathToDeploymentsXml;
    ObservablePath.OnConcurrentUpdateEvent concurrentDeploymentsXmlUpdateInfo;
    DeploymentDescriptorModel model;

    @Inject
    private SectionManager<DeploymentDescriptorModel> sectionManager;

    @Inject
    private DeploymentsSections deploymentsSections;

    public interface View extends SectionView<DeploymentsSectionPresenter> {

        Element getEventListenersTable();

        Element getGlobalsTable();

        Element getRequiredRolesTable();

        String getConcurrentUpdateMessage();

        HTMLElement getMenuItemsContainer();

        HTMLElement getContentContainer();
    }

    @Inject
    public DeploymentsSectionPresenter(final View view,
                                       final Promises promises,
                                       final MenuItem<ProjectScreenModel> menuItem,
                                       final AddSingleValueModal addEventListenerModal,
                                       final AddDoubleValueModal addGlobalModal,
                                       final AddSingleValueModal addRequiredRoleModal,
                                       final WorkspaceProjectContext projectContext,
                                       final Caller<DDEditorService> ddEditorService,
                                       final ManagedInstance<ObservablePath> observablePaths,
                                       final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                       final EventListenersListPresenter eventListenerPresenters,
                                       final GlobalsListPresenter globalPresenters,
                                       final RequiredRolesListPresenter requiredRolePresenters, Event<NotificationEvent> notificationEvent) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.addEventListenerModal = addEventListenerModal;
        this.addGlobalModal = addGlobalModal;
        this.addRequiredRoleModal = addRequiredRoleModal;
        this.projectContext = projectContext;
        this.ddEditorService = ddEditorService;
        this.observablePaths = observablePaths;
        this.eventListenerPresenters = eventListenerPresenters;
        this.globalPresenters = globalPresenters;
        this.requiredRolePresenters = requiredRolePresenters;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void init() {

        //FIXME: urgh
        deploymentsSections.getList().forEach(s -> {
            if (s instanceof DeploymentsMarshallingStrategiesPresenter) {
                ((DeploymentsMarshallingStrategiesPresenter) s).setParentPresenter(this);
            }
        });

        sectionManager.init(deploymentsSections.getList(),
                            view.getMenuItemsContainer(),
                            view.getContentContainer());
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel i) {

        view.init(this);

        final String deploymentsXmlUri = projectContext.getActiveWorkspaceProject().get()
                .getRootPath().toURI() + "src/main/resources/META-INF/kie-deployment-descriptor.xml";

        pathToDeploymentsXml = observablePaths.get().wrap(PathFactory.newPath(
                "kie-deployment-descriptor.xml",
                deploymentsXmlUri));

        concurrentDeploymentsXmlUpdateInfo = null;
        pathToDeploymentsXml.onConcurrentUpdate(info -> concurrentDeploymentsXmlUpdateInfo = info);

        return createIfNotExists().then(ignore -> loadDeploymentDescriptor()).then(model -> {
            sectionManager.setupMenuItems();
            deploymentsSections.getList().forEach(section -> section.setup(model));
            sectionManager.goToFirstAvailable();

            this.model = model;

            setupEventListenersTable(model);
            setupGlobalsTable(model);
            setupRequiredRolesTable(model);

            return promises.resolve();
        });
    }

    void setupEventListenersTable(final DeploymentDescriptorModel model) {

        addEventListenerModal.setup(LibraryConstants.AddEventListener, LibraryConstants.Id);

        if (model.getEventListeners() == null) {
            model.setEventListeners(new ArrayList<>());
        }

        eventListenerPresenters.setup(
                view.getEventListenersTable(),
                model.getEventListeners(),
                (eventListener, presenter) -> presenter.setup(eventListener, this));
    }

    void setupGlobalsTable(final DeploymentDescriptorModel model) {

        addGlobalModal.setup(LibraryConstants.AddGlobal, LibraryConstants.Name, LibraryConstants.Value);

        if (model.getGlobals() == null) {
            model.setGlobals(new ArrayList<>());
        }

        globalPresenters.setup(
                view.getGlobalsTable(),
                model.getGlobals(),
                (global, presenter) -> presenter.setup(global, this));
    }

    void setupRequiredRolesTable(final DeploymentDescriptorModel model) {

        addRequiredRoleModal.setup(LibraryConstants.AddRequiredRole, LibraryConstants.Role);

        if (model.getRequiredRoles() == null) {
            model.setRequiredRoles(new ArrayList<>());
        }

        requiredRolePresenters.setup(
                view.getRequiredRolesTable(),
                model.getRequiredRoles(),
                (requiredRole, presenter) -> presenter.setup(requiredRole, this));
    }

    Promise<DeploymentDescriptorModel> loadDeploymentDescriptor() {
        return promises.promisify(ddEditorService, s -> {
            s.load(pathToDeploymentsXml);
        });
    }

    Promise<Void> createIfNotExists() {
        return promises.promisify(ddEditorService, s -> {
            s.createIfNotExists(pathToDeploymentsXml);
        });
    }


    public void openNewEventListenerModal() {
        addEventListenerModal.show(this::addEventListener);
    }

    public void openNewGlobalModal() {
        addGlobalModal.show(this::addGlobal);
    }

    public void openNewRequiredRoleModal() {
        addRequiredRoleModal.show(this::addRequiredRole);
    }


    void addEventListener(final String name) {
        eventListenerPresenters.add(newObjectModelItem(name));
        fireChangeEvent();
    }

    void addGlobal(final String name, final String value) {
        globalPresenters.add(newNamedObjectModelItem(name, value));
        fireChangeEvent();
    }

    void addRequiredRole(final String role) {
        requiredRolePresenters.add(role);
        fireChangeEvent();
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentDeploymentsXmlUpdateInfo == null) {
            return save(comment);
        } else {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup(null);
        }
    }

    Promise<Void> save(final String comment) {
        return promises.promisify(ddEditorService, s -> {
            s.save(pathToDeploymentsXml, model, model.getOverview().getMetadata(), comment);
        });
    }

    ItemObjectModel newObjectModelItem(final String name) {
        final ItemObjectModel model = new ItemObjectModel();
        model.setValue(name);
        model.setResolver(Resolver.MVEL.name().toLowerCase());
        model.setParameters(new ArrayList<>());
        return model;
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
    public int currentHashCode() {
        return model.hashCode();
    }

    @Override
    public SectionView getView() {
        return view;
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
