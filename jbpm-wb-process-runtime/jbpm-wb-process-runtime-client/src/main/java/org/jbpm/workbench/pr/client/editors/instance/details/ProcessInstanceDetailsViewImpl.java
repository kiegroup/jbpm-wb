/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ProcessInstanceDetailsViewImpl extends Composite implements ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Constants constants = Constants.INSTANCE;

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

    private TabListItem instanceDetailsTab;

    private TabPane instanceDetailsPane;

    private TabListItem processVariablesTab;

    private TabPane processVariablesPane;

    private TabListItem documentTab;

    private TabPane documentPane;

    private TabListItem logsTab;

    private TabPane logsPane;

    private TabListItem diagramTab;

    private TabPane diagramPane;

    private ProcessInstanceDetailsPresenter presenter;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(final ProcessInstanceDetailsPresenter presenter) {
        initWidget(uiBinder.createAndBindUi(this));
        this.presenter = presenter;
        initTabs();
    }

    public void initTabs() {
        tabContent.setPaddingBottom(50);
        {
            instanceDetailsPane = GWT.create(TabPane.class);
            instanceDetailsPane.add(presenter.getProcessInstanceView());
            instanceDetailsTab = GWT.create(TabListItem.class);
            instanceDetailsTab.setText(constants.Process_Instance_Details());
            instanceDetailsTab.setDataTargetWidget(instanceDetailsPane);
            instanceDetailsTab.addStyleName("uf-dropdown-tab-list-item");
            tabContent.add(instanceDetailsPane);
            navTabs.add(instanceDetailsTab);
        }
        {
            processVariablesPane = GWT.create(TabPane.class);
            processVariablesPane.add(presenter.getProcessVariablesView());
            processVariablesTab = GWT.create(TabListItem.class);
            processVariablesTab.setText(constants.Process_Variables());
            processVariablesTab.setDataTargetWidget(processVariablesPane);
            processVariablesTab.addStyleName("uf-dropdown-tab-list-item");
            tabContent.add(processVariablesPane);
            navTabs.add(processVariablesTab);
            processVariablesTab.addShowHandler(new TabShowHandler() {
                @Override
                public void onShow(final TabShowEvent event) {
                    presenter.variableListRefreshGrid();
                }
            });
        }
        {
            documentPane = GWT.create(TabPane.class);
            documentPane.add(presenter.getDocumentView());
            documentTab = GWT.create(TabListItem.class);
            documentTab.setText(constants.Documents());
            documentTab.setDataTargetWidget(documentPane);
            documentTab.addStyleName("uf-dropdown-tab-list-item");
            tabContent.add(documentPane);
            navTabs.add(documentTab);
            documentTab.addShowHandler(new TabShowHandler() {
                @Override
                public void onShow(final TabShowEvent event) {
                    presenter.documentListRefreshGrid();
                }
            });
        }
        {
            logsPane = GWT.create(TabPane.class);
            logsPane.add(presenter.getLogsView());
            logsTab = GWT.create(TabListItem.class);
            logsTab.setText(constants.Logs());
            logsTab.setDataTargetWidget(logsPane);
            logsTab.addStyleName("uf-dropdown-tab-list-item");
            tabContent.add(logsPane);
            navTabs.add(logsTab);
        }
        {
            diagramPane = GWT.create(TabPane.class);
            diagramPane.add(presenter.getProcessDiagramView());
            diagramTab = GWT.create(TabListItem.class);
            diagramTab.addShownHandler(e -> presenter.onProcessDiagramTabShow());
            diagramTab.setText(constants.Diagram());
            diagramTab.setDataTargetWidget(diagramPane);
            diagramTab.addStyleName("uf-dropdown-tab-list-item");
            tabContent.add(diagramPane);
            navTabs.add(diagramTab);
        }
    }

    @Override
    public void resetTabs(boolean onlyLogTab) {
        if (onlyLogTab) {
            instanceDetailsTab.showTab();
        } else {
            ((TabListItem) navTabs.getWidget(0)).showTab();
        }
    }

    @Override
    public void displayAllTabs() {
        for (Widget active : navTabs) {
            active.setVisible(true);
        }
        for (Widget active : tabContent) {
            active.setVisible(true);
        }
    }

    @Override
    public void displayOnlyLogTab() {
        for (Widget active : navTabs) {
            active.setVisible(false);
        }
        for (Widget active : tabContent) {
            active.setVisible(false);
        }
        instanceDetailsPane.setVisible(true);
        instanceDetailsTab.setVisible(true);

        logsPane.setVisible(true);
        logsTab.setVisible(true);

        diagramPane.setVisible(true);
        diagramTab.setVisible(true);
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    interface Binder extends UiBinder<Widget, ProcessInstanceDetailsViewImpl> {

    }
}
