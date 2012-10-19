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

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Form Builder Perspective")
public class FormBuilderPerspective {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "Form Builder Perspective" );
        PlaceRequest palette = new DefaultPlaceRequest( "Form Builder - Palette" );
        p.getRoot().addPart( new PartDefinitionImpl( palette ) );
        final PanelDefinition eastPanel = new PanelDefinitionImpl();
        eastPanel.setWidth( 800 );
 	eastPanel.setMinWidth( 480 );
        PlaceRequest canvasContent = new DefaultPlaceRequest( "Form Builder - Canvas");
        eastPanel.addPart( new PartDefinitionImpl( canvasContent ) );      
        
        final PanelDefinition east2Panel = new PanelDefinitionImpl();
        east2Panel.setWidth( 200 );
 	east2Panel.setMinWidth( 180 );
        PlaceRequest propertiesContent = new DefaultPlaceRequest( "Form Builder - Properties");
        east2Panel.addPart( new PartDefinitionImpl( propertiesContent ) );      
        
        p.getRoot().setChild( Position.EAST , east2Panel );
        p.getRoot().setChild( Position.EAST , eastPanel );
        p.setTransient(true);
        return p;
  
    }

}
