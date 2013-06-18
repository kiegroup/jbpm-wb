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

package org.jbpm.console.ng.ht.client.history;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.util.CalendarPicker;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Dependent
@Templated(value = "ActionHistoryListViewImpl.html")
public class ActionHistoryListViewImpl extends Composite implements ActionHistoryPresenter.ActionHistoryView{

    @Inject
    @DataField
    public NavLink dayViewTasksNavLink;

    @Inject
    @DataField
    public NavLink gridViewTasksNavLink;

    @Inject
    @DataField
    public NavLink monthViewTasksNavLink;

    @Inject
    @DataField
    public NavLink weekViewTasksNavLink;
    
    @Inject
    @DataField
    public TextBox searchBox;

    @Inject
    @DataField
    public NavLink createQuickTaskNavLink;

    @Inject
    @DataField
    public NavLink showAllTasksNavLink;

    @Inject
    @DataField
    public NavLink showPersonalTasksNavLink;

    @Inject
    @DataField
    public NavLink showGroupTasksNavLink;

    @Inject
    @DataField
    public NavLink showActiveTasksNavLink;

    
    @DataField
    public Heading taskCalendarViewLabel = new Heading(4);

    @Inject
    @DataField
    private CalendarPicker calendarPicker;

    @Inject
    @DataField
    public FlowPanel tasksViewContainer;
    
    @Inject
    @DataField
    public IconAnchor refreshIcon;
    
    private ActionHistoryPresenter presenter;

    @Override
    public void init(ActionHistoryPresenter presenter) {
        this.presenter = presenter;
        
    }

    @Override
    public void displayNotification(String text) {
        // TODO Auto-generated method stub
        
    }

}
