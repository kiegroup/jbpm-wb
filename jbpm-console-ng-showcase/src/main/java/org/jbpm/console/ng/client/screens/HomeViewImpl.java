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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.client.i18n.Constants;
import org.jbpm.console.ng.client.perspectives.ProjectAuthoringPerspective;
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

        authoringLabel.setText( constants.Authoring() );
        modelProcessAnchor.setText( constants.Business_Processes() );
        workLabel.setText( constants.Work() );
        workTaskListAnchor.setText( constants.Tasks_List() );
        workProcessDefinitionsAnchor.setText( constants.Process_Definitions() );
        workProcessInstancesAnchor.setText( constants.Process_Instances() );
        dashboardsLabel.setText( constants.Dashboards() );
        processDashboardsAnchor.setText( constants.Process_Dashboard() );
        thejBPMCycle.setText( constants.The_jBPM_Cycle() );
        thejBPMCycle.setStyleName( "" );

        discoverLabel.setText( constants.Discover() );
        discoverLabel.setStyleName( "" );
        discoverTextLabel.setText( constants.Discover_Text() );
        discoverTextLabel.setStyleName( "" );
        designLabel.setText( constants.Design() );
        designLabel.setStyleName( "" );
        designTextLabel.setText( constants.Design_Text() );
        designTextLabel.setStyleName( "" );
        deployLabel.setText( constants.Deploy() );
        deployLabel.setStyleName( "" );
        deployTextLabel.setText( constants.Deploy_Text() );
        deployTextLabel.setStyleName( "" );
        workTasksLabel.setText( constants.Work() );
        workTasksLabel.setStyleName( "" );
        workTasksTextLabel.setText( constants.Work_Text() );
        workTasksTextLabel.setStyleName( "" );
        dashboardsCarrouselLabel.setText( constants.Dashboards() );
        dashboardsCarrouselLabel.setStyleName( "" );
        dashboardsCarrouselTextLabel.setText( constants.Dashboards_Text() );
        dashboardsCarrouselTextLabel.setStyleName( "" );
        improveLabel.setText( constants.Improve() );
        improveLabel.setStyleName( "" );
        improveTextLabel.setText( constants.Improve_Text() );
        improveTextLabel.setStyleName( "" );

        modelProcessAnchor.addClickHandler(e -> placeManager.goTo(new DefaultPlaceRequest(ProjectAuthoringPerspective.PERSPECTIVE_ID)));

        workTaskListAnchor.addClickHandler(e -> placeManager.goTo(new DefaultPlaceRequest(DATASET_TASKS)));

        workProcessDefinitionsAnchor.addClickHandler(e -> placeManager.goTo(new DefaultPlaceRequest(PROCESS_DEFINITIONS)));

        workProcessInstancesAnchor.addClickHandler(e -> placeManager.goTo(new DefaultPlaceRequest(DATASET_PROC_INST_VARS)));

        processDashboardsAnchor.addClickHandler(e -> placeManager.goTo(new DefaultPlaceRequest(PROCESS_DASHBOARD)));

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

}