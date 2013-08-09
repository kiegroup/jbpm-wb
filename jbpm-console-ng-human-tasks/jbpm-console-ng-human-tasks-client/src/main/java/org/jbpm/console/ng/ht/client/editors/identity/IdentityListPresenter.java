/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.editors.identity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.service.GroupServiceEntryPoint;
import org.jbpm.console.ng.ht.service.UserServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@WorkbenchScreen(identifier = "Users and Groups")
public class IdentityListPresenter {

    private static final String TITLE = "Users and Groups";
    
    @Inject
    private Identity identity;

    public interface IdentityListView extends UberView<IdentityListPresenter> {

        void displayNotification(String text);

        TextBox getUserText();

        DataGrid<IdentitySummary> getDataGrid();
        
        ListBox getIdentityTypesList();

    }

    @Inject
    private IdentityListView view;
    
    @Inject
    Caller<UserServiceEntryPoint> userService;
    
    @Inject
    Caller<GroupServiceEntryPoint> groupService;
    
    public enum IdentityType{
        USERS, GROUPS;
    }
    
    
    private ListDataProvider<IdentitySummary> dataProvider = new ListDataProvider<IdentitySummary>();

    @WorkbenchPartTitle
    public String getTitle() {
        return TITLE;
    }

    @WorkbenchPartView
    public UberView<IdentityListPresenter> getView() {
        return view;
    }

    public IdentityListPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void addDataDisplay(HasData<IdentitySummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<IdentitySummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnOpen
    public void onOpen() {
        refreshIdentityList();
    }

    public void refreshIdentityList() {
        if(view.getIdentityTypesList().getValue().equals(IdentityType.USERS.toString())){
            refreshUsers();
        }else{
            refreshGroups();
        }
    }
    
    public void refreshUsers(){
        userService.call(new RemoteCallback<List<IdentitySummary>>() {
            @Override
            public void callback(List<IdentitySummary> entities) {
                refreshList(entities);
            }
          }).getAll();
    }
    
    public void refreshGroups(){
        groupService.call(new RemoteCallback<List<IdentitySummary>>() {
            @Override
            public void callback(List<IdentitySummary> entities) {
                refreshList(entities);
            }
          }).getAll();
    }
    
    public void getUserById(String entityId) {
        userService.call(new RemoteCallback<IdentitySummary>() {
            @Override
            public void callback(IdentitySummary identity) {
                refreshIdentity(identity);

            }
        }).getById(entityId);
    }
    
    public void getGroupById(String entityId){
        groupService.call(new RemoteCallback<IdentitySummary>() {
            @Override
            public void callback(IdentitySummary identity) {
                refreshIdentity(identity);

            }
        }).getById(entityId);
    }
    
    private void refreshList(List<IdentitySummary> entities){
        dataProvider.getList().clear();
        if (entities != null) {
            dataProvider.getList().addAll(entities);
            dataProvider.refresh();
        }
    }
    
    private void refreshIdentity(IdentitySummary identity){
        dataProvider.getList().clear();
        if (identity != null) {
            List<IdentitySummary> values = new ArrayList<IdentitySummary>();
            values.add(identity);

            dataProvider.getList().addAll(values);
            dataProvider.refresh();
        }
    }

}