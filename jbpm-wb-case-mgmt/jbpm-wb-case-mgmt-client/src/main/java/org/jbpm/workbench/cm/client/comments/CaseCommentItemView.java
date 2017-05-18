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

package org.jbpm.workbench.cm.client.comments;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.cm.client.events.CaseCommentEditEvent;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.DateConverter;
import org.jbpm.workbench.cm.model.CaseCommentSummary;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.ValidationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class CaseCommentItemView extends AbstractView<CaseCommentsPresenter> implements TakesValue<CaseCommentSummary>, IsElement {

    @Inject
    @DataField("comment-author")
    @Bound
    Span author;

    @Inject
    @DataField("comment-text")
    @Bound(property = "text")
    Span text;

    @Inject
    @DataField("icon-type")
    Span iconType;

    @Inject
    @DataField("comment-addedat")
    @Bound(converter = DateConverter.class)
    Span addedAt;

    @Inject
    @DataField("list-group-item")
    Div listGroupItem;

    @Inject
    @DataField("actions-dropdown")
    Div actions;

    @Inject
    @DataField("actions-button")
    Button actionsButton;

    @Inject
    @DataField("actions-items")
    UnorderedList actionsItems;

    @Inject
    @DataField("comment-update-input")
    TextArea updateCommentText;

    @Inject
    @DataField("comment-update-help")
    Span updateCommentTextHelp;

    @Inject
    @DataField("comment-update-group")
    FormGroup updateCommentTextGroup;

    @Inject
    @DataField("comment-show")
    Div commentShowGroup;

    @Inject
    @DataField("comment-update")
    Div commentUpdate;

    @Inject
    @AutoBound
    private DataBinder<CaseCommentSummary> caseCommentSummary;

    @Inject
    private TranslationService translationService;

    @Inject
    User identity;

    @Inject
    ConfirmPopup confirmPopup;

    private Event<CaseCommentEditEvent> commentEditEvent;

    CaseCommentsPresenter.CaseCommentAction updateCommandAction;

    boolean editMode = false;

    @Inject
    public void setCommentEditEvent(final Event<CaseCommentEditEvent> commentEditEvent) {
        this.commentEditEvent = commentEditEvent;
    }

    public void setIconType(final String iconTypeClass) {
        addCSSClass(this.iconType, iconTypeClass);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    public void addAction(final CaseCommentsPresenter.CaseCommentAction action) {
        removeCSSClass(actionsButton, "disabled");

        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        a.setOnclick(e -> action.execute());

        final HTMLElement li = getDocument().createElement("li");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

    public void addUpdateCommentAction(final CaseCommentsPresenter.CaseCommentAction action) {
        updateCommandAction = action;
        addAction(action);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        updateActions(editMode);
        if (editMode) {
            addCSSClass(commentShowGroup, "hidden");
            removeCSSClass(commentUpdate, "hidden");
            updateCommentText.setValue(getValue().getText());
            clearErrorMessages();
            updateCommentText.focus();
        } else {
            addCSSClass(commentUpdate, "hidden");
            removeCSSClass(commentShowGroup, "hidden");
        }
    }

    public void clearErrorMessages() {
        updateCommentTextHelp.setTextContent("");
        updateCommentTextGroup.clearValidationState();
    }

    public boolean validateForm() {
        clearErrorMessages();
        final boolean newCommentEmpty = isNullOrEmpty(updateCommentText.getValue());
        if (newCommentEmpty) {
            updateCommentText.focus();
            updateCommentTextHelp.setTextContent(translationService.format(CASE_COMMENT_CANT_BE_EMPTY));
            updateCommentTextGroup.setValidationState(ValidationState.ERROR);
            return false;
        }
        return true;
    }

    @EventHandler("update-comment")
    public void onUpdateCommentClick(@ForEvent("click") org.jboss.errai.common.client.dom.Event e) {
        if (updateCommandAction != null) {
            updateCommandAction.execute();
        }
    }

    @EventHandler("cancel-edition")
    public void onCancelEdtionClick(@ForEvent("click") org.jboss.errai.common.client.dom.Event e) {
        setEditMode(false);

    }

    @Override
    public CaseCommentSummary getValue() {
        return caseCommentSummary.getModel();
    }

    @Override
    public void setValue(final CaseCommentSummary model) {
        this.caseCommentSummary.setModel(model);
        setEditMode(false);
    }

    protected void updateActions(final boolean editItem) {
        actionsItems.setInnerHTML("");
        if (identity.getIdentifier().equals(getValue().getAuthor())) {
            if (editItem) {
                addUpdateCommentAction(new CaseCommentsPresenter.CaseCommentAction() {
                    @Override
                    public String label() {
                        return translationService.format(UPDATE);
                    }

                    @Override
                    public void execute() {
                        if (validateForm()) {
                            presenter.updateCaseComment(getValue(), updateCommentText.getValue());
                        }
                    }
                });
                addAction(new CaseCommentsPresenter.CaseCommentAction() {
                    @Override
                    public String label() {
                        return translationService.format(CANCEL);
                    }

                    @Override
                    public void execute() {
                        setEditMode(false);
                    }
                });
            } else {
                addAction(new CaseCommentsPresenter.CaseCommentAction() {
                    @Override
                    public String label() {
                        return translationService.format(EDIT);
                    }

                    @Override
                    public void execute() {
                        commentEditEvent.fire(new CaseCommentEditEvent(getValue().getId()));
                    }
                });
            }
            addAction(new CaseCommentsPresenter.CaseCommentAction() {
                @Override
                public String label() {
                    return translationService.format(DELETE);
                }

                @Override
                public void execute() {
                    confirmPopup.show(translationService.format(DELETE_COMMENT),
                            translationService.format(DELETE),
                            translationService.format(DELETE_THIS_COMMENT),
                            () -> presenter.deleteCaseComment(getValue()));
                }
            });
        } else {
            setIconType("kie-no-highlight");
        }
    }

    public void onCaseCommentEditEvent(@Observes CaseCommentEditEvent event) {
        if (event.getCommentId().equals(getValue().getId())) {
            setEditMode(true);
        } else {
            if (editMode) {
                setEditMode(false);
            }
        }
    }

    public void setLastElementStyle(){
        addCSSClass(actions, "dropup");
    }

}
