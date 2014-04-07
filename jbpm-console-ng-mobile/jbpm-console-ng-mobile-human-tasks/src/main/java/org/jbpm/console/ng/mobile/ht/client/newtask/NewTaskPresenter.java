/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.mobile.ht.client.newtask;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateParser;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateRenderer;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskPresenter;


/**
 *
 * @author livthomas
 * @author salaboy
 */
public class NewTaskPresenter extends AbstractTaskPresenter {

    public interface NewTaskView extends View {

        HasText getTaskNameTextBox();

        HasValue<Boolean> getAssignToMeCheckBox();

        HasText getDueOnDateBox();

        MListBox getPriorityListBox();

        HasText getUserTextBox();

        HasTapHandlers getAddTaskButton();

    }

    @Inject
    private NewTaskView view;

    public NewTaskView getView() {
        return view;
    }

    @AfterInitialization
    public void init() {
        view.getAssignToMeCheckBox().setValue(false);
        view.getDueOnDateBox().setText(new DateRenderer().render(new Date()));
        view.getUserTextBox().setText(identity.getName());

        view.getAddTaskButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                try {
                    List<String> users = new ArrayList<String>();
                    users.add(view.getUserTextBox().getText());
                    List<String> groups = new ArrayList<String>();
                    String taskName = view.getTaskNameTextBox().getText();
                    int priority = view.getPriorityListBox().getSelectedIndex();
                    boolean assignToMe = view.getAssignToMeCheckBox().getValue();
                    long date = new DateParser().parse(view.getDueOnDateBox().getText()).getTime();
                    long time = 0;

                    addTask(users, groups, taskName, priority, assignToMe, date, time);

                    view.getTaskNameTextBox().setText("");
                    view.getPriorityListBox().setSelectedIndex(0);
                    view.getAssignToMeCheckBox().setValue(false);
                    view.getDueOnDateBox().setText(new DateRenderer().render(new Date()));

                    AnimationHelper animationHelper = new AnimationHelper();
                    RootPanel.get().clear();
                    RootPanel.get().add(animationHelper);
                    animationHelper.goTo(clientFactory.getTaskListPresenter().getView(), Animation.SLIDE_REVERSE);
                } catch (ParseException ex) {
                    view.displayNotification("Wrong date format", "Enter the date in the correct format!");
                }
            }
        });

        view.getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                view.getTaskNameTextBox().setText("");
                AnimationHelper animationHelper = new AnimationHelper();
                RootPanel.get().clear();
                RootPanel.get().add(animationHelper);
                animationHelper.goTo(clientFactory.getTaskListPresenter().getView(), Animation.SLIDE_REVERSE);
            }
        });
    }

    private void addTask(final List<String> users, List<String> groups, final String taskName, int priority,
            boolean isAssignToMe, long dueDate, long dueDateTime) {
        Map<String, Object> templateVars = new HashMap<String, Object>();
        Date due = new Date(dueDate + dueDateTime);
        templateVars.put("due", due);
        templateVars.put("now", new Date());

        String str = "(with (new Task()) { priority = " + priority
                + ", taskData = (with( new TaskData()) { createdOn = now, expirationTime = due } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        str += " [";
        if (users != null && !users.isEmpty()) {

            for (String user : users) {
                str += "new User('" + user + "'), ";
            }

        }
        if (groups != null && !groups.isEmpty()) {

            for (String group : groups) {
                str += "new Group('" + group + "'), ";
            }

        }
        str += "], businessAdministrators = [ new Group('Administrators') ],}),";
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')]})";

        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                clientFactory.getTaskListPresenter().refresh();
            }
        }).addTask(str, null, templateVars);
    }

}
