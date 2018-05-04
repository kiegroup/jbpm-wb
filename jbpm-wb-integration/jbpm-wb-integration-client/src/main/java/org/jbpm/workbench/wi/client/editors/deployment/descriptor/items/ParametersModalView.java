/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.widget.ListItemView;
import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;

@Templated
public class ParametersModalView implements Elemental2Modal.View<ParametersModal> {

    @Inject
    @DataField("header")
    private HTMLDivElement header;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("footer")
    private HTMLDivElement footer;

    @Inject
    @Named("tbody")
    @DataField("parameters-table")
    private HTMLTableSectionElement parametersTable;

    @Inject
    @DataField("add-parameter-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLButtonElement addParameterButton;

    @Inject
    @DataField("done-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLButtonElement doneButton;

    private ParametersModal presenter;

    @Override
    public void init(final ParametersModal presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-parameter-button")
    public void onAddParameterButtonClicked(final ClickEvent ignore) {
        presenter.add();
    }

    @EventHandler("done-button")
    public void onDoneButtonClicked(final ClickEvent ignore) {
        presenter.hide();
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }

    public HTMLTableSectionElement getParametersTable() {
        return parametersTable;
    }

    @Templated("ParametersModalView.html#parameter")
    public static class Parameter implements ListItemView<ParametersModal.ParameterItemPresenter>,
                                             IsElement {

        @Inject
        @DataField("type")
        private HTMLInputElement name;

        @Inject
        @DataField("value")
        private HTMLInputElement value;

        @Inject
        @DataField("remove-button")
        @SuppressWarnings("PMD.UnusedPrivateField")
        private HTMLAnchorElement removeButton;

        private ParametersModal.ParameterItemPresenter presenter;

        @Override
        public void init(final ParametersModal.ParameterItemPresenter presenter) {
            this.presenter = presenter;
        }

        @EventHandler("remove-button")
        public void onRemoveButtonClicked(final ClickEvent ignore) {
            presenter.remove();
        }

        @EventHandler("type")
        public void onNameChanged(final ChangeEvent ignore) {
            presenter.setName(name.value);
        }

        @EventHandler("value")
        public void onValueChanged(final ChangeEvent ignore) {
            presenter.setValue(value.value);
        }

        public void setType(final String name) {
            this.name.value = name;
        }

        public void setValue(final String value) {
            this.value.value = value;
        }
    }
}
