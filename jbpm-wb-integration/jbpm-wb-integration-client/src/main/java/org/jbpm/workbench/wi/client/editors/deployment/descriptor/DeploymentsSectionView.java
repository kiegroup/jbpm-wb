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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

@Templated
public class DeploymentsSectionView implements DeploymentsSectionPresenter.View {

    @Inject
    private TranslationService translationService;

    @TranslationKey(defaultValue = "")
    public static final String DeploymentsXmlConcurrentUpdate = "DeploymentsXmlConcurrentUpdate";

    @Inject
    @DataField("menu-items-container")
    private HTMLUListElement menuItemsContainer;

    @Inject
    @DataField("content")
    private HTMLDivElement content;

    private DeploymentsSectionPresenter presenter;

    @Override
    public void init(final DeploymentsSectionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getConcurrentUpdateMessage() {
        return translationService.format(DeploymentsXmlConcurrentUpdate);
    }

    @Override
    public HTMLElement getMenuItemsContainer() {
        return menuItemsContainer;
    }

    @Override
    public HTMLElement getContentContainer() {
        return content;
    }

    @Override
    public String getTitle() {
        return "Deployments"; //FIXME: i18n
    }
}
