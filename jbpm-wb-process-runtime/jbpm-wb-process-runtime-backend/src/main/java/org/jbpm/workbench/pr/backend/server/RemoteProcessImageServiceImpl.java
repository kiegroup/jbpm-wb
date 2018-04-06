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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ks.integration.AbstractKieServerService;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.client.UIServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class RemoteProcessImageServiceImpl extends AbstractKieServerService implements ProcessImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteProcessImageServiceImpl.class);

    @Override
    public String getProcessInstanceDiagram(String serverTemplateId,
                                            String containerId,
                                            Long processInstanceId) {
        UIServicesClient uiServicesClient = getClient(serverTemplateId,
                                                      containerId,
                                                      UIServicesClient.class);

        try {
            return removeActionsFromSVG(uiServicesClient.getProcessInstanceImage(containerId,
                                                                                 processInstanceId));
        } catch (KieServicesHttpException ex) {
            LOGGER.warn("Failed to retrieve process instance image: {}",
                        ex.getMessage());
            if (ex.getHttpCode() == 404) {
                return null;
            } else {
                throw ex;
            }
        }
    }

    @Override
    public String getProcessDiagram(String serverTemplateId,
                                    String containerId,
                                    String processId) {
        UIServicesClient uiServicesClient = getClient(serverTemplateId,
                                                      containerId,
                                                      UIServicesClient.class);

        try {
            return removeActionsFromSVG(uiServicesClient.getProcessImage(containerId,
                                                                         processId));
        } catch (KieServicesHttpException ex) {
            LOGGER.warn("Failed to retrieve process definition image: {}",
                        ex.getMessage());
            if (ex.getHttpCode() == 404) {
                return null;
            } else {
                throw ex;
            }
        }
    }

    protected String removeActionsFromSVG(final String originalHTML) {
        if (originalHTML == null) {
            return null;
        }

        return originalHTML.replaceAll("onclick=\".*?\"|onmouseover=\".*?\"",
                                       "");
    }
}