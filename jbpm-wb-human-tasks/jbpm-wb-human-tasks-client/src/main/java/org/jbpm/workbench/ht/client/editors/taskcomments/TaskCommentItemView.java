/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;

import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class TaskCommentItemView implements TakesValue<CommentSummary>,
                                            IsElement {

    private Constants constants = Constants.INSTANCE;

    @Inject
    @DataField("comment-author")
    @Bound(property = "addedBy")
    Span addedBy;

    @Inject
    @DataField("comment-text")
    @Bound(property = "text")
    Span text;

    @Inject
    @DataField("icon-type")
    Span iconType;

    @Inject
    @DataField("comment-addedat")
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
    ConfirmPopup confirmPopup;

    protected TaskCommentsPresenter presenter;

    public void init(TaskCommentsPresenter presenter) {
        this.presenter = presenter;
        updateActions(presenter.getDeleteCondition().test(getValue()));
    }

    @Inject
    @AutoBound
    private DataBinder<CommentSummary> commentSummary;

    public void setIconType(final String iconTypeClass) {
        addCSSClass(this.iconType, iconTypeClass);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    public void addAction(final TaskCommentsPresenter.CommentAction action) {
        removeCSSClass(actionsButton,
                       "disabled");

        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        a.setOnclick(e -> action.execute());

        final HTMLElement li = getDocument().createElement("li");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

    @Override
    public CommentSummary getValue() {
        return commentSummary.getModel();
    }

    @Override
    public void setValue(final CommentSummary model) {
        this.commentSummary.setModel(model);
        addedAt.setTextContent(DateUtils.getPrettyTime(model.getAddedAt()));
    }

    protected void updateActions(final boolean editItem) {
        actionsItems.setInnerHTML("");
        if (presenter.getDeleteCondition().test(getValue())) {
            addAction(new TaskCommentsPresenter.CommentAction() {
                @Override
                public String label() {
                    return constants.Delete();
                }

                @Override
                public void execute() {
                    confirmPopup.show(constants.DeleteCommentTitle(),
                                      constants.Delete(),
                                      constants.DeleteComment(getValue().getText()),
                                      () -> presenter.removeTaskComment(getValue().getId()));
                }
            });
        } else {
            setIconType("kie-no-highlight");
        }
    }
}
