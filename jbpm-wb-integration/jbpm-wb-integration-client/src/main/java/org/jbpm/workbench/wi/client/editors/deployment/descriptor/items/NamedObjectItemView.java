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

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class NamedObjectItemView implements NamedObjectItemPresenter.View,
                                            IsElement {

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @DataField("value")
    private HTMLInputElement value;

    @Inject
    @DataField("resolvers")
    private HTMLDivElement resolversContainer;

    @Inject
    @DataField("parameters-link")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLAnchorElement parametersLink;

    @Inject
    @Named("span")
    @DataField("parameters-count")
    private HTMLElement parametersCount;

    @Inject
    @DataField("remove-button")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HTMLAnchorElement removeButton;

    private NamedObjectItemPresenter presenter;

    @Override
    public void init(final NamedObjectItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    @EventHandler("parameters-link")
    public void onParametersLinkClicked(final ClickEvent ignore) {
        presenter.showParametersModal();
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setValue(final String value) {
        this.value.value = value;
    }

    @Override
    public void setParametersCount(final int parametersCount) {
        this.parametersCount.textContent = Integer.toString(parametersCount);
    }

    @Override
    public Element getResolversContainer() {
        return resolversContainer;
    }

    @EventHandler("value")
    public void onValueChange(final @ForEvent("change") Event event) {
        presenter.onValueChange(value.value);
    }

    @EventHandler("name")
    public void onNameChange(final @ForEvent("change") Event event) {
        presenter.onNameChange(name.value);
    }
}
