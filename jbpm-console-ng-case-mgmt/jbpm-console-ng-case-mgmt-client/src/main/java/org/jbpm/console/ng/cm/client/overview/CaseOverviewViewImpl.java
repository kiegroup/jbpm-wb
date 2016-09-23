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
package org.jbpm.console.ng.cm.client.overview;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(stylesheet = "CaseOverviewViewImpl.css")
public class CaseOverviewViewImpl extends Composite implements CaseOverviewPresenter.CaseOverviewView {

    @Inject
    @DataField("case-details-content")
    FlowPanel caseDetails;

    @Inject
    @DataField("side-bar-left")
    FlowPanel sideBarLeft;

    @Inject
    @DataField("side-bar-right")
    FlowPanel sideBarRight;

    @Inject
    @DataField("case-stages")
    FlowPanel caseStages;

    @Inject
    @DataField("case-comments")
    FlowPanel caseComments;

    @Inject
    @DataField("case-files")
    FlowPanel caseFiles;

    @Inject
    @DataField("case-roles")
    FlowPanel caseRoles;

    @Inject
    @DataField("case-milestones")
    FlowPanel caseMilestones;

    @Inject
    @DataField("case-title")
    Span caseTitle;

    @Inject
    @DataField("case-id")
    Span caseId;

    @Inject
    @DataField("case-destroy")
    Button destroyCase;

    @Inject
    @DataField("case-cancel")
    Button cancelCase;

    @Inject
    @DataField("backToList")
    Button backToList;

    @Inject
    @DataField("refresh")
    Button refresh;

    @Inject
    PlaceManager placeManager;

    private CaseOverviewPresenter presenter;

    @PostConstruct
    protected void init(){
        backToList.setType(ButtonType.LINK);
        refresh.setType(ButtonType.LINK);
    }

    @Override
    public void init(final CaseOverviewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addCaseDetails(final String placeId, final Map<String, String> properties) {
        addWidget(placeId, properties, caseDetails);
    }

    @Override
    public void addCaseActions(final String placeId, final Map<String, String> properties) {
        addWidget(placeId, properties, sideBarLeft);
    }

    @Override
    public void addCaseStages(final String placeId, final Map<String, String> properties) {
        addWidget(placeId, properties, caseStages);
    }

    @Override
    public void addCaseComments(String placeId, Map<String, String> properties) {
        addWidget(placeId, properties, caseComments);
    }

    @Override
    public void addCaseFiles(String placeId, Map<String, String> properties) {
        addWidget(placeId, properties, caseFiles);
    }

    @Override
    public void addCaseRoles(String placeId, Map<String, String> properties) {
        addWidget(placeId, properties, caseRoles);
    }

    @Override
    public void addCaseMilestones(String placeId, Map<String, String> properties) {
        addWidget(placeId, properties, caseMilestones);
    }

    @Override
    public void addCaseActivities(String placeId, Map<String, String> properties) {
        addWidget(placeId, properties, sideBarRight);
    }

    private void addWidget(final String placeId, final Map<String, String> properties, final HasWidgets widget) {
        placeManager.goTo(new DefaultPlaceRequest(placeId, properties), widget);
    }

    @Override
    public void setCaseTitle(final String title) {
        caseTitle.setText(title);
    }

    @Override
    public void setCaseId(final String description) {
        caseId.setText(description);
    }

    @EventHandler("case-destroy")
    @SuppressWarnings("unsued")
    protected void onDestroyClick(final ClickEvent event) {
        presenter.destroyCaseInstance();
    }

    @EventHandler("case-cancel")
    @SuppressWarnings("unsued")
    protected void onCancelClick(final ClickEvent event) {
        presenter.cancelCaseInstance();
    }

    @EventHandler("backToList")
    @SuppressWarnings("unsued")
    protected void onBackToListClick(final ClickEvent event) {
        presenter.backToList();
    }

    @EventHandler("refresh")
    @SuppressWarnings("unsued")
    protected void onRefreshClick(final ClickEvent event) {
        presenter.refreshCase();
    }

}