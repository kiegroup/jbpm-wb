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

package org.jbpm.workbench.cm.client.util;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllElementChildren;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Templated(stylesheet = "InlineNotification.css")
@Dependent
public class InlineNotification implements IsElement {

    @Inject
    @DataField("alert")
    private Div alert;

    @Inject
    @DataField("message")
    private Span message;

    @Inject
    @DataField("icon")
    private Span icon;

    @Inject
    @DataField("dismiss")
    private Button dismiss;

    @Override
    public HTMLElement getElement() {
        return alert;
    }

    public void setMessage(final String message) {
        this.message.setTextContent(message);
    }

    public void setMessage(final List<String> messages) {
        removeAllElementChildren(this.message);
        final HTMLElement ul = getDocument().createElement("ul");
        addCSSClass(ul, "list-unstyled");
        for (String message : messages) {
            final HTMLElement li = getDocument().createElement("li");
            li.setTextContent(message);
            ul.appendChild(li);
        }
        this.message.appendChild(ul);
    }

    public void setDismissable() {
        addCSSClass(alert,
                    "alert-dismissable");
        removeCSSClass(dismiss,
                       "hidden");
    }

    public void setType(final InlineNotificationType type) {
        addCSSClass(alert,
                    type.getCssClass());
        addCSSClass(icon,
                    type.getIcon());
    }

    public enum InlineNotificationType {

        SUCCESS("alert-success",
                "pficon-ok"),
        INFO("alert-info",
             "pficon-info"),
        WARNING("alert-warning",
                "pficon-warning-triangle-o"),
        DANGER("alert-danger",
               "pficon-error-circle-o");

        private String cssClass;

        private String icon;

        InlineNotificationType(final String cssClass,
                               final String icon) {
            this.cssClass = cssClass;
            this.icon = icon;
        }

        public String getCssClass() {
            return cssClass;
        }

        public String getIcon() {
            return icon;
        }
    }
}
