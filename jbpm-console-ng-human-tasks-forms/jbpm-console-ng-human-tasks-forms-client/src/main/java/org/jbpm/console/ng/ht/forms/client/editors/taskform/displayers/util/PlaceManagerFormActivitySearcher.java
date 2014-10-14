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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util;

import com.google.gwt.user.client.ui.HasWidgets;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import java.util.HashMap;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * @author pefernan
 */
@Dependent
public class PlaceManagerFormActivitySearcher {

    @Inject
    private PlaceManager placeManager;


    public void findFormActivityWidget( String name,
                                            Map<String, String> params, HasWidgets widget ) {

        if(params == null){
            params = new HashMap<String, String>();
        }
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(name + " Form", params);
        widget.clear();
        PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
        if(status.equals(PlaceStatus.OPEN)){
            placeManager.closePlace(defaultPlaceRequest);
            placeManager.forceClosePlace(defaultPlaceRequest);
        }
        placeManager.goTo(defaultPlaceRequest, widget);

    }

}
