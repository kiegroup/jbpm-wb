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
package org.jbpm.workbench.ht.client.editors.taskcomments;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.common.client.util.AbstractView;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.ValidationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated(value = "TaskCommentsViewImpl.html", stylesheet = "TaskCommentsViewImpl.css")
public class TaskCommentsViewImpl extends AbstractView<TaskCommentsPresenter>
        implements TaskCommentsPresenter.TaskCommentsView {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("add-comment-div")
    Div addCommentDiv;

    @Inject
    @DataField("comments-header-div")
    Div commentsHeaderDiv;

    @Inject
    @DataField("load-div")
    Div loadDiv;

    @Inject
    @DataField("load-more-comments")
    @SuppressWarnings("PMD.UnusedPrivateField")
    private Button loadMoreComments;

    @Inject
    @DataField("commentsDiv")
    Div commentsContainer;

    @Inject
    @DataField("comment-creation-input")
    TextArea newCommentTextArea;

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
    @DataField("sort-alpha-asc")
    private Button sortAlphaAsc;

    @Inject
    @DataField("sort-alpha-desc")
    private Button sortAlphaDesc;

    @Inject
    @Bound
    @DataField("comments-list")
    private ListComponent<CommentSummary, TaskCommentItemView> comments;

    @Inject
    @AutoBound
    private DataBinder<List<CommentSummary>> commentList;

    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    protected TaskCommentsPresenter presenter;

    @PostConstruct
    public void init() {
        tooltip(sortAlphaAsc);
        sortAlphaAsc.setAttribute("data-original-title", constants.SortByDateDesc());
        tooltip(sortAlphaDesc);
        sortAlphaDesc.setAttribute("data-original-title", constants.SortByDateAsc());
    }

    @Override
    public HTMLElement getElement() {
        return commentsContainer;
    }

    @Override
    public void init(final TaskCommentsPresenter presenter) {
        this.presenter = presenter;
        comments.addComponentCreationHandler(v -> v.init(presenter));
        newCommentTextArea.setOnkeypress((KeyboardEvent e) -> {
            if ((e == null || e.getKeyCode() == KeyCodes.KEY_ENTER) && newCommentTextArea.getValue().isEmpty() == false) {
                submitCommentAddition();
            }
        });
    }

    @Override
    public void clearCommentInputForm() {
        newCommentTextArea.setValue("");
        clearErrorMessages();
    }

    @Override
    public void disableNewComments() {
        addCSSClass(addCommentDiv, "hidden");
    }

    public void clearErrorMessages() {
        newCommentTextAreaHelp.setTextContent("");
        newCommentTextAreaGroup.clearValidationState();
    }

    @Override
    public void resetPagination() {
        presenter.setCurrentPage(1);
        onSortChange(sortAlphaAsc, sortAlphaDesc, false);
    }

    private void addOrRemoveClassNameOnElement(HTMLElement element, boolean adding, String className) {
        if (adding) {
            addCSSClass(element, className);
        } else {
            removeCSSClass(element, className);
        }
    }

    @Override
    public void setCommentList(final List<CommentSummary> commentSummaries) {
        this.commentList.setModel(commentSummaries);
        addOrRemoveClassNameOnElement(emptyContainer, !commentSummaries.isEmpty(), "hidden");
    }

    @Override
    public void hideLoadButton() {
        loadDiv.setHidden(true);
    }

    @Override
    public void showLoadButton() {
        loadDiv.setHidden(false);
    }

    @Override
    public void showCommentHeader() {
        removeCSSClass(commentsHeaderDiv, "hidden");
    }

    @EventHandler("addCommentButton")
    @SuppressWarnings("unsued")
    public void addCommentButton(@ForEvent("click") final Event e) {
        submitCommentAddition();
    }

    protected void submitCommentAddition() {
        if (validateForm()) {
            presenter.addTaskComment(newCommentTextArea.getValue());
        }
    }

    private boolean validateForm() {
        clearErrorMessages();

        final boolean newCommentEmpty = isNullOrEmpty(newCommentTextArea.getValue());
        if (newCommentEmpty) {
            newCommentTextArea.focus();
            newCommentTextAreaHelp.setTextContent(constants.CommentCannotBeEmpty());
            newCommentTextAreaGroup.setValidationState(ValidationState.ERROR);
            return false;
        }
        return true;
    }

    @EventHandler("sort-alpha-asc")
    public void onSortAlphaAsc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortAlphaAsc, sortAlphaDesc, false);
    }

    @EventHandler("sort-alpha-desc")
    public void onSortAlphaDesc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortAlphaDesc, sortAlphaAsc, true);
    }

    private void onSortChange(final HTMLElement toHide,
                              final HTMLElement toShow,
                              final Boolean sortByAsc) {
        addOrRemoveClassNameOnElement(toHide, true, "hidden");
        addOrRemoveClassNameOnElement(toShow, false, "hidden");
        presenter.sortComments(sortByAsc);
    }

    @EventHandler("load-more-comments")
    public void loadMoreComments(final @ForEvent("click") MouseEvent event) {
        presenter.loadMoreTaskComments();
    }
}
