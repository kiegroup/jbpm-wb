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
package org.jbpm.console.ng.gc.forms.client.display.standalone;

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
@WorkbenchPerspective(identifier = "FormDisplayPerspective")
public class FormDisplayPerspective {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );

        perspective.setName( "FormDisplayPerspective" );

        DefaultPlaceRequest request;

        Map<String, List<String>> parameterMap = Window.Location.getParameterMap();
        String taskId = "-1";
        if ( parameterMap.containsKey( "taskId" ) && !parameterMap.get( "taskId" ).isEmpty() ) {
            taskId = parameterMap.get( "taskId" ).get( 0 );
        }

        if ( !taskId.equals( "-1" ) ) {
            request= new DefaultPlaceRequest( "Standalone Task Form Display" );
            request.addParameter( "taskId", taskId );
        } else {
            request = new DefaultPlaceRequest( "Standalone Process Form Display" );
            String processId = "none";
            if ( parameterMap.containsKey( "processId" ) && !parameterMap.get( "processId" ).isEmpty() ) {
                processId = parameterMap.get( "processId" ).get( 0 );
            }

            String domainId = "none";
            if ( parameterMap.containsKey( "domainId" ) && !parameterMap.get( "domainId" ).isEmpty() ) {
                domainId = parameterMap.get( "domainId" ).get( 0 );
            }

            if ( !processId.equals( "none" ) && !processId.equals( "domainId" ) ) {
                request.addParameter( "processId", processId );
                request.addParameter( "domainId", domainId );
            }

        }
        String opener = "none";
        if ( parameterMap.containsKey( "opener" ) && !parameterMap.get( "opener" ).isEmpty() ) {
            opener = parameterMap.get( "opener" ).get( 0 );
        }
        request.addParameter( "opener", opener );
        perspective.getRoot().addPart( new PartDefinitionImpl( request ) );

        return perspective;
    }

}
