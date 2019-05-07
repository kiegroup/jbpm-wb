/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.IntStream;

import org.jbpm.document.Document;
import org.jbpm.workbench.common.model.PortableQueryFilter;
import org.jbpm.workbench.common.model.QueryFilter;
import org.jbpm.workbench.pr.backend.server.util.VariableHelper;
import org.jbpm.workbench.pr.model.DocumentSummary;
import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.jbpm.workbench.pr.service.ProcessVariablesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.paging.PageResponse;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessDocumentsServiceImplTest {

    private final String serverTemplateId = "serverTemplateId";

    @Mock
    private ProcessVariablesService processVariablesService;

    @InjectMocks
    private RemoteProcessDocumentsServiceImpl processDocumentsService;

    private static ProcessVariableSummary newDocumentVariable() {
        return new ProcessVariableSummary("",
                                          "",
                                          1l,
                                          "",
                                          "docId" + Document.PROPERTIES_SEPARATOR + "1" + Document.PROPERTIES_SEPARATOR + new SimpleDateFormat(Document.DOCUMENT_DATE_PATTERN).format(new Date()) + Document.PROPERTIES_SEPARATOR + "1",
                                          0l,
                                          VariableHelper.JBPM_DOCUMENT);
    }

    @Test
    public void testGetData() {
        PageResponse<ProcessVariableSummary> variablesResponse = new PageResponse<>();
        variablesResponse.setPageRowList(singletonList(newDocumentVariable()));

        when(processVariablesService.getData(any())).thenReturn(variablesResponse);

        QueryFilter queryFilter = new PortableQueryFilter(0,
                                                          10,
                                                          false,
                                                          "",
                                                          "",
                                                          false);

        queryFilter.getParams().put(serverTemplateId, serverTemplateId);

        final PageResponse<DocumentSummary> response = processDocumentsService.getData(queryFilter);

        assertEquals(1,
                     response.getTotalRowSize());
        assertEquals(0,
                     response.getStartRowIndex());
        assertTrue(response.isTotalRowSizeExact());
        assertTrue(response.isFirstPage());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testGetDataPaginated() {
        int totalItems = 12;

        PageResponse<ProcessVariableSummary> variablesResponse = new PageResponse<>();
        variablesResponse.setPageRowList(new ArrayList<>());
        IntStream.range(0, totalItems).forEach(i -> variablesResponse.getPageRowList().add(newDocumentVariable()));

        when(processVariablesService.getData(any())).thenReturn(variablesResponse);

        QueryFilter queryFilter = new PortableQueryFilter(0,
                                                          10,
                                                          false,
                                                          "",
                                                          "",
                                                          false);

        queryFilter.getParams().put(serverTemplateId, serverTemplateId);

        final PageResponse<DocumentSummary> response = processDocumentsService.getData(queryFilter);

        assertEquals(totalItems,
                     response.getTotalRowSize());
        assertEquals(0,
                     response.getStartRowIndex());
        assertTrue(response.isTotalRowSizeExact());
        assertTrue(response.isFirstPage());
        assertFalse(response.isLastPage());
    }
}