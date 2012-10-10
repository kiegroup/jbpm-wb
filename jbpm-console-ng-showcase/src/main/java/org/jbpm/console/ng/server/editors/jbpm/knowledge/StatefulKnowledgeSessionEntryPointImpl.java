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
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.shared.StatefulKnowledgeSessionEntryPoint;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class StatefulKnowledgeSessionEntryPointImpl implements StatefulKnowledgeSessionEntryPoint{

    @Inject
    KnowledgeDomainService knowledgeService;

    public long startProcess(String processId) {
        StatefulKnowledgeSession ksession = knowledgeService.getSessionByBusinessKey("default");
        ProcessInstance pi = ksession.startProcess(processId);
        return pi.getId();
    }

   
    
}
