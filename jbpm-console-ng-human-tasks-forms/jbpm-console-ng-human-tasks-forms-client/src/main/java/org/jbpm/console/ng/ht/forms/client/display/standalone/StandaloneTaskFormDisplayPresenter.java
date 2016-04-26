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
package org.jbpm.console.ng.ht.forms.client.display.standalone;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.gc.forms.client.display.views.EmbeddedFormDisplayView;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.jbpm.console.ng.ht.forms.client.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Standalone Task Form Display")
public class StandaloneTaskFormDisplayPresenter {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private EmbeddedFormDisplayView view;

    @Inject
    private HumanTaskFormDisplayProvider humanTaskFormDisplayProvider;

    protected String placeOnClose;

    protected PlaceRequest place;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Form();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view.getView();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {

        placeOnClose = place.getParameter( "onClose", "none" );

        Long currentTaskId = Long.parseLong( place.getParameter( "taskId", "-1" ) );
        String opener = place.getParameter("opener", null);

        view.setOnCloseCommand( new Command() {
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

        if (currentTaskId != -1) {
            TaskKey key = new TaskKey(currentTaskId);
            HumanTaskDisplayerConfig config = new HumanTaskDisplayerConfig(key);
            config.setFormOpener(opener);
            humanTaskFormDisplayProvider.setup(config, view);
        }
    }

}
