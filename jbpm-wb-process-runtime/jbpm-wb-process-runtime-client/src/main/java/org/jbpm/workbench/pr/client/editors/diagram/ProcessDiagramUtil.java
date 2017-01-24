/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workbench.pr.client.editors.diagram;

import org.jbpm.workbench.common.model.process.DummyProcessPath;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

public final class ProcessDiagramUtil {

    public static PlaceRequest buildPlaceRequest( final PlaceRequest input ) {
        final String deploymentId = input.getParameter( "deploymentId", "" );
        final String processId = input.getParameter( "processId", "" );
        final String activeNodes = input.getParameter( "activeNodes", "" );
        final String completedNodes = input.getParameter( "completedNodes", "" );

        final PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest( new DummyProcessPath( processId ), "jbpm.designer.popup" );
        //Set Parameters here:
        defaultPlaceRequest.addParameter( "readOnly", "true" );
        if ( !activeNodes.equals( "" ) ) {
            defaultPlaceRequest.addParameter( "activeNodes", activeNodes );
        }
        if ( !completedNodes.equals( "" ) ) {
            defaultPlaceRequest.addParameter( "completedNodes", completedNodes );
        }
        defaultPlaceRequest.addParameter( "processId", processId );
        defaultPlaceRequest.addParameter( "deploymentId", deploymentId );
        return defaultPlaceRequest;
    }

    public static PlaceRequest buildPlaceRequest( String serverTemplateId, String deploymentId, String processId ) {
        final PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest( new DummyProcessPath( processId ), "jBPM Process Diagram" );

        defaultPlaceRequest.addParameter( "serverTemplateId", serverTemplateId );
        defaultPlaceRequest.addParameter( "processId", processId );
        defaultPlaceRequest.addParameter( "containerId", deploymentId );
        return defaultPlaceRequest;
    }

    public static PlaceRequest buildPlaceRequest( String serverTemplateId, String deploymentId, Long processInstanceId ) {
        final PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest( new DummyProcessPath( processInstanceId.toString() ), "jBPM Process Diagram" );

        defaultPlaceRequest.addParameter( "serverTemplateId", serverTemplateId );
        defaultPlaceRequest.addParameter( "processInstanceId", processInstanceId.toString() );
        defaultPlaceRequest.addParameter( "containerId", deploymentId );
        return defaultPlaceRequest;
    }
}
