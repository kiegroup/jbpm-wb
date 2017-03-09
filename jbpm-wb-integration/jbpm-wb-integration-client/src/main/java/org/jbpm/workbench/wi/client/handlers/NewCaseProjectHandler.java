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

package org.jbpm.workbench.wi.client.handlers;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.kie.workbench.common.screens.projecteditor.client.handlers.NewProjectHandlerView;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.screens.projecteditor.client.wizard.POMBuilder;
import org.kie.workbench.common.widgets.client.handlers.NewProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Handler for the creation of new Projects
 */
@Dependent
public class NewCaseProjectHandler
        implements NewProjectHandler {

    private NewProjectHandlerView view;
    private ProjectContext context;
    private NewProjectWizard wizard;
    private Caller<RepositoryStructureService> repoStructureService;
    private ProjectController projectController;

    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    private Caller<CaseProjectService> caseProjectService;
    private Event<NotificationEvent> notification;

    private boolean openEditorOnCreation = true;
    private org.uberfire.client.callbacks.Callback<Project> creationSuccessCallback;

    @Inject
    public void setCaseProjectService(Caller<CaseProjectService> caseProjectService) {
        this.caseProjectService = caseProjectService;
    }

    @Inject
    public void setNotification(Event<NotificationEvent> notification) {
        this.notification = notification;
    }

    org.uberfire.client.callbacks.Callback<Project> configureCaseProjectCallback = new org.uberfire.client.callbacks.Callback<Project>() {
        @Override
        public void callback(Project project) {
            if (project != null) {
                caseProjectService.call(new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void aVoid) {
                        notification.fire(new NotificationEvent(Constants.INSTANCE.ConfigureProjectSuccess(project.getProjectName()), NotificationEvent.NotificationType.SUCCESS));
                        if (creationSuccessCallback != null) {
                            creationSuccessCallback.callback(project);
                        }
                    }
                }, new DefaultErrorCallback() {
                    @Override
                    public boolean error(Message message, Throwable throwable) {
                        notification.fire(new NotificationEvent(Constants.INSTANCE.ConfigureProjectFailure(project.getProjectName()), NotificationEvent.NotificationType.ERROR));
                        return super.error(message, throwable);
                    }
                }).configureNewCaseProject(project);
            }
        }
    };

    public NewCaseProjectHandler() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public NewCaseProjectHandler(final NewProjectHandlerView view,
                                 final ProjectContext context,
                                 final NewProjectWizard wizard,
                                 final Caller<RepositoryStructureService> repoStructureService,
                                 final ProjectController projectController,
                                 final AnyResourceTypeDefinition resourceType) {
        this.view = view;
        this.context = context;
        this.wizard = wizard;
        this.repoStructureService = repoStructureService;
        this.projectController = projectController;
        this.resourceType = resourceType;
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.CaseProject();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(ProjectEditorResources.INSTANCE.newProjectIcon());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public boolean canCreate() {
        return projectController.canCreateProjects();
    }

    @Override
    public void create(final Package pkg,
                       final String projectName,
                       final NewResourcePresenter presenter) {
        //This is not supported by the NewProjectHandler. It is invoked via NewResourceView that has bypassed for NewProjectHandler
        throw new UnsupportedOperationException();
    }

    @Override
    public void validate(final String projectName,
                         final ValidatorWithReasonCallback callback) {
        //This is not supported by the NewProjectHandler. It is invoked via NewResourceView that has bypassed for NewProjectHandler
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptContext(final Callback<Boolean, Void> response) {

        if (context.getActiveRepository() != null) {

            //You can always create a new Project (provided a repository has been selected)
            repoStructureService.call(new RemoteCallback<RepositoryStructureModel>() {

                @Override
                public void callback(RepositoryStructureModel repoModel) {
                    if (repoModel != null && repoModel.isManaged()) {
                        boolean isMultiModule = repoModel.isMultiModule();
                        response.onSuccess(isMultiModule);
                    } else {
                        response.onSuccess(true);
                    }
                }
            }).load(context.getActiveRepository(),
                    context.getActiveBranch());
        } else {
            response.onSuccess(false);
        }
    }

    @Override
    public Command getCommand(final NewResourcePresenter newResourcePresenter) {
        return new Command() {
            @Override
            public void execute() {
                if (context.getActiveRepository() != null) {
                    repoStructureService.call(new RemoteCallback<RepositoryStructureModel>() {

                        @Override
                        public void callback(final RepositoryStructureModel repositoryStructureModel) {
                            POMBuilder builder = new POMBuilder();
                            if (repositoryStructureModel != null && repositoryStructureModel.isManaged()) {
                                builder.setProjectName("")
                                        .setGroupId(repositoryStructureModel.getPOM().getGav().getGroupId())
                                        .setVersion(repositoryStructureModel.getPOM().getGav().getVersion());
                            } else {
                                builder.setProjectName("")
                                        .setGroupId(context.getActiveOrganizationalUnit().getDefaultGroupId());
                            }
                            wizard.initialise(builder.build());
                            wizard.start(configureCaseProjectCallback,
                                    openEditorOnCreation);
                        }
                    }).load(context.getActiveRepository(),
                            context.getActiveBranch());

                } else {
                    view.showNoRepositorySelectedPleaseSelectARepository();
                }
            }
        };
    }

    @Override
    public ProjectContext getProjectContext() {
        return context;
    }

    @Override
    public void setCreationSuccessCallback(final org.uberfire.client.callbacks.Callback<Project> creationSuccessCallback) {
        this.creationSuccessCallback = creationSuccessCallback;
    }

    @Override
    public void setOpenEditorOnCreation(final boolean openEditorOnCreation) {
        this.openEditorOnCreation = openEditorOnCreation;
    }
}
