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
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseCommentsPresenter.SCREEN_ID)
public class CaseCommentsPresenter extends AbstractCaseInstancePresenter<CaseCommentsPresenter.CaseCommentsView> {

    public static final String SCREEN_ID = "Case Comments";

    @Inject
    User identity;

    private String currentUpdatedCommentId = "";

    @WorkbenchPartTitle
    public String getTittle() {
        return translationService.format(CASE_COMMENTS);
    }

    @Override
    protected void clearCaseInstance() {
        view.removeAllComments();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        refreshComments();
    }

    public void refreshComments() {
        clearCaseInstance();
        caseService.call(
                (List<CaseCommentSummary> comments) -> {
                    boolean editing = false;
                    String updateActionLabel;
                    for (CaseCommentSummary caseCommentSummary : comments) {
                        CaseCommentAction deleteCommentAction = null;
                        updateActionLabel = null;
                        if (identity.getIdentifier().equals(caseCommentSummary.getAuthor())) {
                            if (caseCommentSummary.getId().equals(currentUpdatedCommentId)) {
                                editing = true;
                                updateActionLabel = translationService.format(SAVE);
                            } else {
                                editing = false;
                                updateActionLabel = translationService.format(EDIT);
                            }
                            deleteCommentAction = new CaseCommentAction() {

                                @Override
                                public String label() {
                                    return translationService.format(DELETE);
                                }

                                @Override
                                public void execute() {
                                    deleteCaseComment(caseCommentSummary.getId());
                                }
                            };
                        }
                        view.addComment(editing, updateActionLabel, caseCommentSummary.getId(), caseCommentSummary.getAuthor(),
                                caseCommentSummary.getText(), caseCommentSummary.getAddedAt(), deleteCommentAction);
                    }
                }
        ).getComments(serverTemplateId, containerId, caseId);
    }

    protected void addCaseComment(String caseCommentText) {
        caseService.call(
                (Void) -> refreshComments()
        ).addComment(serverTemplateId, containerId, caseId, identity.getIdentifier(), caseCommentText);
    }

    protected void addCaseComment(final CaseCommentSummary caseCommentSummary) {
        caseService.call(
                (Void) -> refreshComments()
        ).addComment(serverTemplateId, containerId, caseId, caseCommentSummary.getAuthor(),
                caseCommentSummary.getText());
    }

    protected void updateCaseComment(String caseCommentText, String caseCommentId) {
        setCurrentUpdatedCommentId("");
        caseService.call(
                (Void) -> refreshComments()
        ).updateComment(serverTemplateId, containerId, caseId, caseCommentId, identity.getIdentifier(), caseCommentText);
    }

    protected void deleteCaseComment(final String caseCommentId) {
        setCurrentUpdatedCommentId("");
        caseService.call(
                (Void) -> refreshComments()
        ).removeComment(serverTemplateId, containerId, caseId, caseCommentId);
    }

    public void setCurrentUpdatedCommentId(String commentId) {
        currentUpdatedCommentId = commentId;
    }

    public interface CaseCommentsView extends UberElement<CaseCommentsPresenter> {

        void clearCommentInputForm();

        void removeAllComments();

        void addComment(boolean editing, String editActionLabel, String commentId, final String author,
                        final String commentText, final Date commentAddedAt,
                        final CaseCommentsPresenter.CaseCommentAction... actions);

    }

    public interface CaseCommentAction extends Command {

        String label();

    }

}