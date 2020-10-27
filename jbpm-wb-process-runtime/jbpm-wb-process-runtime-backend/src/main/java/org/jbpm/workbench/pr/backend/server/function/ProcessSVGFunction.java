/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server.function;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.external.impl.BackendComponentFunction;
import org.jbpm.workbench.pr.service.ProcessImageService;

@Dependent
public class ProcessSVGFunction implements BackendComponentFunction {

    private final String CONTAINERID_PARAM = "containerId";
    private final static String PROCESSID_PARAM = "processId";
    private final static String SERVER_TEMPLATE_PARAM = "serverTemplate";

    @Inject
    ProcessImageService remoteProccessImageService;

    @Override
    public Object exec(Map<String, Object> params) {
        String containerId = getRequiredParam(CONTAINERID_PARAM, params);
        String processId = getRequiredParam(PROCESSID_PARAM, params);
        String serverTemplateId = getRequiredParam(SERVER_TEMPLATE_PARAM, params);

        String processDiagram = remoteProccessImageService.getProcessDiagram(serverTemplateId, containerId, processId);

        if (processDiagram == null) {
            throw new RuntimeException("Process SVG not found.");
        }

        return processDiagram;
    }

    private String getRequiredParam(String param, Map<String, Object> params) {
        Object value = params.get(param);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new RuntimeException("Param '" + param + "' is required.");
        }
        return value.toString();
    }

}