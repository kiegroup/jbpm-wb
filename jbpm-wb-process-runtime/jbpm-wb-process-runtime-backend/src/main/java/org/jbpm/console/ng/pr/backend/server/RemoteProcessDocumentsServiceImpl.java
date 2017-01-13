/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.pr.backend.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ks.integration.AbstractKieServerService;
import org.jbpm.console.ng.pr.model.DocumentSummary;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.service.ProcessDocumentsService;
import org.jbpm.console.ng.pr.service.ProcessVariablesService;
import org.jbpm.document.Document;
import org.kie.server.client.DocumentServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class RemoteProcessDocumentsServiceImpl extends AbstractKieServerService implements ProcessDocumentsService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteProcessDocumentsServiceImpl.class);

    @Inject
    private ProcessVariablesService processVariablesService;

    @Override
    public PageResponse<DocumentSummary> getData(QueryFilter filter) {
        PageResponse<DocumentSummary> response = new PageResponse<DocumentSummary>();
        List<DocumentSummary> documents = getDocuments(filter);

        response.setStartRowIndex(filter.getOffset());
        response.setTotalRowSize(documents.size() - 1);

        response.setTotalRowSizeExact(true);
        response.setTotalRowSize(documents.size());

        if (!documents.isEmpty()) {
            if (documents.size() > (filter.getCount() + filter.getOffset())) {
                response.setPageRowList(new ArrayList<DocumentSummary>(documents.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
                response.setLastPage(false);
            } else {
                response.setPageRowList(new ArrayList<DocumentSummary>(documents.subList(filter.getOffset(), documents.size())));
                response.setLastPage(true);
            }

        } else {
            response.setPageRowList(new ArrayList<DocumentSummary>(documents));
            response.setLastPage(true);

        }
        return response;

    }

    private List<DocumentSummary> getDocuments(QueryFilter filter) throws NumberFormatException {

        String serverTemplateId = (String) filter.getParams().get("serverTemplateId");

        Collection<ProcessVariableSummary> processVariables = processVariablesService.getData(filter).getPageRowList();
        SimpleDateFormat sdf = new SimpleDateFormat(Document.DOCUMENT_DATE_PATTERN);
        List<DocumentSummary> documents = new ArrayList<DocumentSummary>();
        for (ProcessVariableSummary pv : processVariables) {
            if ("org.jbpm.document.Document".equals(pv.getType()) &&
                pv.getNewValue() != null && !pv.getNewValue().isEmpty()) {
                    String[] values = pv.getNewValue().split(Document.PROPERTIES_SEPARATOR);
                    if (values.length == 4) {
                        Date lastModified = null;
                        try {
                            lastModified = sdf.parse(values[2]);
                        } catch (ParseException ex) {
                            logger.error("Can not parse last modified date!", ex);
                        }
                        documents.add(new DocumentSummary(values[0], lastModified, Long.valueOf(values[1]), getDocumentLink(serverTemplateId, values[3])));
                    }
            }
        }
        return documents;
    }

    @Override
    public String getDocumentLink(final String serverTemplateId, final String documentIdentifier) {
        DocumentServicesClient documentClient = getClient(serverTemplateId, DocumentServicesClient.class);
        return documentClient.getDocumentLink(documentIdentifier);
    }

}