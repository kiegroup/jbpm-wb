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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals;

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
public class DeploymentsGlobalsView implements SectionView<DeploymentsGlobalsPresenter> {

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @Named("tbody")
    @DataField("globals")
    private HTMLTableSectionElement globalsTable;

    @Inject
    @DataField("add-global-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLButtonElement addGlobalButton;

    private DeploymentsGlobalsPresenter presenter;

    @Override
    public void init(final DeploymentsGlobalsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-global-button")
    public void onAddGlobalButtonClicked(final ClickEvent ignore) {
        presenter.addNewGlobal();
    }

    public Element getGlobalsTable() {
        return globalsTable;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
