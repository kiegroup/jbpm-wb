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

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.console.ng.cm.client.util.FormGroup;
import org.jbpm.console.ng.cm.client.util.ValidationState;
import org.jbpm.console.ng.gc.client.util.DateUtils;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;


@Dependent
@Templated
public class CaseCommentsViewImpl implements CaseCommentsPresenter.CaseCommentsView {

    @Inject
    @DataField("comments")
    Div commentsContainer;

    @Inject
    @DataField("comments-list")
    Div comments;

    @Inject
    @DataField("comment-creation")
    Div commentCreation;

    @Inject
    @DataField("comment-creation-input")
    TextInput newCommentTextArea;

    @Inject
    @DataField("comment-creation-label")
    Label newCommentLabel;

    @Inject
    @DataField("comment-creation-help")
    Span newCommentTextAreaHelp;

    @Inject
    @DataField("comment-creation-group")
    FormGroup newCommentTextAreaGroup;

    @Inject
    @DataField
    Button addCommentButton;

    @Inject
    private ManagedInstance<CaseCommentItemView> provider;

    @Inject
    private TranslationService translationService;

    private CaseCommentsPresenter presenter;

    @Override
    public void init(final CaseCommentsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void removeAllComments() {
        clearCommentInputForm();
        removeAllChildren(comments);
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
    public void addComment(boolean editing, String editActionLabel, final String commentId, final String author,
                           final String commentText, final Date commentAddedAt, final CaseCommentsPresenter.CaseCommentAction... actions) {
        addCommentView(editing, editActionLabel, commentId, author, commentText, commentAddedAt, "fa-comment-o", actions);
    }

    private void addCommentView(boolean editing, String editActionLabel, final String commentId, final String author,
                                final String commentText, final Date commentAddedAt, final String iconType,
                                final CaseCommentsPresenter.CaseCommentAction... actions) {
        final CaseCommentItemView commentItemView = provider.get();
        commentItemView.setCommentAuthor(author);
        commentItemView.setCommentText(commentText);
        commentItemView.setCommentAddedAt(DateUtils.getDateStr(commentAddedAt));

        commentItemView.setIconType(iconType);
        commentItemView.setEditMode(editing);

        if (editActionLabel != null) {
            for (CaseCommentsPresenter.CaseCommentAction action : actions) {
                commentItemView.addAction(action);
            }
            if(editing) {
                commentItemView.addUpdateCommentAction(new CaseCommentsPresenter.CaseCommentAction() {
                    @Override
                    public String label() {
                        return translationService.format(SAVE);
                    }

                    @Override
                    public void execute() {
                        if (commentItemView.validateForm()) {
                            presenter.updateCaseComment(commentItemView.getUpdatedComment(), commentId);
                            commentItemView.setEditMode(false);
                        }
                    }
                });
            }
            commentItemView.addAction(new CaseCommentsPresenter.CaseCommentAction() {
                @Override
                public String label() {
                    if(editing) {
                        return translationService.format(CANCEL);
                    }
                    return translationService.format(EDIT);
                }

                @Override
                public void execute() {
                    String cmmntId="";
                    if(!editing){
                        cmmntId = commentId ;
                    }
                    presenter.setCurrentUpdatedCommentId(cmmntId);
                    commentItemView.setEditMode(!editing);
                    presenter.refreshComments();
                }
            });
        }
        comments.appendChild(commentItemView.getElement());
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

    @Override
    public HTMLElement getElement() {
        return commentsContainer;
    }

}