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
package org.jbpm.console.ng.ht.client.editors.taskattachments;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.AttachmentSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Task Attachments")
public class TaskAttachmentPresenter {

    public interface TaskAttachmentView extends UberView<TaskAttachmentPresenter> {

        TextBox getAttachmentNameText();

        TextArea getAttachmentContentTextArea();

        Button getAddAttachmentButton();

        DataGrid<AttachmentSummary> getDataGrid();

        SimplePager getPager();
        
        void displayNotification(String text);

    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TaskAttachmentView view;

    @Inject
    private Identity identity;
    
    private long currentTaskId = 0;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    private final Constants constants = GWT.create(Constants.class);

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private final ListDataProvider<AttachmentSummary> dataProvider = new ListDataProvider<AttachmentSummary>();

    public ListDataProvider<AttachmentSummary> getDataProvider() {
        return dataProvider;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Attachments();
    }

    @WorkbenchPartView
    public UberView<TaskAttachmentPresenter> getView() {
        return view;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        this.currentTaskId = Long.parseLong(place.getParameter("taskId", "0").toString());
        refreshAttachments();
        view.getDataGrid().redraw();
    }

    public void refreshAttachments() {
        taskServices.call(new RemoteCallback<List<AttachmentSummary>>() {
            @Override
            public void callback(List<AttachmentSummary> attachments) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll(attachments);
                if (attachments.size() > 0) {
                    view.getDataGrid().setHeight("350px");
                    view.getPager().setVisible(true);
                }
                dataProvider.refresh();
                view.getDataGrid().redraw();
            }
        } ).getAllAttachmentsByTaskId(currentTaskId);
    }

    public void addTaskAttachment(String name, String content) {
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long response) {
                refreshAttachments();
                view.getAttachmentNameText().setText("");
                view.getAttachmentContentTextArea().setText("");
            }
        } ).addAttachment(currentTaskId, name, identity.getName(), content);
    }
    
    public void removeTaskAttachment(long attachmentId) {
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long response) {
                refreshAttachments();
                view.displayNotification("Attachment has been deleted"); // Constants.Attachment_Deleted()
            }
        } ).deleteAttachment(currentTaskId, attachmentId);
    }

    public void addDataDisplay(HasData<AttachmentSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event){
        if (currentTaskId == event.getTaskId()) {
            refreshAttachments();
            view.getDataGrid().redraw();
        }
    }

}
