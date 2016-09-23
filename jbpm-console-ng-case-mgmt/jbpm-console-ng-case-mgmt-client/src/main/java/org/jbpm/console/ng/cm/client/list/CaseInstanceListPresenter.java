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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.view.client.Range;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.console.ng.cm.client.newcase.NewCaseInstancePresenter;
import org.jbpm.console.ng.cm.client.overview.CaseOverviewPresenter;
import org.jbpm.console.ng.cm.client.perspectives.CaseOverviewPerspective;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.client.events.CaseCancelEvent;
import org.jbpm.console.ng.cm.client.events.CaseCreatedEvent;
import org.jbpm.console.ng.cm.client.events.CaseDestroyEvent;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseInstanceListPresenter.SCREEN_ID)
public class CaseInstanceListPresenter extends AbstractScreenListPresenter<CaseInstanceSummary> {

    public static final String SCREEN_ID = "Case List";

    @Inject
    private CaseInstanceListView view;

    private Caller<CaseManagementService> caseService;

    private Event<CaseCancelEvent> caseCancelEvent;

    private Event<CaseDestroyEvent> caseDestroyEvent;

    @Inject
    private TranslationService translationService;

    @Inject
    private NewCaseInstancePresenter newCaseInstancePresenter;

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(final Range visibleRange) {
        if (currentFilter == null) {
            currentFilter = new PortableQueryFilter(
                    visibleRange.getStart(),
                    visibleRange.getLength(),
                    false,
                    "",
                    "",
                    false
            );
        }

        currentFilter.setOffset(visibleRange.getStart());
        currentFilter.setCount(visibleRange.getLength());

        caseService.call((List<CaseInstanceSummary> cases) -> {
            final PageResponse<CaseInstanceSummary> response = new PageResponse<CaseInstanceSummary>();
            response.setStartRowIndex(currentFilter.getOffset());
            response.setTotalRowSize(cases.size());
            response.setPageRowList(cases);
            response.setTotalRowSizeExact(cases.isEmpty());
            if (cases.size() < visibleRange.getLength()) {
                response.setLastPage(true);
            } else {
                response.setLastPage(false);
            }
            updateDataOnCallback(response);
        }).getCaseInstances(
                selectedServerTemplate,
                currentFilter.getOffset() / currentFilter.getCount(),
                currentFilter.getCount()
        );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASES_LIST);
    }

    @WorkbenchPartView
    public UberView<CaseInstanceListPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelMenu(translationService.format(NEW_CASE_INSTANCE))
                .respondsWith(() -> {
                    if (selectedServerTemplate != null && !selectedServerTemplate.isEmpty()) {
                        newCaseInstancePresenter.show(selectedServerTemplate);
                    } else {
                        view.displayNotification(translationService.format(SELECT_SERVER_TEMPLATE));
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(serverTemplateSelectorMenuBuilder)
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                .endMenu()
                .newTopLevelCustomMenu(new RefreshSelectorMenuBuilder(this))
                .endMenu()
                .build();
    }

    protected void selectCaseInstance(final CaseInstanceSummary cis) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CaseOverviewPresenter.PARAMETER_SERVER_TEMPLATE_ID, selectedServerTemplate);
        parameters.put(CaseOverviewPresenter.PARAMETER_CONTAINER_ID, cis.getContainerId());
        parameters.put(CaseOverviewPresenter.PARAMETER_CASE_ID, cis.getCaseId());
        final DefaultPlaceRequest overview = new DefaultPlaceRequest(CaseOverviewPerspective.PERSPECTIVE_ID, parameters);
        placeManager.goTo(overview);
    }

    protected void cancelCaseInstance(final CaseInstanceSummary caseInstanceSummary) {
        caseService.call(
                e -> caseCancelEvent.fire(new CaseCancelEvent(caseInstanceSummary.getCaseId()))
        ).cancelCaseInstance(selectedServerTemplate, caseInstanceSummary.getContainerId(), caseInstanceSummary.getCaseId());
    }

    protected void destroyCaseInstance(final CaseInstanceSummary caseInstanceSummary) {
        caseService.call(
                e -> caseDestroyEvent.fire(new CaseDestroyEvent(caseInstanceSummary.getCaseId()))
        ).destroyCaseInstance(selectedServerTemplate, caseInstanceSummary.getContainerId(), caseInstanceSummary.getCaseId());
    }

    public void onCaseCreatedEvent(@Observes CaseCreatedEvent event) {
        refreshGrid();
    }

    public void onCaseDestroyEvent(@Observes CaseDestroyEvent event) {
        refreshGrid();
    }

    public void onCaseCancelEvent(@Observes CaseCancelEvent event) {
        refreshGrid();
    }

    @Inject
    public void setCaseService(final Caller<CaseManagementService> caseService) {
        this.caseService = caseService;
    }

    @Inject
    public void setCaseCancelEvent(final Event<CaseCancelEvent> caseCancelEvent) {
        this.caseCancelEvent = caseCancelEvent;
    }

    @Inject
    public void setCaseDestroyEvent(final Event<CaseDestroyEvent> caseDestroyEvent) {
        this.caseDestroyEvent = caseDestroyEvent;
    }

    public interface CaseInstanceListView extends ListView<CaseInstanceSummary, CaseInstanceListPresenter> {
    }

}