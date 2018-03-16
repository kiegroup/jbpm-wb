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

package org.jbpm.workbench.common.client.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@Dependent
public class ManageSelectorImpl implements ManageSelector {

    private static final Map<String, String> availablePerspectives;

    @Inject
    ManageSelectorView view;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private User identity;

    @Inject
    private PlaceManager placeManager;

    static {
        Map<String, String> perspectives = new HashMap<>();

        perspectives.put(PerspectiveIds.PROCESS_DEFINITIONS,
                         Constants.INSTANCE.Process_Definitions());
        perspectives.put(PerspectiveIds.PROCESS_INSTANCES,
                         Constants.INSTANCE.Process_Instances());
        perspectives.put(PerspectiveIds.TASKS_ADMIN,
                         Constants.INSTANCE.Tasks());
        perspectives.put(PerspectiveIds.EXECUTION_ERRORS,
                         Constants.INSTANCE.ExecutionErrors());
        perspectives.put(PerspectiveIds.JOBS,
                         Constants.INSTANCE.Jobs());

        availablePerspectives = Collections.unmodifiableMap(perspectives);
    }

    @PostConstruct
    public void init() {
        view.removeAllOptions();
        String currentPerspectiveId = perspectiveManager.getCurrentPerspective().getIdentifier();
        availablePerspectives.keySet().forEach(perspectiveId -> addPerspective(perspectiveId,
                                                                               availablePerspectives.get(perspectiveId),
                                                                               perspectiveId.equals(currentPerspectiveId)));

        view.setOptionChangeCommand(() -> goToPerspective(view.getSelectedOption()));
        view.refresh();
    }

    private boolean hasAccessToPerspective(String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                  ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              identity);
    }

    protected void addPerspective(String perspectiveId,
                                  String optionLabel,
                                  boolean selected) {
        if (hasAccessToPerspective(perspectiveId) &&
                !activityManager.getActivities(new DefaultPlaceRequest(perspectiveId)).isEmpty()) {
            view.addOption(optionLabel,
                           perspectiveId,
                           selected);
        }
    }

    public void goToPerspective(String perspectiveId) {
        if (perspectiveId != null && !perspectiveId.isEmpty()) {
            placeManager.goTo(new DefaultPlaceRequest(perspectiveId));
        }
    }

    public IsWidget getManageSelectorWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public interface ManageSelectorView extends IsElement {

        void addOption(String label,
                       String value,
                       boolean selected);

        void removeAllOptions();

        void setOptionChangeCommand(Command changeCommand);

        void refresh();

        String getSelectedOption();
    }
}