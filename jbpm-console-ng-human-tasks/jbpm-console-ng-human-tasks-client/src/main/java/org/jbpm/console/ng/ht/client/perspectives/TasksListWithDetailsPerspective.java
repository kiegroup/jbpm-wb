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
package org.jbpm.console.ng.ht.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.console.ng.ht.model.events.SearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.lifecycle.OnStartup;

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
@WorkbenchPerspective(identifier = "Tasks With Details", isDefault = false)
public class TasksListWithDetailsPerspective {

    @Inject
    private ContextualSearch contextualSearch;
    
    @Inject
    private Event<SearchEvent> searchEvents;
    
    private String selectedTaskId = "";
    
    private String selectedTaskName = "";
    
    private String currentView = "";
    
    private String currentTaskType = "";
    
    private String currentDate = "";
    
    private String currentFilter = "";
    
    @Perspective
    public PerspectiveDefinition getPerspective() {
        
        
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(PanelType.ROOT_LIST);
        p.setName( "Tasks" );
        DefaultPlaceRequest taskListPlaceRequest = new DefaultPlaceRequest( "Tasks List" );
        p.getRoot().addPart( new PartDefinitionImpl( taskListPlaceRequest ) );
        
        
        p.setTransient( true );
        return p;
    }
    
    @OnStartup
    public void onStartup(final PlaceRequest place) {
        contextualSearch.setSearchBehavior(new SearchBehavior() {
            @Override
            public void execute(String searchFilter) {
                searchEvents.fire(new SearchEvent(searchFilter));
            }
           
        });
        
    }

}
