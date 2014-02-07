package org.jbpm.console.ng.ht.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.ht.model.events.SearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "Identity", isDefault = false)
public class IdentityPerspective {

    @Inject
    private ContextualSearch contextualSearch;
    
    @Inject
    private Event<SearchEvent> searchEvents;
    
    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setName( "Users" );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Users and Groups" ) ) );
        p.setTransient( true );
        return p;
    }
    
    @OnStartup
    public void init() {
        contextualSearch.setSearchBehavior(new SearchBehavior() {
            @Override
            public void execute(String searchFilter) {
                searchEvents.fire(new SearchEvent(searchFilter));
            }
           
        });
        
    }

    
}
