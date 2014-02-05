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

package org.jbpm.console.ng.bd.client.editors.deployment.newunit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.events.DeployedUnitChangedEvent;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "New Deployment")
public class NewDeploymentPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface NewDeploymentView extends UberView<NewDeploymentPresenter> {

        void displayNotification( String text );

        void showBusyIndicator( String message );

        void hideBusyIndicator();

        TextBox getGroupText();

        void cleanForm();
    }

    @Inject
    private NewDeploymentView view;

    @Inject
    private Identity identity;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @Inject
    private Event<DeployedUnitChangedEvent> unitChanged;

    private PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Deploy_A_New_Unit();
    }

    @WorkbenchPartView
    public UberView<NewDeploymentPresenter> getView() {
        return view;
    }

    public NewDeploymentPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void deployUnit( final String group,
                            final String artifact,
                            final String version,
                            final String kbaseName,
                            final String kieSessionName,
                            String strategy ) {
        view.showBusyIndicator( constants.Please_Wait() );
        deploymentManager.call( new RemoteCallback<Void>() {
                                    @Override
                                    public void callback( Void nothing ) {
                                        view.cleanForm();
                                        view.hideBusyIndicator();
                                        view.displayNotification( " Kjar Deployed " + group + ":" + artifact + ":" + version );
                                        unitChanged.fire( new DeployedUnitChangedEvent() );
                                        close();
                                    }
                                }, new ErrorCallback<Message>() {
                                    @Override
                                    public boolean error( Message message,
                                                          Throwable throwable ) {
                                        view.cleanForm();
                                        view.hideBusyIndicator();
                                        close();
                                        view.displayNotification( "Error: Deploy failed, check Problems panel " );
                                        return true;
                                    }
                                }
                              ).deploy( new KModuleDeploymentUnitSummary( "", group, artifact, version, kbaseName, kieSessionName, strategy ) );
    }

    @OnOpen
    public void onOpen() {
        view.getGroupText().setFocus( true );

    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }
}
