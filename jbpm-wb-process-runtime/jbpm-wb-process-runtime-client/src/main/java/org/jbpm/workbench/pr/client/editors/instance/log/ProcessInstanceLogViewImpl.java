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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils.LogOrder;
import org.jbpm.workbench.pr.client.util.LogUtils.LogType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

@Dependent
@Templated(value = "ProcessInstanceLogViewImpl.html")
public class ProcessInstanceLogViewImpl extends Composite
        implements ProcessInstanceLogPresenter.ProcessInstanceLogView {

    // Can't inject this because many uberfire widgets extend it.
    @DataField
    public Div logTextArea = new Div();

    @Inject
    @DataField
    public Button showBusinessLogButton;

    @Inject
    @DataField
    public Button showTechnicalLogButton;

    @Inject
    @DataField
    public Button showAscLogButton;

    @Inject
    @DataField
    public Button showDescLogButton;

    private ProcessInstanceLogPresenter presenter;

    private LogOrder logOrder = LogOrder.ASC;

    private LogType logType = LogType.BUSINESS;

    private Constants constants = Constants.INSTANCE;

    @Override
    public void init(final ProcessInstanceLogPresenter presenter) {
        this.presenter = presenter;

        this.setFilters(showBusinessLogButton,
                        constants.Business_Log(),
                        LogType.BUSINESS);
        this.setFilters(showTechnicalLogButton,
                        constants.Technical_Log(),
                        LogType.TECHNICAL);
        this.setOrder(showAscLogButton,
                      constants.Asc_Log_Order(),
                      LogOrder.ASC);
        this.setOrder(showDescLogButton,
                      constants.Desc_Log_Order(),
                      LogOrder.DESC);
    }

    private void setFilters(Button button,
                            String description,
                            final LogType logType) {
        button.setText(description);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveLogTypeButton(logType);
                getInstanceData();
            }
        });
    }

    private void setOrder(Button button,
                          String description,
                          final LogOrder logOrder) {
        button.setText(description);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveLogOrderButton(logOrder);
                getInstanceData();
            }
        });
    }

    public void getInstanceData() {
        presenter.refreshProcessInstanceData(logOrder,
                                             logType);
    }

    @Override
    public void setLogs(final List<String> logs) {
        logTextArea.clear();
        final UnorderedList list = new UnorderedList() {{
            addStyleName("list-unstyled");
        }};
        for (String log : logs) {
            list.add(new ListItem(log));
        }
        logTextArea.add(list);
    }

    public void setActiveLogTypeButton(LogType logType) {
        this.logType = logType;
        switch (logType) {
            case TECHNICAL:
                showTechnicalLogButton.setActive(true);
                showBusinessLogButton.setActive(false);
                break;
            case BUSINESS:
                showBusinessLogButton.setActive(true);
                showTechnicalLogButton.setActive(false);
                break;
        }
    }

    public void setActiveLogOrderButton(LogOrder logOrder) {
        this.logOrder = logOrder;
        switch (logOrder) {
            case ASC:
                showAscLogButton.setActive(true);
                showDescLogButton.setActive(false);
                break;
            case DESC:
                showDescLogButton.setActive(true);
                showAscLogButton.setActive(false);
                break;
        }
    }
}