/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.pr.client.editors.definition.details;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessDefStyleEvent;
import org.uberfire.backend.vfs.Path;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class BaseProcessDefDetailsPresenter {

    @Inject
    private Event<ProcessDefStyleEvent> processDefStyleEvent;

    private String currentProcessDefId = "";
    private String currentDeploymentId = "";

    public interface BaseProcessDefDetailsView extends IsWidget {

        void displayNotification( String text );

        HTML getProcessNameText();

        HTML getProcessIdText();

        HTML getDeploymentIdText();

        void setProcessAssetPath( Path processAssetPath );

        void setEncodedProcessSource( String encodedProcessSource );

        Path getProcessAssetPath();

        String getEncodedProcessSource();
    }

    protected void changeStyleRow( String processDefName, String processDefVersion ) {

        processDefStyleEvent.fire( new ProcessDefStyleEvent( processDefName, processDefVersion ) );
    }

    public void onProcessDefSelectionEvent( @Observes final ProcessDefSelectionEvent event ) {
        this.currentProcessDefId = event.getProcessId();
        this.currentDeploymentId = event.getDeploymentId();
        refreshView( currentProcessDefId, currentDeploymentId );
        refreshProcessDef( currentDeploymentId, currentProcessDefId );
    }

    public abstract IsWidget getWidget();

    protected abstract void refreshView( String currentProcessDefId, String currentDeploymentId );

    protected abstract void refreshProcessDef( final String deploymentId, final String processId );
}
