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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.bd.service.StatefulKnowledgeSessionEntryPoint;
import org.kie.runtime.KieSession;
import org.kie.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
@Transactional
public class StatefulKnowledgeSessionEntryPointImpl implements StatefulKnowledgeSessionEntryPoint{

    @Inject
    KnowledgeDomainService domainService;
    @Inject
    KnowledgeDataService dataService;

    public long startProcess(int sessionId, String processId) {
        
                
        KieSession ksession = domainService.getSessionsByName(domainService.getProcessInSessionByName(processId)).get(sessionId);
        ProcessInstance pi = ksession.startProcess(processId);
        return pi.getId();
    }
    
    public long startProcess(int sessionId, String processId, Map<String, String> params) {
        KieSession ksession = domainService.getSessionsByName(domainService.getProcessInSessionByName(processId)).get(sessionId);
        ProcessInstance pi = ksession.startProcess(processId, new HashMap<String, Object>(params));
        return pi.getId();
    }

    @Override
    public void abortProcessInstance(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        domainService.getSessionById(piDesc.getSessionId()).abortProcessInstance(processInstanceId);

    }
    
    
    @Override
    public void signalProcessInstance(long processInstanceId, String signalName, Object event) {
        
        if (processInstanceId == -1) {
            Collection<String> sessionNames = domainService.getSessionsNames();
            for (String sessionName : sessionNames) {
                Map<Integer, KieSession> sessions = domainService.getSessionsByName(sessionName);
                Iterator<KieSession> sessionsIter = sessions.values().iterator();
                while (sessionsIter.hasNext()) {
                    KieSession ksession = (KieSession) sessionsIter.next();
                    ksession.signalEvent(signalName, event);
                }

            }
        } else {
            ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
            KieSession ksession = domainService.getSessionById(piDesc.getSessionId());
            ksession.signalEvent(signalName, event, processInstanceId);
        }

    }
    
}
