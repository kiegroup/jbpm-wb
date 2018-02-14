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

package org.jbpm.workbench.common.client.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.mvp.ParameterizedCommand;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class ServerTemplateSelectorViewImpl implements ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField
    private Div container;

    @Inject
    @DataField("selected-serverTemplate-text")
    Span selectedServerTemplateText;

    @Inject
    @DataField("server-templates-list")
    UnorderedList serverTemplatesList;

    private ParameterizedCommand<String> changeCommand;

    @Override
    public void init(final ServerTemplateSelectorMenuBuilder presenter) {
    }

    @Override
    public void selectServerTemplate(String serverTemplateId) {
        NodeList childList = serverTemplatesList.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            if (childList.item(i).getLastChild().getTextContent().equals(serverTemplateId)) {
                selectServerTemplate((HTMLElement) childList.item(i),
                                     serverTemplateId,
                                     false);
                break;
            }
        }
    }

    @Override
    public void updateSelectedValue(String serverTemplateId) {
        NodeList childList = serverTemplatesList.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            if (childList.item(i).getLastChild().getTextContent().equals(serverTemplateId)) {
                selectServerTemplate((HTMLElement) childList.item(i),
                                     serverTemplateId,
                                     true);
                break;
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            removeCSSClass(container,
                           "hidden");
        } else {
            addCSSClass(container,
                        "hidden");
        }
    }

    @Override
    public void addServerTemplate(String serverTemplateId) {
        final HTMLElement li = getDocument().createElement("li");
        final HTMLElement a = getDocument().createElement("a");
        final HTMLElement textSpan = getDocument().createElement("span");
        textSpan.setTextContent(serverTemplateId);
        a.setOnclick(e -> selectServerTemplate(li,
                                               serverTemplateId,
                                               false));
        a.appendChild(textSpan);
        li.appendChild(a);
        serverTemplatesList.appendChild(li);
    }

    protected void selectServerTemplate(final HTMLElement liOption,
                                        String serverTemplateId,
                                        boolean updating) {
        final boolean serverChanged = serverTemplateId.equals(selectedServerTemplateText.getTextContent()) == false;
        unSelectAllServerTemplateNavLinks();
        addCSSClass(liOption,
                    "active");
        if (serverChanged) {
            selectedServerTemplateText.setTextContent(serverTemplateId);
            if (changeCommand != null && !updating) {
                changeCommand.execute(serverTemplateId);
            }
        }
    }

    @Override
    public void clearSelectedServerTemplate() {
        selectedServerTemplateText.setTextContent(constants.ServerTemplates());
        if (changeCommand != null) {
            changeCommand.execute(null);
        }
    }

    @Override
    public String getSelectedServerTemplate() {
        final String serverTemplate = selectedServerTemplateText.getTextContent();
        return serverTemplate.equals(constants.ServerTemplates()) ? null : serverTemplate;
    }

    @Override
    public void removeAllServerTemplates() {
        removeAllChildren(serverTemplatesList);
    }

    @Override
    public void setServerTemplateChangeHandler(ParameterizedCommand<String> command) {
        this.changeCommand = command;
    }

    private void unSelectAllServerTemplateNavLinks() {
        NodeList childList = serverTemplatesList.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            removeCSSClass((HTMLElement) childList.item(i),
                           "active");
        }
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}