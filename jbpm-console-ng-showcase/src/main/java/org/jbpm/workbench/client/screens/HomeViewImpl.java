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

package org.jbpm.workbench.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.client.i18n.Constants;
import org.jbpm.workbench.client.perspectives.ProjectAuthoringPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

@Dependent
@Templated(value = "HomeViewImpl.html")
public class HomeViewImpl extends Composite implements HomePresenter.HomeView {

    @Inject
    private PlaceManager placeManager;

    @Inject
    public User identity;

    @DataField
    public Image carouselImg0;

    @DataField
    public Image carouselImg1;

    @DataField
    public Image carouselImg2;

    @DataField
    public Image carouselImg3;

    @DataField
    public Image carouselImg4;

    @DataField
    public Image carouselImg5;

    @Inject
    @DataField
    public Anchor authoringLabel;

    @Inject
    @DataField
    public Anchor workLabel;

    @Inject
    @DataField
    public Anchor dashboardsLabel;

    @Inject
    @DataField
    public Anchor modelProcessAnchor;

    @Inject
    @DataField
    public Anchor workTaskListAnchor;

    @Inject
    @DataField
    public Anchor workProcessDefinitionsAnchor;

    @Inject
    @DataField
    public Anchor workProcessInstancesAnchor;

    @Inject
    @DataField
    public Anchor processDashboardsAnchor;

    @Inject
    @DataField
    public Label thejBPMCycle;

    @Inject
    @DataField
    public Label discoverLabel;

    @Inject
    @DataField
    public Label discoverTextLabel;

    @Inject
    @DataField
    public Label designLabel;

    @Inject
    @DataField
    public Label designTextLabel;

    @Inject
    @DataField
    public Label deployLabel;

    @Inject
    @DataField
    public Label deployTextLabel;

    @Inject
    @DataField
    public Label workTasksLabel;

    @Inject
    @DataField
    public Label workTasksTextLabel;

    @Inject
    @DataField
    public Label dashboardsCarrouselLabel;

    @Inject
    @DataField
    public Label dashboardsCarrouselTextLabel;

    @Inject
    @DataField
    public Label improveLabel;

    @Inject
    @DataField
    public Label improveTextLabel;

    @Inject
    private Event<NotificationEvent> notification;
    private Constants constants = GWT.create( Constants.class );

    public HomeViewImpl() {
        carouselImg5 = new Image();
        carouselImg4 = new Image();
        carouselImg3 = new Image();
        carouselImg2 = new Image();
        carouselImg1 = new Image();
        carouselImg0 = new Image();
    }

    @Override
    public void init( final HomePresenter presenter ) {
        String url = GWT.getModuleBaseURL();
        // avatar.setUrl(url + "images/avatars/" + identity.getName() + ".png");
        // avatar.setSize("64px", "64px");

        carouselImg5.setUrl( url + "images/mountain.jpg" );
        carouselImg4.setUrl( url + "images/mountain.jpg" );
        carouselImg3.setUrl( url + "images/mountain.jpg" );
        carouselImg2.setUrl( url + "images/mountain.jpg" );
        carouselImg1.setUrl( url + "images/mountain.jpg" );
        carouselImg0.setUrl( url + "images/mountain.jpg" );

        authoringLabel.setTextContent( constants.Authoring() );
        modelProcessAnchor.setTextContent( constants.Business_Processes() );
        workLabel.setTextContent( constants.Work() );
        workTaskListAnchor.setTextContent( constants.Tasks_List() );
        workProcessDefinitionsAnchor.setTextContent( constants.Process_Definitions() );
        workProcessInstancesAnchor.setTextContent( constants.Process_Instances() );
        dashboardsLabel.setTextContent( constants.Dashboards() );
        processDashboardsAnchor.setTextContent( constants.Process_Dashboard() );
        thejBPMCycle.setTextContent( constants.The_jBPM_Cycle() );

        discoverLabel.setTextContent( constants.Discover() );
        discoverTextLabel.setTextContent( constants.Discover_Text() );
        designLabel.setTextContent( constants.Design() );
        designTextLabel.setTextContent( constants.Design_Text() );
        deployLabel.setTextContent( constants.Deploy() );
        deployTextLabel.setTextContent( constants.Deploy_Text() );
        workTasksLabel.setTextContent( constants.Work() );
        workTasksTextLabel.setTextContent( constants.Work_Text() );
        dashboardsCarrouselLabel.setTextContent( constants.Dashboards() );
        dashboardsCarrouselTextLabel.setTextContent( constants.Dashboards_Text() );
        improveLabel.setTextContent( constants.Improve() );
        improveTextLabel.setTextContent( constants.Improve_Text() );

        modelProcessAnchor.setOnclick(e -> placeManager.goTo(new DefaultPlaceRequest(ProjectAuthoringPerspective.PERSPECTIVE_ID)));

        workTaskListAnchor.setOnclick(e -> placeManager.goTo(new DefaultPlaceRequest(DATASET_TASKS)));

        workProcessDefinitionsAnchor.setOnclick(e -> placeManager.goTo(new DefaultPlaceRequest(PROCESS_DEFINITIONS)));

        workProcessInstancesAnchor.setOnclick(e -> placeManager.goTo(new DefaultPlaceRequest(DATASET_PROC_INST_VARS)));

        processDashboardsAnchor.setOnclick(e -> placeManager.goTo(new DefaultPlaceRequest(PROCESS_DASHBOARD)));

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

}