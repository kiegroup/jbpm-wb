/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.backend.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.services.api.AdHocProcessService;
import org.jbpm.services.api.ProcessService;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

@Service
@ApplicationScoped
public class KieSessionEntryPointImpl implements KieSessionEntryPoint {

    @Inject
    private ProcessService processService;

    @Inject
    private AdHocProcessService adHocProcessService;

    private CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

    @Override
    public long startProcess(String deploymentId, String processId) {
        try {
            Long processInstanceId = processService.startProcess(deploymentId, processId);

            return processInstanceId;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public long startProcess(String deploymentUnitId, String processId, Map<String, Object> params) {
        try {
            Long processInstanceId = processService.startProcess(deploymentUnitId, processId, params);

            return processInstanceId;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public long startProcess(String deploymentUnitId, String processId, String correlationKey, Map<String, Object> params) {
        try {
            CorrelationKey correlationKeyValue = null;
            if (correlationKey != null && !correlationKey.isEmpty()) {
                String[] correlations = correlationKey.split(",");

                correlationKeyValue = correlationKeyFactory.newCorrelationKey(Arrays.asList(correlations));
            }

            Long processInstanceId = processService.startProcess(deploymentUnitId, processId, correlationKeyValue, params);

            return processInstanceId;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public long startProcess(String deploymentUnitId, String processId, String correlationKey,
                             Map<String, Object> params, Long parentProcessInstanceId) {
        try {
            CorrelationKey correlationKeyValue = null;
            if (correlationKey != null && !correlationKey.isEmpty()) {
                String[] correlations = correlationKey.split(",");

                correlationKeyValue = correlationKeyFactory.newCorrelationKey(Arrays.asList(correlations));
            }

            Long processInstanceId = adHocProcessService.startProcess(deploymentUnitId, processId,
                    correlationKeyValue, params, parentProcessInstanceId);

            return processInstanceId;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void abortProcessInstance(long processInstanceId) {

        try {
            processService.abortProcessInstance(processInstanceId);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void abortProcessInstances(List<Long> processInstanceIds) {
        try {
            processService.abortProcessInstances(processInstanceIds);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void suspendProcessInstance(long processInstanceId) {
        throw new UnsupportedOperationException("Not yet supported");

    }

    @Override
    public void signalProcessInstance(long processInstanceId, String signalName, Object event) {

        try {
            processService.signalProcessInstance(processInstanceId, signalName, event);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }

    }

    @Override
    public void signalProcessInstances(List<Long> processInstanceIds, String signalName, Object event) {
        try {
            processService.signalProcessInstances(processInstanceIds, signalName, event);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }

    }

    @Override
    public Collection<String> getAvailableSignals(long processInstanceId) {
        try {
            Collection<String> signals = processService.getAvailableSignals(processInstanceId);

            return signals;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }

    }

    @Override
    public void setProcessVariable(long processInstanceId, String variableId, Object value) {
        try {
            processService.setProcessVariable(processInstanceId, variableId, value);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

}
