/*
 * Copyright 2012 JBoss Inc
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

package org.jbpm.console.ng.bh.client.editors.home;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jbpm.console.ng.bh.client.i18n.Constants;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Home Screen")
public class HomePresenter {

    public interface HomeView extends UberView<HomePresenter> {

        void displayNotification( String text );

    }

    @Inject
    private PlaceManager placeManager;

    private Constants constants = GWT.create( Constants.class );

    @Inject
    HomeView view;
    // Retrieve the actions from a service
    Map<String, String> actions = new HashMap<String, String>();

    @PostConstruct
    public void init() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Home();
    }

    @WorkbenchPartView
    public UberView<HomePresenter> getView() {
        return view;
    }

    public void doAction( String action ) {
        String locatedAction = actions.get( action );
        if ( locatedAction == null || locatedAction.equals( "" ) ) {
            view.displayNotification( " Action Not Implemented Yet!" );
            return;
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( locatedAction );
        // placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));

        placeManager.goTo( placeRequestImpl );
    }

    @OnOpen
    public void onOpen() {

    }

}
