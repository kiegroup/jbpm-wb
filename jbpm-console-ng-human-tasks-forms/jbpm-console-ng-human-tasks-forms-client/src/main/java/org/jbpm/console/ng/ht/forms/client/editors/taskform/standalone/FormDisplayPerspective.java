/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.forms.client.editors.taskform.standalone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@WorkbenchPerspective(identifier = "FormDisplayPerspective")
public class FormDisplayPerspective {
    private Constants constants = GWT.create(Constants.class);
    private PerspectiveDefinition perspective;

    @PostConstruct
    public void init() {
        buildPerspective();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return perspective;
    }

    private void buildPerspective() {
        perspective = new PerspectiveDefinitionImpl(PanelType.ROOT_STATIC);
        perspective.setName("FormDisplayPerspective");
        perspective.setTransient(true);

        DefaultPlaceRequest request = new DefaultPlaceRequest("Generic Form Display");

        Map<String, List<String>> parameterMap = Window.Location.getParameterMap();
        String taskId = "-1";
        if (parameterMap.containsKey("taskId") && !parameterMap.get("taskId").isEmpty()) taskId = parameterMap.get("taskId").get(0);

        if (!taskId.equals("-1")) {
            request.addParameter("taskId", taskId);
        } else {
            String processId = "none";
            if (parameterMap.containsKey("processId") && !parameterMap.get("processId").isEmpty()) processId = parameterMap.get("processId").get(0);

            String domainId = "none";
            if (parameterMap.containsKey("domainId") && !parameterMap.get("domainId").isEmpty()) domainId = parameterMap.get("domainId").get(0);

            if (!processId.equals("none") && !processId.equals("domainId")) {
                request.addParameter("processId", processId);
                request.addParameter("domainId", domainId);
            }

        }
        String opener = "none";
        if (parameterMap.containsKey("opener") && !parameterMap.get("opener").isEmpty()) opener = parameterMap.get("opener").get(0);
        request.addParameter("opener", opener);
        this.perspective.getRoot().addPart( new PartDefinitionImpl( request ) );
    }
}
