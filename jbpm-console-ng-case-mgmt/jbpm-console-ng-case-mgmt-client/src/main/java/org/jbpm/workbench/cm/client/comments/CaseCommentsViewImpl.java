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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.FormGroup;
import org.jbpm.workbench.cm.client.util.ValidationState;
import org.jbpm.workbench.cm.model.CaseCommentSummary;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;


@Dependent
@Templated
public class CaseCommentsViewImpl extends AbstractView<CaseCommentsPresenter> implements CaseCommentsPresenter.CaseCommentsView {

    @Inject
    @DataField("comments")
    Div commentsContainer;

    @Inject
    @Bound
    @DataField("comments-list")
    private ListComponent<CaseCommentSummary, CaseCommentItemView> comments;

    @Inject
    @AutoBound
    private DataBinder<List<CaseCommentSummary>> caseCommentList;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("comment-creation-input")
    TextInput newCommentTextArea;

    @Inject
    @DataField("comment-creation-help")
    Span newCommentTextAreaHelp;

    @Inject
    @DataField("comment-creation-group")
    FormGroup newCommentTextAreaGroup;

    @Inject
    @DataField
    Anchor addCommentButton;

    @Inject
    private TranslationService translationService;

    @Override
    public HTMLElement getElement() {
        return commentsContainer;
    }

    @Override
    public void init(final CaseCommentsPresenter presenter) {
        this.presenter = presenter;
        comments.addComponentCreationHandler(v -> v.init(presenter));
    }

    @Override
    public void clearCommentInputForm() {
        newCommentTextArea.setValue("");
        clearErrorMessages();
    }

    public void clearErrorMessages() {
        newCommentTextAreaHelp.setTextContent("");
        newCommentTextAreaGroup.clearValidationState();
    }

    @Override
    public void setCaseCommentList(final List<CaseCommentSummary> caseCommentList) {
        this.caseCommentList.setModel(caseCommentList);
        if (caseCommentList.isEmpty()) {
            removeCSSClass(emptyContainer, "hidden");
        } else {
            addCSSClass(emptyContainer, "hidden");
        }
    }

    @EventHandler("addCommentButton")
    @SuppressWarnings("unsued")
    public void addCommentButton(@ForEvent("click") final Event e) {
        submitCommentAddition();
    }

    @EventHandler("comment-creation-input")
    @SuppressWarnings("unsued")
    public void addCommentPressingEnter(@ForEvent("keyup") final KeyboardEvent e) {
        //Chrome bug, key is not set
        if ("Enter".equals(e.getKey()) || "Enter".equals(e.getCode()) || "NumpadEnter".equals(e.getCode())) {
            submitCommentAddition();
        }
    }

    private void submitCommentAddition(){
        if (validateForm()) {
            presenter.addCaseComment(newCommentTextArea.getValue());
        }
    }

    private boolean validateForm() {
        clearErrorMessages();

        final boolean newCommentEmpty = isNullOrEmpty(newCommentTextArea.getValue());
        if (newCommentEmpty) {
            newCommentTextArea.focus();
            newCommentTextAreaHelp.setTextContent(translationService.format(CASE_COMMENT_CANT_BE_EMPTY));
            newCommentTextAreaGroup.setValidationState(ValidationState.ERROR);
            return false;
        }
        return true;
    }

}