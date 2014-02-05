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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.bh.client.i18n.Constants;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "HomeViewImpl.html")
public class HomeViewImpl extends Composite implements HomePresenter.HomeView {

    private HomePresenter presenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    public Identity identity;

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

    // @Inject
    // @DataField
    // public IconAnchor discoverLabel;

    @Inject
    @DataField
    public IconAnchor authoringLabel;

    // @Inject
    // @DataField
    // public IconAnchor deployLabel;
    @Inject
    @DataField
    public IconAnchor workLabel;

    @Inject
    @DataField
    public IconAnchor dashboardsLabel;
    // @Inject
    // @DataField
    // public IconAnchor improveLabel;

    @Inject
    @DataField
    public IconAnchor modelProcessAnchor;

    @Inject
    @DataField
    public IconAnchor workTaskListAnchor;

    @Inject
    @DataField
    public IconAnchor workProcessDefinitionsAnchor;

    @Inject
    @DataField
    public IconAnchor workProcessInstancesAnchor;
    // @Inject
    // @DataField
    // public IconAnchor deployIdentityAnchor;
    @Inject
    @DataField
    public IconAnchor processDashboardsAnchor;

    @Inject
    @DataField
    public IconAnchor businessDashboardsAnchor;

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

    // @Inject
    // @DataField
    // public IconAnchor deployJobsAnchor;

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
        this.presenter = presenter;
        String url = GWT.getHostPageBaseURL();
        // avatar.setUrl(url + "images/avatars/" + identity.getName() + ".png");
        // avatar.setSize("64px", "64px");
        List<Role> roles = identity.getRoles();

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
        processDashboardsAnchor.setText(constants.Process_Dashboard());
        businessDashboardsAnchor.setText(constants.Business_Dashboard());
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

        modelProcessAnchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Authoring" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        workTaskListAnchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Tasks List" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        workProcessDefinitionsAnchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Definition List" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        workProcessInstancesAnchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instances" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        processDashboardsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "DashboardPerspective" );
                placeManager.goTo( placeRequestImpl );
            }
        });

        final String dashbuilderURL = DashboardURLBuilder.getDashboardURL("/dashbuilder/workspace", null, LocaleInfo.getCurrentLocale().getLocaleName());
        businessDashboardsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open( dashbuilderURL, "_blank", "" );
            }
        });

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

}
