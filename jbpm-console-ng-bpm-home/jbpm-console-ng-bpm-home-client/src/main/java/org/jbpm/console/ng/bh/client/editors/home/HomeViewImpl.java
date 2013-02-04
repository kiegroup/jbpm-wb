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

import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Window;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

import org.jbpm.console.ng.bh.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "HomeViewImpl.html")
public class HomeViewImpl extends Composite
        implements
        HomePresenter.HomeView {

  private HomePresenter presenter;
  @Inject
  private PlaceManager placeManager;
  @Inject
  public Identity identity;
  @Inject
  @DataField
  public Label userRolesLabel;
  @DataField
  public Image avatar;
  @Inject
  @DataField
  public IconAnchor discoverLabel;
  @Inject
  @DataField
  public IconAnchor designLabel;
  @Inject
  @DataField
  public IconAnchor deployLabel;
  @Inject
  @DataField
  public IconAnchor workLabel;
  @Inject
  @DataField
  public IconAnchor monitorLabel;
  @Inject
  @DataField
  public IconAnchor improveLabel;
  @Inject
  @DataField
  public IconAnchor modelProcessAnchor;
  
  @Inject
  @DataField
  public IconAnchor workTaskListAnchor;
  
  @Inject
  @DataField
  public IconAnchor workProcessRuntimeAnchor;
  
  @Inject
  @DataField
  public IconAnchor monitorBAMAnchor;
  
  @Inject
  private Event<NotificationEvent> notification;
  private Constants constants = GWT.create(Constants.class);

  public HomeViewImpl() {

    avatar = new Image();

  }

  @Override
  public void init(final HomePresenter presenter) {
    this.presenter = presenter;
    String url = GWT.getHostPageBaseURL();
    avatar.setUrl(url + "images/avatars/" + identity.getName() + ".png");
    avatar.setSize("64px", "64px");
    List<Role> roles = identity.getRoles();
    List<String> stringRoles = new ArrayList<String>(roles.size());
    for (Role r : roles) {
      if (!r.getName().equals("IS_REMEMBER_ME")) {
        stringRoles.add(r.getName());
      }
    }
    userRolesLabel.setText(stringRoles.toString());
    discoverLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.discover();
      }
    });

    designLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.design();
      }
    });

    deployLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.deploy();
      }
    });

    workLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.work();
      }
    });
    monitorLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.monitor();
      }
    });
    improveLabel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.improve();
      }
    });

    modelProcessAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("New Process Definition");
        placeManager.goTo(placeRequestImpl);
      }
    });

    workTaskListAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Tasks List");
        placeManager.goTo(placeRequestImpl);
      }
    });
    
    workProcessRuntimeAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Runtime");
        placeManager.goTo(placeRequestImpl);
      }
    });
    
    monitorBAMAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.open("http://localhost:8080/bam-app/", "_blank", "");
      }
    });

  }

  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));
  }
}
