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
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayProvider;
import org.jbpm.workbench.forms.client.display.views.display.EmbeddedFormDisplayer;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.api.HumanTaskDisplayerConfig;
import org.jbpm.workbench.ht.model.TaskKey;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = StandaloneTaskFormDisplayPresenter.SCREEN_ID)
public class StandaloneTaskFormDisplayPresenter {

    public static final String SCREEN_ID = "Standalone Task Form Display";

    protected PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private EmbeddedFormDisplayer displayer;

    @Inject
    private HumanTaskFormDisplayProvider humanTaskFormDisplayProvider;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Form();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return displayer;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {

        final String placeOnClose = place.getParameter(StandaloneConstants.ON_CLOSE_PARAM, null);

        final String serverTemplate = place.getParameter(StandaloneConstants.SERVER_TEMPLATE_PARAM, null);
        final String domainId = place.getParameter(StandaloneConstants.DOMAIN_ID_PARAM, null);

        final String taskId = place.getParameter(StandaloneConstants.TASK_ID_PARAM, null);

        final String opener = place.getParameter(StandaloneConstants.OPENER_PARAM, null);

        displayer.setOnCloseCommand(() -> {
            if (null != placeOnClose) {
                placeManager.closePlace(place);
                placeManager.forceClosePlace(placeOnClose);
            } else {
                placeManager.closePlace(place);
            }
        });

        if (null != taskId) {
            TaskKey key = new TaskKey(serverTemplate, domainId, Long.decode(taskId));
            HumanTaskDisplayerConfig config = new HumanTaskDisplayerConfig(key);
            config.setFormOpener(opener);
            humanTaskFormDisplayProvider.setup(config, displayer);
        }
    }
}
