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
package org.jbpm.console.ng.gc.client.experimental.details;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 * @author salaboy
 */
public abstract class AbstractTabbedDetailsPresenter {

    @Inject
    protected PlaceManager placeManager;

    protected PlaceRequest place;

    protected Map<String, AbstractWorkbenchActivity> activitiesMap =
            new HashMap<String, AbstractWorkbenchActivity>();

    protected String deploymentId = "";

    protected String processId = "";

    @OnOpen
    public void onOpen() {

    }

    @OnFocus
    public void onFocus() {

    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;

    }

    @OnClose
    public void onClose() {
        for ( String activityId : activitiesMap.keySet() ) {
            activitiesMap.get( activityId ).onClose();
        }
        activitiesMap.clear();
    }

    public void closeDetails() {
        placeManager.closePlace( place );
    }

}
