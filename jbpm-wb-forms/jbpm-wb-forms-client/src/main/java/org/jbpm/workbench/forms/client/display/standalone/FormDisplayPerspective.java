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
package org.jbpm.workbench.forms.client.display.standalone;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Window;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = FormDisplayPerspective.PERSPECTIVE_ID)
public class FormDisplayPerspective {

    public static final String PERSPECTIVE_ID = "FormDisplayPerspective";

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());

        perspective.setName(PERSPECTIVE_ID);

        DefaultPlaceRequest request = new DefaultPlaceRequest();

        Map<String, List<String>> parameterMap = Window.Location.getParameterMap();

        String taskId = readParameter(StandaloneConstants.TASK_ID_PARAM, parameterMap);

        if (null != taskId) {
            request.setIdentifier(StandaloneTaskFormDisplayPresenter.SCREEN_ID);
            request.addParameter(StandaloneConstants.TASK_ID_PARAM, taskId);
        } else {
            request = new DefaultPlaceRequest(StandaloneProcessFormDisplayScreen.SCREEN_ID);

            String processId = readParameter(StandaloneConstants.PROCESS_ID_PARAM, parameterMap);

            if (null != processId) {
                request.addParameter(StandaloneConstants.PROCESS_ID_PARAM, processId);
            }
        }

        String serverTemplate = readParameter(StandaloneConstants.SERVER_TEMPLATE_PARAM, parameterMap);
        String domainId = readParameter(StandaloneConstants.DOMAIN_ID_PARAM, parameterMap);
        String opener = readParameter(StandaloneConstants.OPENER_PARAM, parameterMap);

        request.addParameter(StandaloneConstants.SERVER_TEMPLATE_PARAM, serverTemplate);
        request.addParameter(StandaloneConstants.DOMAIN_ID_PARAM, domainId);
        request.addParameter(StandaloneConstants.OPENER_PARAM, opener);

        perspective.getRoot().addPart(new PartDefinitionImpl(request));

        return perspective;
    }

    private String readParameter(String paramName, Map<String, List<String>> params) {
        List<String> paramValue = params.get(paramName);

        if (paramValue != null && !paramValue.isEmpty()) {
            return paramValue.get(0);
        }

        return null;
    }
}
