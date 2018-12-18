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

package org.jbpm.workbench.forms.display.backend;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.kie.server.api.marshalling.json.StringContentCaseFile;
import org.kie.server.api.marshalling.json.StringContentMap;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;


@WebServlet(name="FormRendererProxyServlet", urlPatterns="/jbpm/forms/*")
public class FormRendererProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 4517893706538216846L;
    
    private static final String RENDERER = System.getProperty("org.jbpm.wb.forms.renderer.name", UIServicesClient.WORKBENCH_FORM_RENDERER);
    
    private Pattern containerPattern = Pattern.compile(".*/containers/([^/]+).*");
    private Pattern caseIdPattern = Pattern.compile(".*/cases/([^/]+).*");
    private Pattern processIdPattern = Pattern.compile(".*/processes/([^/]+).*");
    private Pattern taskIdPattern = Pattern.compile(".*/tasks/([^/]+).*");
    private Pattern taskStatePattern = Pattern.compile(".*/states/([^/]+).*");
    
    @Inject
    protected KieServerIntegration kieServerIntegration;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // return form content
        String serverTemplateId = req.getParameter("serverTemplateId");
        String containerId = req.getParameter("containerId");
        
        
        UIServicesClient uiServicesClient = getClient(serverTemplateId,                                                      
                                                      UIServicesClient.class);
        
        String caseDefId = req.getParameter("caseDefId");
        String processId = req.getParameter("processId");
        String taskId = req.getParameter("taskId");
        
        String formContent = null;
        if (caseDefId != null) {
            formContent = uiServicesClient.renderCaseForm(containerId, caseDefId, RENDERER);
        } else if (processId != null) {
            formContent = uiServicesClient.renderProcessForm(containerId, processId, RENDERER);
        } else if (taskId != null) {
            formContent = uiServicesClient.renderTaskForm(containerId, Long.valueOf(taskId), RENDERER);
        }
        
        resp.getOutputStream().write(formContent.getBytes("UTF-8"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // process and case start operations
        String serverTemplateId = req.getParameter("templateid");
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            throw new IllegalArgumentException("Missing path information");
        }
        
        String body = req.getReader().lines().collect(Collectors.joining());
        
        String containerId = extractValue(containerPattern, pathInfo);
        if (pathInfo.contains("cases")) {
            // start case operation
            String caseDefId = extractValue(caseIdPattern, pathInfo);
            CaseServicesClient caseServicesClient = getClient(serverTemplateId,                                                      
                                                              CaseServicesClient.class);
            
            String responseBody = caseServicesClient.startCase(containerId, caseDefId, new StringContentCaseFile(body));
            resp.getOutputStream().write(responseBody.getBytes("UTF-8"));
        } else {
            // start process operation
            String processId = extractValue(processIdPattern, pathInfo);
            ProcessServicesClient processServicesClient = getClient(serverTemplateId,                                                      
                                                                    ProcessServicesClient.class);
            
            Long responseBody = processServicesClient.startProcess(containerId, processId, new StringContentMap(body));
            
            resp.getOutputStream().write(responseBody.toString().getBytes("UTF-8"));
        }
        
        
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String serverTemplateId = req.getParameter("templateid");
        
        // task life cycle operations
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            throw new IllegalArgumentException("Missing path information");
        }
        String containerId = extractValue(containerPattern, pathInfo);
        String taskId = extractValue(taskIdPattern, pathInfo);
        String taskState = extractValue(taskStatePattern, pathInfo);
        
        UserTaskServicesClient taskServicesClient = getClient(serverTemplateId,                                                      
                                                            UserTaskServicesClient.class);
        
        if ("claimed".equalsIgnoreCase(taskState)) {
            taskServicesClient.claimTask(containerId, Long.valueOf(taskId), "");
        } else if ("released".equalsIgnoreCase(taskState)) {
            taskServicesClient.releaseTask(containerId, Long.valueOf(taskId), "");
        } else if ("started".equalsIgnoreCase(taskState)) {
            taskServicesClient.startTask(containerId, Long.valueOf(taskId), "");
        } else if ("stopped".equalsIgnoreCase(taskState)) {
            taskServicesClient.stopTask(containerId, Long.valueOf(taskId), "");
        } else if ("completed".equalsIgnoreCase(taskState)) {
            
            String body = req.getReader().lines().collect(Collectors.joining());
            
            taskServicesClient.completeAutoProgress(containerId, Long.valueOf(taskId), "", new StringContentMap(body));
        } else {
            
            String body = req.getReader().lines().collect(Collectors.joining());
            
            taskServicesClient.saveTaskContent(containerId, Long.valueOf(taskId), new StringContentMap(body));
        }
    }


    protected <T> T getClient(final String serverTemplateId,
                              final Class<T> clientType) {
        KieServicesClient client = getKieServicesClient(serverTemplateId);
        return client.getServicesClient(clientType);
    }


    protected KieServicesClient getKieServicesClient(final String serverTemplateId) {
        KieServicesClient client = kieServerIntegration.getServerClient(serverTemplateId);
        if (client == null) {
            throw new RuntimeException("No connection to '" + serverTemplateId + "' server(s)");
        }
        return client;
    }
    
    protected String extractValue(Pattern p, String text) {
        Matcher matcher = p.matcher(text);

        if (matcher.find()) {
            String value = matcher.group(1);
            
            return value;
            
        }
        
        return null;
    }
}
