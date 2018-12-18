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
package org.jbpm.workbench.pr.client.editors.documents.list;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.list.ListView;
import org.jbpm.workbench.common.model.PortableQueryFilter;
import org.jbpm.workbench.common.client.list.AbstractListPresenter;
import org.jbpm.workbench.pr.client.editors.instance.ProcessInstanceSummaryAware;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.DocumentSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessDocumentsService;
import org.uberfire.paging.PageResponse;

@Dependent
public class ProcessDocumentListPresenter extends AbstractListPresenter<DocumentSummary> implements ProcessInstanceSummaryAware {

    private Constants constants = Constants.INSTANCE;

    @Inject
    private ProcessDocumentListView view;

    @Inject
    private Caller<ProcessDocumentsService> documentsServices;

    private ProcessInstanceSummary processInstance;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getWidget() {
        return view;
    }

    @Override
    public void setProcessInstance(ProcessInstanceSummary processInstance) {
        this.processInstance = processInstance;
        refreshGrid();
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(Range visibleRange) {
        if (processInstance != null) {
            ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
            if (currentFilter == null) {
                currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                                                        visibleRange.getLength(),
                                                        false,
                                                        "",
                                                        columnSortList.size() > 0 ? columnSortList.get(0)
                                                                .getColumn().getDataStoreName() : "",
                                                        columnSortList.size() == 0 || columnSortList.get(0).isAscending());
            }
            // If we are refreshing after a search action, we need to go back to offset 0
            if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                    || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
                currentFilter.setOffset(visibleRange.getStart());
                currentFilter.setCount(visibleRange.getLength());
            } else {
                currentFilter.setOffset(0);
                currentFilter.setCount(view.getListGrid().getPageSize());
            }
            //Applying screen specific filters
            if (currentFilter.getParams() == null) {
                currentFilter.setParams(new HashMap<String, Object>());
            }
            currentFilter.getParams().put("processInstanceId",
                                          processInstance.getProcessInstanceId().toString());
            currentFilter.getParams().put("processDefId",
                                          processInstance.getProcessId());
            currentFilter.getParams().put("deploymentId",
                                          processInstance.getDeploymentId());
            currentFilter.getParams().put("serverTemplateId",
                                          processInstance.getServerTemplateId());

            currentFilter.setOrderBy(columnSortList.size() > 0 ? columnSortList.get(0)
                    .getColumn().getDataStoreName() : "");
            currentFilter.setIsAscending(columnSortList.size() == 0 || columnSortList.get(0).isAscending());

            documentsServices.call(new RemoteCallback<PageResponse<DocumentSummary>>() {
                                       @Override
                                       public void callback(PageResponse<DocumentSummary> response) {
                                           updateDataOnCallback(response);
                                       }
                                   },
                                   new ErrorCallback<Message>() {
                                       @Override
                                       public boolean error(Message message,
                                                            Throwable throwable) {
                                           view.hideBusyIndicator();
                                           view.displayNotification(constants.ErrorRetrievingProcessDocuments(throwable.getMessage()));
                                           GWT.log(throwable.toString());
                                           return true;
                                       }
                                   }).getData(currentFilter);
        }
    }

    public interface ProcessDocumentListView extends ListView<DocumentSummary, ProcessDocumentListPresenter> {

    }
}