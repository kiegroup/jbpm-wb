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

package org.jbpm.console.ng.cm.client.comments;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;

import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.FormGroup;
import org.jbpm.console.ng.cm.client.util.ValidationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.CASE_COMMENT_CANT_BE_EMPTY;


@Dependent
@Templated
public class CaseCommentItemView implements IsElement {

    @Inject
    @DataField("comment-author")
    Span commentAuthor;

    @Inject
    @DataField("comment-text")
    Span commentText;

    @Inject
    @DataField("comment-addedat")
    Span commentAddedAt;

    @Inject
    @DataField("user-actions")
    Div userActions;

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
    @DataField("comment-update-input")
    TextInput updateCommentText;

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
    private TranslationService translationService;

    CaseCommentsPresenter.CaseCommentAction updateCommandAction;

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    public void setCommentAuthor(final String commentAutor) {
        this.commentAuthor.setInnerHTML(commentAutor);
    }

    public void setCommentText(final String commentText) {
        this.commentText.setTextContent(commentText);
        this.updateCommentText.setValue(commentText);
    }

    public void setCommentAddedAt(final String commentAddedAt) {
        this.commentAddedAt.setInnerHTML(commentAddedAt);
    }

    public void addAction(final CaseCommentsPresenter.CaseCommentAction action) {
        removeCSSClass(actions, "hidden");

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

    public String getUpdatedComment() {
        return updateCommentText.getValue();
    }

    public void setEditMode(boolean editMode) {
        if (editMode) {
            addCSSClass(commentShowGroup, "hidden");
            removeCSSClass(commentUpdate, "hidden");
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

    @EventHandler("comment-update-input")
    @SuppressWarnings("unsued")
    public void updateCommentPressingEnter(@ForEvent("keyup") final KeyboardEvent e) {
        //Chrome bug, key is not set
        if ("Enter".equals(e.getKey()) || "Enter".equals(e.getCode()) || "NumpadEnter".equals(e.getCode())) {
            updateCommandAction.execute();
        }
    }

}
