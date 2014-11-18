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
package org.jbpm.console.ng.gc.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;
import org.uberfire.lifecycle.OnStartup;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Experimental Paging")
public class ExperimentalPagingPerspective extends FlowPanel {

    @Inject
    private ContextualSearch contextualSearch;

    @Inject
    private Event<SearchEvent> searchEvents;

    @Inject
    @WorkbenchPanel(parts = "Pagination For Tables")
    FlowPanel paginationTables;

    @PostConstruct
    private void init() {
        Layouts.setToFillParent( paginationTables );
        add( paginationTables );
    }

    @OnStartup
    public void onStartup() {
        contextualSearch.setSearchBehavior( new SearchBehavior() {
            @Override
            public void execute( String searchFilter ) {
                searchEvents.fire( new SearchEvent( searchFilter ) );
            }

        } );

    }
}
