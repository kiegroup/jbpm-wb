/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.Arrays;
import java.util.Collection;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.Command;
import org.uberfire.client.workbench.widgets.menu.CommandMenuItem;
import org.uberfire.client.workbench.widgets.menu.SubMenuItem;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 *
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;
    @Inject
    private WorkbenchMenuBarPresenter menubar;
    private String[] menuItems = new String[]{"Personal Task Statistics", "Show Task Content", "Add Task Content", "Form Display", "Form Builder", "Quick New Task", "Quick New Sub Task", "Personal Tasks", "Group Tasks", "Task Details"};
    @Inject
    private Caller<FileExplorerRootService> rootService;

    @AfterInitialization
    public void startApp() {
        loadStyles();
        setupMenu();
        setupFileSystems();
        hideLoadingPopup();
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        //ShowcaseResources.INSTANCE.showcaseCss().ensureInjected();
        //RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    private void setupMenu() {
        //Places sub-menu
        final WorkbenchMenuBar placesMenuBar = new WorkbenchMenuBar();
        final SubMenuItem placesMenu = new SubMenuItem("Places",
                placesMenuBar);

        //Add places
        Arrays.sort(menuItems);
        for (final String menuItem : menuItems) {
            final CommandMenuItem item = new CommandMenuItem(menuItem,
                    new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo(new PlaceRequest(menuItem));
                        }
                    });
            placesMenuBar.addItem(item);
        }
        menubar.addMenuItem(placesMenu);
    }

    //TODO {manstis} Speak to porcelli about bootstrapping FileSystems
    private void setupFileSystems() {
        rootService.call(new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback(Collection<Root> response) {
                //Do nothing. FileSystems have been initialised.
            }
        }).listRoots();

    }
    //Fade out the "Loading application" pop-up

    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {
            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }
}