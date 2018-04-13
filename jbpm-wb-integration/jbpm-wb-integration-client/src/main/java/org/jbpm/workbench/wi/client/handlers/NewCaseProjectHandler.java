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

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewWorkspaceProjectWizard;
import org.kie.workbench.common.screens.projecteditor.client.wizard.POMBuilder;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewWorkspaceProjectHandler;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Projects
 */
@Dependent
public class NewCaseProjectHandler
        implements NewWorkspaceProjectHandler {

    private WorkspaceProjectContext context;
    private NewWorkspaceProjectWizard wizard;
    private ProjectController projectController;

    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;

    private Caller<CaseProjectService> caseProjectService;
    private Event<NotificationEvent> notification;

    private boolean openEditorOnCreation = true;
    private org.uberfire.client.callbacks.Callback<WorkspaceProject> creationSuccessCallback;
    org.uberfire.client.callbacks.Callback<WorkspaceProject> configureCaseProjectCallback = new org.uberfire.client.callbacks.Callback<WorkspaceProject>() {
        @Override
        public void callback(WorkspaceProject project) {
            if (project != null) {
                caseProjectService.call(new RemoteCallback<Void>() {
                                            @Override
                                            public void callback(Void aVoid) {
                                                notification.fire(new NotificationEvent(Constants.INSTANCE.ConfigureProjectSuccess(project.getName()),
                                                                                        NotificationEvent.NotificationType.SUCCESS));
                                                if (creationSuccessCallback != null) {
                                                    creationSuccessCallback.callback(project);
                                                }
                                            }
                                        },
                                        new DefaultErrorCallback() {
                                            @Override
                                            public boolean error(Message message,
                                                                 Throwable throwable) {
                                                notification.fire(new NotificationEvent(Constants.INSTANCE.ConfigureProjectFailure(project.getName()),
                                                                                        NotificationEvent.NotificationType.ERROR));
                                                return super.error(message,
                                                                   throwable);
                                            }
                                        }).configureNewCaseProject(project);
            }
        }
    };
    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEventEvent;
    private LibraryPreferences libraryPreferences;
    private Caller<OrganizationalUnitService> ouService;

    public NewCaseProjectHandler() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public NewCaseProjectHandler(final WorkspaceProjectContext context,
                                 final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEventEvent,
                                 final LibraryPreferences libraryPreferences,
                                 final NewWorkspaceProjectWizard wizard,
                                 final Caller<OrganizationalUnitService> ouService,
                                 final ProjectController projectController,
                                 final AnyResourceTypeDefinition resourceType) {
        this.context = context;
        this.projectContextChangeEventEvent = projectContextChangeEventEvent;
        this.libraryPreferences = libraryPreferences;
        this.ouService = ouService;
        this.wizard = wizard;
        this.projectController = projectController;
        this.resourceType = resourceType;
    }

    @Inject
    public void setCaseProjectService(Caller<CaseProjectService> caseProjectService) {
        this.caseProjectService = caseProjectService;
    }

    @Inject
    public void setNotification(Event<NotificationEvent> notification) {
        this.notification = notification;
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
        response.onSuccess(true);
    }

    @Override
    public Command getCommand(final NewResourcePresenter newResourcePresenter) {
        return new Command() {
            @Override
            public void execute() {

                if (!context.getActiveOrganizationalUnit().isPresent()) {
                    ouService.call(new RemoteCallback<OrganizationalUnit>() {
                        @Override
                        public void callback(OrganizationalUnit organizationalUnit) {

                            projectContextChangeEventEvent.fire(new WorkspaceProjectContextChangeEvent(organizationalUnit));

                            init();
                        }
                    }).getOrganizationalUnit(libraryPreferences.getOrganizationalUnitPreferences().getName());
                } else {
                    init();
                }
            }
        };
    }

    private void init() {
        wizard.initialise(new POMBuilder().setModuleName("")
                                  .setGroupId(context.getActiveOrganizationalUnit()
                                                     .orElseThrow(() -> new IllegalStateException("Cannot initialize new case project without an active organizational unit."))
                                                     .getDefaultGroupId())
                                  .build());
        wizard.start(configureCaseProjectCallback,
                     openEditorOnCreation);
    }

    @Override
    public void setCreationSuccessCallback(final org.uberfire.client.callbacks.Callback<WorkspaceProject> creationSuccessCallback) {
        this.creationSuccessCallback = creationSuccessCallback;
    }

    @Override
    public void setOpenEditorOnCreation(final boolean openEditorOnCreation) {
        this.openEditorOnCreation = openEditorOnCreation;
    }
}
