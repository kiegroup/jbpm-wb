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

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.DeploymentsSections;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
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
    private final Event<NotificationEvent> notificationEvent;
    private final SectionManager<DeploymentDescriptorModel> sectionManager;
    private final DeploymentsSections deploymentsSections;

    private ObservablePath pathToDeploymentsXml;
    ObservablePath.OnConcurrentUpdateEvent concurrentDeploymentsXmlUpdateInfo;
    DeploymentDescriptorModel model;

    public interface View extends SectionView<DeploymentsSectionPresenter> {

        String getConcurrentUpdateMessage();

        HTMLElement getMenuItemsContainer();

        HTMLElement getContentContainer();
    }

    @Inject
    public DeploymentsSectionPresenter(final View view,
                                       final Promises promises,
                                       final MenuItem<ProjectScreenModel> menuItem,
                                       final WorkspaceProjectContext projectContext,
                                       final Caller<DDEditorService> ddEditorService,
                                       final ManagedInstance<ObservablePath> observablePaths,
                                       final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                       final Event<NotificationEvent> notificationEvent,
                                       final SectionManager<DeploymentDescriptorModel> sectionManager,
                                       final DeploymentsSections deploymentsSections) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.projectContext = projectContext;
        this.ddEditorService = ddEditorService;
        this.observablePaths = observablePaths;
        this.notificationEvent = notificationEvent;
        this.sectionManager = sectionManager;
        this.deploymentsSections = deploymentsSections;
    }

    @PostConstruct
    public void init() {
        sectionManager.init(deploymentsSections.getList(),
                            view.getMenuItemsContainer(),
                            view.getContentContainer());
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel ignore) {
        return setup();
    }

    Promise<Void> setup() {

        view.init(this);

        final String deploymentsXmlUri = projectContext.getActiveWorkspaceProject().get()
                .getRootPath().toURI() + "src/main/resources/META-INF/kie-deployment-descriptor.xml";

        pathToDeploymentsXml = observablePaths.get().wrap(PathFactory.newPath(
                "kie-deployment-descriptor.xml",
                deploymentsXmlUri));

        concurrentDeploymentsXmlUpdateInfo = null;
        pathToDeploymentsXml.onConcurrentUpdate(info -> concurrentDeploymentsXmlUpdateInfo = info);

        return createIfNotExists().then(i -> loadDeploymentDescriptor()).then(model -> {
            this.model = model;
            return promises.<Section<DeploymentDescriptorModel>, Void>all(deploymentsSections.getList(), section -> section.setup(model));
        }).then(i -> {
            sectionManager.resetAllDirtyIndicators();
            return sectionManager.goToCurrentSection();
        });
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

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentDeploymentsXmlUpdateInfo != null) {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup();
        }

        return save(comment).then(i -> {
            sectionManager.resetAllDirtyIndicators();
            return promises.resolve();
        });
    }

    Promise<Void> save(final String comment) {
        return promises.promisify(ddEditorService, s -> {
            s.save(pathToDeploymentsXml, model, model.getOverview().getMetadata(), comment);
        });
    }

    public void onSectionChanged(@Observes final SettingsSectionChange<DeploymentDescriptorModel> settingsSectionChange) {

        if (!sectionManager.manages(settingsSectionChange.getSection())) {
            return;
        }

        sectionManager.updateDirtyIndicator(settingsSectionChange.getSection());
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return model.hashCode();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }
}
