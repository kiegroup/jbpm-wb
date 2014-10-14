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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.generic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

/**
 * @author salaboy
 */
@Dependent
@WorkbenchScreen(identifier = "Generic Form Display")
public class ScreenGenericFormDisplayPresenter {

    @Inject
    private GenericFormDisplayPresenter widgetPresenter;

    @Inject
    private PlaceManager placeManager;

    protected String placeOnClose;

    protected PlaceRequest place;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Form();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return widgetPresenter.getView();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        placeOnClose = place.getParameter( "onClose", "none" );
        widgetPresenter.setup( Long.parseLong( place.getParameter( "taskId", "-1" ) ),
                               place.getParameter( "processId", "none" ),
                               place.getParameter( "domainId", "none" ),
                               new Command() {
                                   @Override
                                   public void execute() {
                                       if ( !placeOnClose.equals( "none" ) ) {
                                           placeManager.closePlace( place );
                                           placeManager.forceClosePlace( placeOnClose );
                                       } else {
                                           placeManager.closePlace( place );
                                       }
                                   }
                               } );
    }

    @OnClose
    public void OnClose() {
        widgetPresenter.cleanup();
    }

}
