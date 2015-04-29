/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.gc.forms.client.display.displayers.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.debug.Debug;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author pefernan
 */
@Dependent
public class PlaceManagerFormActivitySearcher {

    private static IdentityHashMap<Panel, Integer> customContainers = new IdentityHashMap<Panel, Integer>();

    @Inject
    private PlaceManager placeManager;


    public void findFormActivityWidget( String name, HasWidgets widget ) {
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(name, new HashMap<String, String>());

        final Panel container = (Panel) widget;
        if ( customContainers.get( container ) == null ) {

            //System.out.println("Got new custom container " + Debug.objectId( widget ));
            container.getElement().addClassName( "custom-container-" + Debug.toMemorableString( System.identityHashCode( widget ) ) + "-" + customContainers.size() );
            customContainers.put( container, customContainers.size() );
            container.addAttachHandler( new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach( AttachEvent event ) {
                    //new Exception( "Container attached " + event.isAttached() + ": " + container.getElement().getId()).printStackTrace(System.out);
                }
            } );
        } else {
            //System.out.println("Reusing custom container " + container.getElement().getAttribute( "class" ));
        }
        widget.clear();
        PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
        if(status.equals(PlaceStatus.OPEN)){
            placeManager.closePlace(defaultPlaceRequest);
            placeManager.forceClosePlace(defaultPlaceRequest);
        }
        placeManager.goTo(defaultPlaceRequest, widget);

    }

}
