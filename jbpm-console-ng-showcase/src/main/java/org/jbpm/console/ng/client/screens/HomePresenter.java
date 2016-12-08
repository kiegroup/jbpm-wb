/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.client.screens;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jbpm.console.ng.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = HomePresenter.SCREEN_ID)
public class HomePresenter {

    public static final String SCREEN_ID = "Home Screen";

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
            view.displayNotification(constants.ActionNotImplementedYet() );
            return;
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( locatedAction );

        placeManager.goTo( placeRequestImpl );
    }

}
