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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.droolsjbpm.services.api.DomainManagerService;

import org.droolsjbpm.services.api.RuntimeDataService;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.backend.server.ProcessInstanceHelper;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.manager.Runtime;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
@Transactional
public class KieSessionEntryPointImpl implements KieSessionEntryPoint {

    @Inject
    private DomainManagerService domainManagerService;
    @Inject
    private RuntimeDataService dataService;

    public long startProcess(String domainName, String processId) {
        List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(domainName);
        // I'm considering Singleton
        KieSession ksession = runtimesByDomain.get(0).getKieSession();
        ProcessInstance pi = ksession.startProcess(processId);
        return pi.getId();
    }

    public long startProcess(String domainName, String processId, Map<String, String> params) {
        List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(domainName);
        // I'm considering Singleton
        KieSession ksession = runtimesByDomain.get(0).getKieSession();
        ProcessInstance pi = ksession.startProcess(processId, new HashMap<String, Object>(params));
        return pi.getId();
    }

    @Override
    public void abortProcessInstance(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(piDesc.getDomainId());
        // I'm considering Singleton
        KieSession ksession = runtimesByDomain.get(0).getKieSession();
        ksession.abortProcessInstance(processInstanceId);

    }

    @Override
    public void signalProcessInstance(long processInstanceId, String signalName, Object event) {

        if (processInstanceId == -1) {
//            Collection<String> sessionNames = domainService.getSessionsNames();
//            for (String sessionName : sessionNames) {
//                Map<Integer, KieSession> sessions = domainService.getSessionsByName(sessionName);
//                Iterator<KieSession> sessionsIter = sessions.values().iterator();
//                while (sessionsIter.hasNext()) {
//                    KieSession ksession = (KieSession) sessionsIter.next();
//                    ksession.signalEvent(signalName, event);
//                }
//
//            }
        } else {
            ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
            List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(piDesc.getDomainId());
            KieSession ksession = runtimesByDomain.get(0).getKieSession();
            ksession.signalEvent(signalName, event, processInstanceId);
        }

    }

    public Collection<String> getAvailableSignals(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(piDesc.getDomainId());
        KieSession ksession = runtimesByDomain.get(0).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        Collection<String> activeSignals = new ArrayList<String>();

        if (processInstance != null) {
            ((ProcessInstanceImpl) processInstance).setProcess(ksession.getKieBase().getProcess(processInstance.getProcessId()));
            Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance) processInstance).getNodeInstances();

            activeSignals.addAll(ProcessInstanceHelper.collectActiveSignals(activeNodes));
        }

        return activeSignals;
    }

    public void setProcessVariable(long processInstanceId, String variableId, Object value) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        List<Runtime> runtimesByDomain = domainManagerService.getRuntimesByDomain(piDesc.getDomainId());
        KieSession ksession = runtimesByDomain.get(0).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);

        ((WorkflowProcessInstance) processInstance).setVariable(variableId, value);

    }
}
