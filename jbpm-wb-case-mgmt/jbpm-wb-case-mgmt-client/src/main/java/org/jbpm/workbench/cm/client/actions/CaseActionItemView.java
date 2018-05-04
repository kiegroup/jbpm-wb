/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.cm.client.actions;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.DateConverter;
import org.jbpm.workbench.cm.model.CaseActionSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.*;

@Dependent
@Templated
public class CaseActionItemView extends AbstractView<CaseActionsPresenter> implements TakesValue<CaseActionSummary>,
                                                                                      IsElement {

    @Inject
    protected TranslationService translationService;

    @Inject
    @DataField("action-name")
    @Bound
    Span name;

    @Inject
    @DataField("action-info")
    private Span actionInfo;

    @Inject
    @DataField("action-createdOn")
    @Bound(converter = DateConverter.class)
    private Span createdOn;

    @Inject
    @DataField("list-group-item")
    private Div listGroupItem;

    @Inject
    @DataField("actions-dropdown")
    private Div actions;

    @Inject
    @DataField("actions-items")
    private UnorderedList actionsItems;

    @Inject
    @AutoBound
    private DataBinder<CaseActionSummary> caseActionSummary;

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public CaseActionSummary getValue() {
        return caseActionSummary.getModel();
    }

    @Override
    public void setValue(final CaseActionSummary model) {
        this.caseActionSummary.setModel(model);

        removeCSSClass(actions,
                       "dropup");
    }

    public void addAction(final CaseActionsPresenter.CaseActionAction action) {
        removeCSSClass(actions,
                       "hidden");

        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        a.setOnclick(e -> action.execute());

        final HTMLElement li = getDocument().createElement("li");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

    public void addCreationDate() {
        removeCSSClass(createdOn,
                       "hidden");
    }

    public void addActionInfo(final String text) {
        actionInfo.setTextContent(text);
    }

    public void setLastElementStyle() {
        addCSSClass(actions,
                    "dropup");
    }
}
