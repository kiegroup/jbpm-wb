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

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.Group;
import org.jbpm.console.ng.ht.model.TypeRole;
import org.jbpm.console.ng.ht.model.User;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;
import org.jbpm.console.ng.ht.service.TypeRoleServiceEntryPoint;
import org.jbpm.console.ng.ht.service.UserServiceEntryPoint;
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
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Quick New User")
public class QuickNewUserPresenter {

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
    Caller<TypeRoleServiceEntryPoint> typeRoleService;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    public interface QuickNewUserView extends UberView<QuickNewUserPresenter> {

        void displayNotification(String text);

        TextBox getDescriptionText();

        ListBox getGroupsList();

        ListBox getTypeRoleList();

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
        User user = new User(view.getDescriptionText().getText());
        if (view.getGroupsList().getValue() != null && !view.getGroupsList().getValue().isEmpty()) {
            user.setGroups(getSelectedGroups());
        }
        if (view.getTypeRoleList().getValue() != null && !view.getTypeRoleList().getValue().isEmpty()) {
            user.setTypesRole(getSelectedTypesRole());
        }
        userService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("User Created (id = " + view.getDescriptionText().getText() + ")");
                close();

            }
        }, new ErrorCallback<Message>() {
             @Override
             public boolean error( Message message, Throwable throwable ) {
                 ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                 return true;
             }
         }).save(user);
    }

    public List<Group> getSelectedGroups() {
        List<Group> selectedGroups = Lists.newArrayListWithExpectedSize(view.getGroupsList().getValue().length());
        for (int i = 0; i < view.getGroupsList().getItemCount(); i++) {
            if (view.getGroupsList().isItemSelected(i)) {
                selectedGroups.add(new Group(view.getGroupsList().getValue(i)));
            }
        }
        return selectedGroups;
    }

    public List<TypeRole> getSelectedTypesRole() {
        List<TypeRole> selectedTypesRole = Lists.newArrayListWithExpectedSize(view.getTypeRoleList().getValue().length());
        for (int i = 0; i < view.getTypeRoleList().getItemCount(); i++) {
            if (view.getTypeRoleList().isItemSelected(i)) {
                selectedTypesRole.add(new TypeRole(view.getTypeRoleList().getValue(i)));
            }
        }
        return selectedTypesRole;
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

    public void loadTypesRole() {
        typeRoleService.call(new RemoteCallback<List<TypeRole>>() {
            @Override
            public void callback(List<TypeRole> types) {
                if (types != null && !types.isEmpty()) {
                    fillListTypeRole(types);
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
            view.getGroupsList().addItem(group.getName(), group.getId());
        }
    }

    private void fillListTypeRole(List<TypeRole> types) {
        for (TypeRole type : types) {
            view.getTypeRoleList().addItem(type.getName(), type.getId());
        }
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

}
