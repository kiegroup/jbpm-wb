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

package org.jbpm.console.ng.cm.client.list;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Form;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.client.util.CaseStatusEnum;
import org.jbpm.console.ng.cm.client.util.Select;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;
import org.jbpm.console.ng.cm.util.CaseInstanceSortBy;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated("CaseInstanceListViewImpl.html#search-actions")
public class CaseInstanceListSearchViewImpl extends AbstractView<CaseInstanceListPresenter> {

    @Inject
    @DataField("search-actions")
    private Form actions;

    @Inject
    @Bound(converter = CaseInstanceSearchSortByConverter.class)
    @DataField("sort-select")
    private Select sortBy;

    @Inject
    @Bound
    @DataField("status-select")
    private Select status;

    @Inject
    @DataField("sort-case-id-asc")
    private Button sortCaseIdAsc;

    @Inject
    @DataField("sort-case-id-desc")
    private Button sortCaseIdDesc;

    @Inject
    @DataField("sort-started-asc")
    private Button sortStartedAsc;

    @Inject
    @DataField("sort-started-desc")
    private Button sortStartedDesc;

    @Inject
    @AutoBound
    private DataBinder<CaseInstanceSearchRequest> searchRequest;

    @Inject
    private TranslationService translationService;

    @Override
    public HTMLElement getElement() {
        return actions;
    }

    @PostConstruct
    public void init() {
        stream(CaseStatusEnum.values())
                .collect(toMap(e -> translationService.format(e.getLabel()), e -> e.getStatus()))
                .entrySet().stream().sorted((s1, s2) -> s1.getKey().compareTo(s2.getKey()))
                .forEach(e -> status.addOption(e.getKey(), e.getValue().toString()));
        status.refresh();

        stream(CaseInstanceSortBy.values()).collect(toMap(s -> s.name(), s -> translationService.format(s.name()))).entrySet().stream().sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue())).forEach(s -> sortBy.addOption(s.getValue(), s.getKey()));
        sortBy.refresh();

        searchRequest.addPropertyChangeHandler("sortBy", e -> {
            if(e.getNewValue() == CaseInstanceSortBy.CASE_ID){
                addCSSClass(sortStartedAsc, "hidden");
                addCSSClass(sortStartedDesc, "hidden");
                onSortCaseIdDesc(null);
            } else {
                addCSSClass(sortCaseIdAsc, "hidden");
                addCSSClass(sortCaseIdDesc, "hidden");
                onSortStartedDesc(null);
            }
        });

        searchRequest.setModel(new CaseInstanceSearchRequest(), StateSync.FROM_MODEL);
        searchRequest.addPropertyChangeHandler(e -> presenter.searchCaseInstances());
    }

    public CaseInstanceSearchRequest getCaseInstanceSearchRequest() {
        return searchRequest.getModel();
    }

    @EventHandler("start-case")
    public void onCreateCaseClick(final @ForEvent("click") MouseEvent event) {
        presenter.createCaseInstance();
    }

    @EventHandler("sort-case-id-asc")
    public void onSortCaseIdAsc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortCaseIdAsc, sortCaseIdDesc, false);
    }

    @EventHandler("sort-case-id-desc")
    public void onSortCaseIdDesc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortCaseIdDesc, sortCaseIdAsc, true);
    }

    @EventHandler("sort-started-asc")
    public void onSortStartedAsc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortStartedAsc, sortStartedDesc, false);
    }

    @EventHandler("sort-started-desc")
    public void onSortStartedDesc(final @ForEvent("click") MouseEvent event) {
        onSortChange(sortStartedDesc, sortStartedAsc, true);
    }

    private void onSortChange(final HTMLElement toHide, final HTMLElement toShow, final Boolean sortByAsc){
        addCSSClass(toHide, "hidden");
        removeCSSClass(toShow, "hidden");
        final CaseInstanceSearchRequest model = searchRequest.getWorkingModel();
        model.setSortByAsc(sortByAsc);
    }

}