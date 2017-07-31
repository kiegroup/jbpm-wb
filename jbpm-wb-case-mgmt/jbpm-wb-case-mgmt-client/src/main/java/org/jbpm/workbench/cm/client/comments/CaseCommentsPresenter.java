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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseCommentSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseCommentsPresenter.SCREEN_ID)
public class CaseCommentsPresenter extends AbstractCaseInstancePresenter<CaseCommentsPresenter.CaseCommentsView> {

    public static final int PAGE_SIZE = 20;
    
    public static final String SCREEN_ID = "Case Comments";

    @Inject
    User identity;

    boolean sortAsc = false;

    List<CaseCommentSummary> visibleComments = new ArrayList<CaseCommentSummary>();
    
    @Override
    public void setPageSize(int pageSize) {
    	this.pageSize = PAGE_SIZE;
    }
    
    @WorkbenchPartTitle
    public String getTittle() {
        return translationService.format(CASE_COMMENTS);
    }

    @Override
    protected void clearCaseInstance() {
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        refreshComments();
    }
    
    private void commentsServiceCall() {
        caseService.call(
                (List<CaseCommentSummary> comments) -> {
                    visibleComments.addAll(comments);
                    if (comments.size() < getPageSize()) {
                        view.hideLoadButton();
                    }
                    else {
                        view.showLoadButton();
                    }
                    view.setCaseCommentList(visibleComments.stream()
                            .sorted((sortAsc ?
                                    comparing(CaseCommentSummary::getAddedAt) :
                                    comparing(CaseCommentSummary::getAddedAt).reversed()))
                            .collect(toList()));
                }
        ).getComments(serverTemplateId, 
                      containerId, 
                      caseId, 
                      getCurrentPage(), 
                      getPageSize());
    }

    public void refreshComments() {
        view.clearCommentInputForm();
        visibleComments.clear();
        commentsServiceCall();
    }
    
    public void loadMoreCaseComments() {
        setCurrentPage(currentPage + 1);
        commentsServiceCall();       
    }

    public void sortComments(final boolean sortAsc) {
        this.sortAsc = sortAsc;
        refreshComments();
    }

    protected void addCaseComment(String caseCommentText) {
        caseService.call(
                (Void) -> {
                    view.resetPagination();
                }
        ).addComment(serverTemplateId,
                     containerId,
                     caseId,
                     identity.getIdentifier(),
                     caseCommentText);
    }

    protected void updateCaseComment(final CaseCommentSummary caseCommentSummary,
                                     String caseCommentNewText) {
        caseService.call(
                (Void) -> refreshComments()
        ).updateComment(serverTemplateId,
                        containerId,
                        caseId,
                        caseCommentSummary.getId(),
                        identity.getIdentifier(),
                        caseCommentNewText);
    }

    protected void deleteCaseComment(final CaseCommentSummary caseCommentSummary) {
        caseService.call(
                (Void) -> refreshComments()
        ).removeComment(serverTemplateId,
                        containerId,
                        caseId,
                        caseCommentSummary.getId());
    }

    public interface CaseCommentsView extends UberElement<CaseCommentsPresenter> {

        void clearCommentInputForm();

        void setCaseCommentList(List<CaseCommentSummary> caseCommentList);

        void resetPagination();
        
        void hideLoadButton();
        
        void showLoadButton();

    }

    public interface CaseCommentAction extends Command {

        String label();
    }
}