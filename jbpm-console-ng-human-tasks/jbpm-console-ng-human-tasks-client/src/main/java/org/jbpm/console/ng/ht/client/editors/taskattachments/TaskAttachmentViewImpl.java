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
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
import org.jbpm.console.ng.ht.client.util.ResizableHeader;
import org.jbpm.console.ng.ht.model.AttachmentSummary;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAttachmentViewImpl.html")
public class TaskAttachmentViewImpl extends Composite implements TaskAttachmentPresenter.TaskAttachmentView {

    private Constants constants = GWT.create(Constants.class);
    
    private HumanTasksImages images = GWT.create(HumanTasksImages.class);

    private TaskAttachmentPresenter presenter;

    @Inject
    @DataField
    public ControlLabel attachmentsAccordionLabel;

    @Inject
    @DataField
    public Label attachmentNameLabel;

    @Inject
    @DataField
    public TextBox attachmentNameText;

    @Inject
    @DataField
    public Label attachmentContentLabel;

    @Inject
    @DataField
    public TextArea attachmentContentTextArea;

    @Inject
    @DataField
    public Button addAttachmentButton;

    @Inject
    @DataField
    public DataGrid<AttachmentSummary> attachmentsListGrid;

    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    @DataField
    public FlowPanel listContainer;

    private ColumnSortEvent.ListHandler<AttachmentSummary> sortHandler;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public TextBox getAttachmentNameText() {
        return attachmentNameText;
    }

    @Override
    public TextArea getAttachmentContentTextArea() {
        return attachmentContentTextArea;
    }

    @Override
    public Button getAddAttachmentButton() {
        return addAttachmentButton;
    }

    @Override
    public DataGrid<AttachmentSummary> getDataGrid() {
        return attachmentsListGrid;
    }

    @Override
    public SimplePager getPager() {
        return pager;
    }

    @Override
    public void init(TaskAttachmentPresenter presenter) {
        this.presenter = presenter;
        
        attachmentNameLabel.setText(constants.Attachment_Name());
        attachmentContentLabel.setText(constants.Content());
        attachmentContentTextArea.setWidth("300px");
        addAttachmentButton.setText(constants.Add_Attachment());
        
        listContainer.add(attachmentsListGrid);
        listContainer.add(pager);
        attachmentsListGrid.setHeight("100px");

        attachmentsAccordionLabel.add(new HTMLPanel(constants.Add_Attachment()));

        attachmentsListGrid.setEmptyTableWidget(new HTMLPanel(constants.No_Attachments_For_This_Task()));
        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<AttachmentSummary>(presenter.getDataProvider().getList());
        attachmentsListGrid.addColumnSortHandler(sortHandler);
        initTableColumns();
        presenter.addDataDisplay(attachmentsListGrid);
        // Create a Pager to control the table.
        pager.setVisible(false);
        pager.setDisplay(attachmentsListGrid);
        pager.setPageSize(6);
    }

    @EventHandler("addAttachmentButton")
    public void addAttachmentButton(ClickEvent e) {
        String name = attachmentNameText.getText();
        String content = attachmentContentTextArea.getText();

        if (!name.isEmpty() && !content.isEmpty()) {
            presenter.addTaskAttachment(name, content);
        } else {
            displayNotification(constants.Fill_In_All_Fields());
        }
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    private void initTableColumns() {
        // attachment name
        Column<AttachmentSummary, String> attachmentNameColumn = new Column<AttachmentSummary, String>(new TextCell()) {
            @Override
            public String getValue(AttachmentSummary object) {
                return object.getName();
            }
        };
        attachmentNameColumn.setSortable(false);
        attachmentsListGrid.addColumn(attachmentNameColumn, constants.Name());

        List<HasCell<AttachmentSummary, ?>> cells = new LinkedList<HasCell<AttachmentSummary, ?>>();

        cells.add(new DeleteAttachmentActionHasCell("Delete", new ActionCell.Delegate<AttachmentSummary>() {
            @Override
            public void execute(AttachmentSummary attachment) {
                presenter.removeTaskAttachment(attachment.getId());
            }
        }));
        
        // attachedBy
        Column<AttachmentSummary, String> attachedByColumn = new Column<AttachmentSummary, String>(new TextCell()) {
            @Override
            public String getValue(AttachmentSummary a) {
                // for some reason the username comes in format [User:'<name>'], so parse just the <name>
                int first = a.getAttachedBy().indexOf('\'');
                int last = a.getAttachedBy().lastIndexOf('\'');
                return a.getAttachedBy().substring(first + 1, last);
            }
        };
        attachedByColumn.setSortable(false);
        attachmentsListGrid.addColumn(attachedByColumn, constants.Attached_By());
        attachmentsListGrid.setColumnWidth(attachedByColumn, "100px");

        // date
        Column<AttachmentSummary, String> attachedAtColumn = new Column<AttachmentSummary, String>(new TextCell()) {
            @Override
            public String getValue(AttachmentSummary a) {
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy");
                return format.format(a.getAttachedAt());
            }
        };
        attachedAtColumn.setSortable(true);
        attachedAtColumn.setDefaultSortAscending(true);
        attachmentsListGrid.addColumn(attachedAtColumn, constants.Date());
        sortHandler.setComparator(attachedAtColumn, new Comparator<AttachmentSummary>() {
            @Override
            public int compare(AttachmentSummary o1, AttachmentSummary o2) {
                return o1.getAttachedAt().compareTo(o2.getAttachedAt());
            }
        });

        CompositeCell<AttachmentSummary> cell = new CompositeCell<AttachmentSummary>(cells);
        Column<AttachmentSummary, AttachmentSummary> actionsColumn = new Column<AttachmentSummary, AttachmentSummary>(
                cell) {
                    @Override
                    public AttachmentSummary getValue(AttachmentSummary object) {
                        return object;
                    }
                };
        attachmentsListGrid.addColumn(actionsColumn, new ResizableHeader("", attachmentsListGrid, actionsColumn));
    }

    private class DeleteAttachmentActionHasCell implements HasCell<AttachmentSummary, AttachmentSummary> {

        private ActionCell<AttachmentSummary> cell;

        public DeleteAttachmentActionHasCell(String text, ActionCell.Delegate<AttachmentSummary> delegate) {
            cell = new ActionCell<AttachmentSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, AttachmentSummary value, SafeHtmlBuilder sb) {
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.abortGridIcon());
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Delete() + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<AttachmentSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<AttachmentSummary, AttachmentSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public AttachmentSummary getValue(AttachmentSummary object) {
            return object;
        }
    }

}
