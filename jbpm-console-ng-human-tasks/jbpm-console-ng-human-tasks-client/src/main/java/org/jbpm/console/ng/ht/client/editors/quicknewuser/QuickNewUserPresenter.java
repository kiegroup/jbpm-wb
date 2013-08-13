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

package org.jbpm.console.ng.ht.client.editors.quicknewuser;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;
import org.jbpm.console.ng.ht.service.UserServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Quick New User")
public class QuickNewUserPresenter {

    private static final String USER = "User";

    private Constants constants = GWT.create(Constants.class);

    @Inject
    QuickNewUserView view;

    @Inject
    Identity identity;

    @Inject
    Caller<UserServiceEntryPoint> userService;

    @Inject
    Caller<GroupServiceEntryPoint> groupService;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    public interface QuickNewUserView extends UberView<QuickNewUserPresenter> {

        void displayNotification(String text);

        TextBox getDescriptionText();

        ListBox getGroupsList();

    }

    private PlaceRequest place;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Add_User();
    }

    @WorkbenchPartView
    public UberView<QuickNewUserPresenter> getView() {
        return view;
    }

    public QuickNewUserPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void onOpen() {
        view.getDescriptionText().setFocus(true);
    }

    public void addUser() {
        IdentitySummary user = new IdentitySummary(view.getDescriptionText().getText(), USER);
        if (view.getGroupsList().getValue() != null && !view.getGroupsList().getValue().isEmpty()) {
            user.setIdParent(view.getGroupsList().getValue());
        }
        userService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("User Created (id = " + view.getDescriptionText().getText() + ")");
                close();

            }
        }).save(user);
    }

    public void loadGroup() {
        groupService.call(new RemoteCallback<List<IdentitySummary>>() {
            @Override
            public void callback(List<IdentitySummary> groups) {
                if (groups != null && !groups.isEmpty()) {
                    fillListGroups(groups);
                }

            }
        }).getAll();
    }

    private void fillListGroups(List<IdentitySummary> groups) {
        for (IdentitySummary group : groups) {
            view.getGroupsList().addItem(group.getId(), group.getId());
        }
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

}
