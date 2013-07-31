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
package org.jbpm.console.ng.pr.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.client.events.ProcessDefinitionsSearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.OnStart;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Process Definitions With Details", isDefault = false)
public class ProcessDefinitionsSplitPerspective {

    @Inject
    private ContextualSearch contextualSearch;
    
    @Inject
    private Event<ProcessDefinitionsSearchEvent> searchEvents;
    
    private String selectedProcessId = "";
    
    private String selectedProcessDeploymentId = "";
    
    
    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(PanelType.ROOT_LIST);
        p.setName( "Process Definitions" );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Process Definition List" ) ) );
        
        final PanelDefinition east = new PanelDefinitionImpl(PanelType.MULTI_LIST);
        east.setWidth( 500 );
        east.setMinWidth( 400 );
        
        
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest( "Process Definition Details" );
        defaultPlaceRequest.addParameter( "processId", selectedProcessId );
        defaultPlaceRequest.addParameter( "deploymentId", selectedProcessDeploymentId );
        east.addPart( new PartDefinitionImpl( defaultPlaceRequest ) );
        p.getRoot().insertChild( Position.EAST, east );
        
        
        p.setTransient( true );
        return p;
    }
    
    @OnStart
    public void onStart(final PlaceRequest place) {
        this.selectedProcessId = place.getParameter( "processId", "" );
        this.selectedProcessDeploymentId= place.getParameter( "deploymentId", "none" );
        contextualSearch.setSearchBehavior(new SearchBehavior() {

            @Override
            public void execute(String searchFilter) {
                searchEvents.fire(new ProcessDefinitionsSearchEvent(searchFilter));
            }
 
        });
        
    }
    

}
