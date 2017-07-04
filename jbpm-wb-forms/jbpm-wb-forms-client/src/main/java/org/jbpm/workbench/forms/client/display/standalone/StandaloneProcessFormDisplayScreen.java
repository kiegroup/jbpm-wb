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
package org.jbpm.workbench.forms.client.display.standalone;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.forms.client.display.api.StartProcessFormDisplayProvider;
import org.jbpm.workbench.forms.client.display.views.EmbeddedFormDisplayView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Standalone Process Form Display")
public class StandaloneProcessFormDisplayScreen {

    protected String placeOnClose;

    protected PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private EmbeddedFormDisplayView view;

    @Inject
    private StartProcessFormDisplayProvider processFormDisplayProvider;

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view.getView();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {

        placeOnClose = place.getParameter("onClose",
                                          "none");

        String currentProcessId = place.getParameter("processId",
                                                     "none");
        String currentDeploymentId = place.getParameter("domainId",
                                                        "none");
        String opener = place.getParameter("opener",
                                           null);

        view.setOnCloseCommand(new Command() {
            @Override
            public void execute() {
                if (!placeOnClose.equals("none")) {
                    placeManager.closePlace(place);
                    placeManager.forceClosePlace(placeOnClose);
                } else {
                    placeManager.closePlace(place);
                }
            }
        });

        if (!currentProcessId.equals("none")) {
            ProcessDefinitionKey key = new ProcessDefinitionKey(null,
                                                                currentDeploymentId,
                                                                currentProcessId);
            ProcessDisplayerConfig config = new ProcessDisplayerConfig(key,
                                                                       "");
            config.setFormOpener(opener);
            processFormDisplayProvider.setup(config,
                                             view);
        }
    }
}
