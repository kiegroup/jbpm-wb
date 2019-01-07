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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.screens.project.AddProjectPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
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
@ApplicationScoped
public class NewCaseProjectHandler implements NewWorkspaceProjectHandler {

    @Inject
    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEventEvent;

    @Inject
    private LibraryPreferences libraryPreferences;

    @Inject
    private Caller<OrganizationalUnitService> ouService;

    @Inject
    private WorkspaceProjectContext context;

    @Inject
    private LibraryPermissions libraryPermissions;

    private Instance<AddProjectPopUpPresenter> addProjectPopUpPresenterProvider;

    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    @Inject
    private AnyResourceTypeDefinition resourceType;

    private Caller<CaseProjectService> caseProjectService;

    private Event<NotificationEvent> notification;

    private org.uberfire.client.callbacks.Callback<WorkspaceProject> creationSuccessCallback;

    @Inject
    public void setCaseProjectService(Caller<CaseProjectService> caseProjectService) {
        this.caseProjectService = caseProjectService;
    }

    @Inject
    public void setNotification(Event<NotificationEvent> notification) {
        this.notification = notification;
    }

    @Inject
    public void setAddProjectPopUpPresenterProvider(Instance<AddProjectPopUpPresenter> addProjectPopUpPresenterProvider) {
        this.addProjectPopUpPresenterProvider = addProjectPopUpPresenterProvider;
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
        return libraryPermissions.userCanCreateProject(context.getActiveOrganizationalUnit().get());
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
        return () -> {
            if (!context.getActiveOrganizationalUnit().isPresent()) {
                ouService.call((OrganizationalUnit organizationalUnit) -> {
                    projectContextChangeEventEvent.fire(new WorkspaceProjectContextChangeEvent(organizationalUnit));
                    init();
                }).getOrganizationalUnit(libraryPreferences.getOrganizationalUnitPreferences().getName());
            } else {
                init();
            }
        };
    }

    protected void init() {
        final AddProjectPopUpPresenter addCaseProjectPopUpPresenter = addProjectPopUpPresenterProvider.get();
        addCaseProjectPopUpPresenter.setSuccessCallback((project) -> {
            if (project != null) {
                caseProjectService.call((Void) -> {
                                            if (addCaseProjectPopUpPresenter.getProjectCreationSuccessCallback() != null) {
                                                addCaseProjectPopUpPresenter.getProjectCreationSuccessCallback().execute(project);
                                            }
                                            if (creationSuccessCallback != null) {
                                                creationSuccessCallback.callback(project);
                                            }
                                            notification.fire(new NotificationEvent(Constants.INSTANCE.ConfigureProjectSuccess(project.getName()),
                                                                                    NotificationEvent.NotificationType.SUCCESS));
                                            addProjectPopUpPresenterProvider.destroy(addCaseProjectPopUpPresenter);
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
        });
        addCaseProjectPopUpPresenter.show();
    }

    @Override
    public void setOpenEditorOnCreation(final boolean openEditorOnCreation) {
    }

    @Override
    public void setCreationSuccessCallback(org.uberfire.client.callbacks.Callback<WorkspaceProject> creationSuccessCallback) {
        this.creationSuccessCallback = creationSuccessCallback;
    }

    @Override
    public boolean isProjectAsset() {
        return false;
    }
}
