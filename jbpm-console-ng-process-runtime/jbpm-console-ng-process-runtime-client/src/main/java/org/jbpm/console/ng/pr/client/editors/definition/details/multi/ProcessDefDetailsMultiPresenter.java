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
package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "Process Details Multi", preferredWidth = 500)
public class ProcessDefDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

    public interface ProcessDefDetailsMultiView
            extends TabbedDetailsView<ProcessDefDetailsMultiPresenter> {

        IsWidget getCloseButton();

        IsWidget getRefreshButton();

        IsWidget getOptionsButton();

        IsWidget getNewInstanceButton();
    }

    @Inject
    public ProcessDefDetailsMultiView view;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private Constants constants = GWT.create( Constants.class );

    public ProcessDefDetailsMultiPresenter() {

    }

    @WorkbenchPartView
    public UberView<ProcessDefDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return Position.EAST;
    }

    public void selectDefaultTab() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
    }

    public void onProcessSelectionEvent( @Observes ProcessDefSelectionEvent event ) {
        selectedItemId = event.getDeploymentId();
        selectedItemName = event.getProcessId();
        view.getHeaderPanel().clear();

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place, String.valueOf( selectedItemId ) + " - " + selectedItemName ) );

        view.getTabPanel().selectTab( 0 );
    }

    public void createNewProcessInstance() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Generic Popup" );
        placeRequestImpl.addParameter( "placeToGo", "Generic Form Display" );
        placeRequestImpl.addParameter( "key", selectedItemName );
        placeRequestImpl.addParameter( "name", selectedItemName );
        placeRequestImpl.addParameter( "type", "screen" );
        placeRequestImpl.addParameter( "params", "processId," + selectedItemName + ",domainId," + selectedItemId + ",processName," + selectedItemName );
        placeManager.goTo( placeRequestImpl );
    }

    public void goToProcessDefModelPopup() {
        if ( place != null && !selectedItemId.equals( "" ) ) {

            DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest( "Process Diagram Popup" );
            //Set Parameters here: 

            defaultPlaceRequest.addParameter( "processId", selectedItemName );
            defaultPlaceRequest.addParameter( "deploymentId", selectedItemId );
            placeManager.goTo( defaultPlaceRequest );

        }
    }

    public void viewProcessInstances() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instances" );
        placeRequestImpl.addParameter( "processName", selectedItemName );
        placeManager.goTo( placeRequestImpl );
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getNewInstanceButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getOptionsButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getRefreshButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getCloseButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }

    public void refresh() {
        processDefSelectionEvent.fire( new ProcessDefSelectionEvent( selectedItemName, selectedItemId ) );
    }

}
