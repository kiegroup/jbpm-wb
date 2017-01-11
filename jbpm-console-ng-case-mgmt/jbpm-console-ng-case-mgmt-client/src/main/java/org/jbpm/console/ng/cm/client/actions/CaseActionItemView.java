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

package org.jbpm.console.ng.cm.client.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
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
import org.jbpm.console.ng.cm.client.events.CaseStartAdhocFragmentEvent;
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.client.util.CaseActionStatus;
import org.jbpm.console.ng.cm.model.CaseActionSummary;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;


@Dependent
@Templated
public class CaseActionItemView extends AbstractView<CaseActionsPresenter> implements TakesValue<CaseActionSummary>, IsElement {

    @Inject
    @DataField("action-bullet")
    Span bullet;

    @Inject
    @DataField("action-name")
    @Bound
    Span name;

    @Inject
    @DataField("action-status")
    @Bound
    Span status;

    @Inject
    @DataField("list-group-item")
    Div listGroupItem;

    @Inject
    @DataField("actions-dropdown")
    Div actions;

    @Inject
    @DataField("actions-items")
    UnorderedList actionsItems;

    @Inject
    @AutoBound
    private DataBinder<CaseActionSummary> caseActionSummary;

    @Inject
    protected TranslationService translationService;

    private Event<CaseStartAdhocFragmentEvent> startAdhocFragmentEvent;

    @Override
    public void init(final CaseActionsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Inject
    public void setCommentEditEvent(final Event<CaseStartAdhocFragmentEvent> startAdhocFragmentEvent) {
        this.startAdhocFragmentEvent = startAdhocFragmentEvent;
    }

    @Override
    public CaseActionSummary getValue() {
        return caseActionSummary.getModel();
    }

    @Override
    public void setValue(final CaseActionSummary model) {
        this.caseActionSummary.setModel(model);
        final CaseActionStatus actionStatus = CaseActionStatus.fromStatus(model.getStatus());

        switch (actionStatus) {
            case READY: {
                removeCSSClass(status, "hidden");
                setBulletStyles("availableColor");
                status.setTextContent(translationService.format(UNASSIGNED));
                break;
            }
            case RESERVED:
            case INPROGRESS: {
                removeCSSClass(status, "hidden");
                setBulletStyles("inProgressColor");
                status.setTextContent(model.getActualOwner());

                break;
            }
            case COMPLETED: {
                removeCSSClass(status, "hidden");
                setBulletStyles("pficon", "pficon-ok");
                break;
            }
            case SUSPENDED:
            case FAILED:
            case ERROR:
            case EXITED:
            case OBSOLETE: {
                removeCSSClass(status, "hidden");
                status.setTextContent(translationService.format(actionStatus.getStatus()));
                setBulletStyles("pficon", "pficon-error-circle-o");
                break;
            }

            case ADHOC_FRAGMENT: {
                setBulletStyles("availableColor");
                addAction(new CaseActionsPresenter.CaseActionAction() {
                    @Override
                    public String label() {
                        return translationService.format(ACTION_START);
                    }

                    @Override
                    public void execute() {
                        startAdhocFragmentEvent.fire(new CaseStartAdhocFragmentEvent(model.getName()));
                    }
                });
            }
        }
    }

    public void setBulletStyles(final String... stylesClass) {
        for (String styleClass : stylesClass) {
            addCSSClass(this.bullet, styleClass);
        }
    }

    public void addAction(final CaseActionsPresenter.CaseActionAction action) {
        removeCSSClass(actions, "hidden");

        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        a.setOnclick(e -> action.execute());

        final HTMLElement li = getDocument().createElement("li");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

}
