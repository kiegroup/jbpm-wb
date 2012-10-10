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
package org.jbpm.console.ng.server.editors.jbpm.knowledge;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.client.model.ProcessInstanceSummary;
import org.jbpm.console.ng.client.model.ProcessSummary;
import org.jbpm.console.ng.client.model.StatefulKnowledgeSessionSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class KnowledgeDomainServiceEntryPointImpl implements KnowledgeDomainServiceEntryPoint{

    @Inject
    KnowledgeDomainService knowledgeService;
    
    public StatefulKnowledgeSessionSummary getSession(long sessionId) {
        return StatefulKnowledgeSessionHelper.adapt(knowledgeService.getSession(sessionId));
    }

    public StatefulKnowledgeSessionSummary getSessionSummaryByBusinessKey(String businessKey) {
        return StatefulKnowledgeSessionHelper.adapt(knowledgeService.getSessionByBusinessKey(businessKey));
    }

    public Collection<String> getSessionsNames() {
        return knowledgeService.getSessionsNames();
    }

    public int getAmountOfSessions() {
        return knowledgeService.getAmountOfSessions();
    }

    public Collection<ProcessInstanceSummary> getProcessInstances() {
        return ProcessInstanceHelper.adaptCollection(knowledgeService.getProcessInstances());
    }

    public Collection<ProcessInstanceSummary> getProcessInstancesBySessionId(String sessionId) {
        return ProcessInstanceHelper.adaptCollection(knowledgeService.getProcessInstancesBySessionId(sessionId));
    }

    public Collection<ProcessSummary> getProcessesBySessionId(String sessionId) {
        return ProcessHelper.adaptCollection(knowledgeService.getProcessesBySessionId(sessionId));
    }

    public Collection<ProcessSummary> getProcesses() {
        return ProcessHelper.adaptCollection(knowledgeService.getProcesses());
    }
    
    
    
    
    
}
