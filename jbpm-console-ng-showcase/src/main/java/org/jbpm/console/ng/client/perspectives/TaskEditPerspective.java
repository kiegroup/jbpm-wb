/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.client.workbench.perspectives.PerspectivePartDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
public class TaskEditPerspective {

    @Perspective(identifier = "Task Edit Perspective")
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Task Edit Perspective" );
        
        p.addPart( new PerspectivePartDefinition( Position.WEST,
                                                  new PlaceRequest( "Task Details" ) ) );
        p.addPart( new PerspectivePartDefinition( Position.WEST,
                                                  new PlaceRequest( "Add Task Content" ) ) );
        p.addPart( new PerspectivePartDefinition( Position.EAST,
                                                  new PlaceRequest( "Quick New Sub Task" ) ) );
      
        

        return p;
    }

}
