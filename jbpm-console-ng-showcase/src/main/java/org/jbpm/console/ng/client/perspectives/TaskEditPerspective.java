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
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
public class TaskEditPerspective {

    @Perspective(identifier = "Task Edit Perspective")
    public PerspectiveDefinition getPerspective() {
        
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "Task Edit Perspective" );
        p.getRoot().addPart( new PartDefinitionImpl( new PlaceRequest( "Task Details" ) ) );
        final PanelDefinition eastPanel = new PanelDefinitionImpl();
        eastPanel.setWidth( 300 );
 	eastPanel.setMinWidth( 200 );
        eastPanel.setWidth(800);
        eastPanel.addPart( new PartDefinitionImpl( new PlaceRequest( "Add Task Content") ) );      
        final PanelDefinition westPanelSouthPanel = new PanelDefinitionImpl();
        westPanelSouthPanel.addPart( new PartDefinitionImpl( new PlaceRequest(  "Quick New Sub Task" ) ) );
        westPanelSouthPanel.setHeight(400);
        westPanelSouthPanel.setMinHeight(200);
        westPanelSouthPanel.setWidth(800);
        eastPanel.setChild( Position.SOUTH , westPanelSouthPanel);
        p.getRoot().setChild( Position.EAST , eastPanel );
        return p;
  
    }

}
