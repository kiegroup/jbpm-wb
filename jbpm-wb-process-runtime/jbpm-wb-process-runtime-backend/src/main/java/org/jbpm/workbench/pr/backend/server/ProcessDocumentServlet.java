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
package org.jbpm.workbench.pr.backend.server;

import java.io.IOException;
import java.text.MessageFormat;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.kie.server.api.model.instance.DocumentInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.server.util.FileServletUtil;

@WebServlet(name = "ProcessDocumentServlet", urlPatterns = "/jbpm/documents")
public class ProcessDocumentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDocumentServlet.class);
    public static final String ERROR_RETRIEVING_DOC = "Error occured during document retrieval \"{0}\".";
    public static final String INVALID_SERVICES_CLIENT = "Unable to retrieve services client with template id \"{0}\"";
    public static final String INVALID_DOCUMENT = "Unable to retrieve document with id \"{0}\"";
    public static final String INVALID_PARAMS = "Invalid parameters to servlet: templateid: \"{0}\", docid: \"{1}\"";

    @Inject
    private KieServerIntegration kieServerIntegration;

    public ProcessDocumentServlet() {
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            String templateId = FileServletUtil.encodeFileNamePart(request.getParameter("templateid"));
            String docId = FileServletUtil.encodeFileNamePart(request.getParameter("docid"));

            if (templateId == null || docId == null) {
                logger.error(MessageFormat.format(INVALID_PARAMS,
                                                  templateId,
                                                  docId));
                return;
            }

            KieServicesClient kieServicesClient = kieServerIntegration.getServerClient(templateId);
            if (kieServicesClient != null) {
                DocumentServicesClient documentServicesClient = kieServicesClient.getServicesClient(DocumentServicesClient.class);
                DocumentInstance documentInstance = documentServicesClient.getDocument(docId);

                if (documentInstance != null) {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition",
                                       "attachment; filename=\"" + documentInstance.getName() + "\"");
                    response.getOutputStream().write(documentInstance.getContent());
                } else {
                    logger.error(MessageFormat.format(INVALID_DOCUMENT,
                                                      docId));
                }
            } else {
                logger.error(MessageFormat.format(INVALID_SERVICES_CLIENT,
                                                  templateId));
                return;
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format(ERROR_RETRIEVING_DOC,
                                              e.getMessage()));
            return;
        }
    }

    // for testing
    public void setKieServerIntegration(KieServerIntegration kieServerIntegration) {
        this.kieServerIntegration = kieServerIntegration;
    }
}
