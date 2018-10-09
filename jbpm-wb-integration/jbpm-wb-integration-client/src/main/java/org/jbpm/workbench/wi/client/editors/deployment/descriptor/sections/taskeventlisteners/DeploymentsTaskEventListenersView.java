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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.taskeventlisteners;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;

@Templated
public class DeploymentsTaskEventListenersView implements SectionView<DeploymentsTaskEventListenersPresenter> {

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @Named("tbody")
    @DataField("task-event-listeners")
    private HTMLTableSectionElement eventListenersTable;

    @Inject
    @DataField("add-task-event-listener-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLButtonElement addEventListenerButton;

    private DeploymentsTaskEventListenersPresenter presenter;

    @Override
    public void init(final DeploymentsTaskEventListenersPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-task-event-listener-button")
    public void onAddTaskEventListenerButtonClicked(final ClickEvent ignore) {
        presenter.addNewTaskEventListener();
    }

    public Element getTaskEventListenersTable() {
        return eventListenersTable;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
