/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.ht.client.editors.quicknewgroup;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.Group;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Quick New Group")
public class QuickNewGroupPresenter {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    QuickNewGroupView view;

    @Inject
    Identity identity;

    @Inject
    Caller<GroupServiceEntryPoint> groupService;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    public interface QuickNewGroupView extends UberView<QuickNewGroupPresenter> {

        void displayNotification(String text);

        TextBox getDescriptionText();

        ListBox getParentGroupList();

    }

    private PlaceRequest place;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Add_Group();
    }

    @WorkbenchPartView
    public UberView<QuickNewGroupPresenter> getView() {
        return view;
    }

    public QuickNewGroupPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void onOpen() {
        view.getDescriptionText().setFocus(true);
    }

    public void addGroup() {
        Group group = new Group(view.getDescriptionText().getText());
        if (view.getParentGroupList().getValue() != null && !view.getParentGroupList().getValue().isEmpty()) {
            group.setParent(new Group(view.getParentGroupList().getValue()));
        }
        groupService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Group Created (id = " + view.getDescriptionText().getText() + ")");
                close();

            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).save(group);
    }

    public void loadGroups() {
        groupService.call(new RemoteCallback<List<Group>>() {
            @Override
            public void callback(List<Group> groups) {
                if (groups != null && !groups.isEmpty()) {
                    fillListGroups(groups);
                }

            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getAll();
    }

    private void fillListGroups(List<Group> groups) {
        for (Group group : groups) {
            view.getParentGroupList().addItem(group.getName(), group.getId());
        }
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
