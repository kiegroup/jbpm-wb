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
package org.droolsjbpm.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.SessionLocator;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class KnowledgeDataServiceImpl implements KnowledgeDataService {

    Map<String, SessionLocator> ksessionLocators = new HashMap<String, SessionLocator>();
    @Inject
    private EntityManager em;

    @PostConstruct
    public void init() {
    }

    public Collection<ProcessInstanceDesc> getProcessInstances() { 
        List<ProcessInstanceDesc> processInstances = em.createQuery("select pi FROM ProcessInstanceDesc pi where pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id ) ").getResultList();

        return processInstances;
    }

    public Collection<ProcessInstanceDesc> getProcessInstancesBySessionId(String sessionId) {
        List<ProcessInstanceDesc> processInstances = em.createQuery("select pi FROM ProcessInstanceDesc pi where pi.sessionId=:sessionId, pi.pk = (select max(pid.pk) FROM ProcessInstanceDesc pid WHERE pid.id = pi.id )")
                .setParameter("sessionId", sessionId).getResultList();

        return processInstances;
    }

    public Collection<ProcessDesc> getProcessesBySessionId(String sessionId) {
        List<ProcessDesc> processes = em.createQuery("select pd from ProcessDesc pd where pd.sessionId=:sessionId GROUP BY pd.id ORDER BY pd.dataTimeStamp DESC")
                .setParameter("sessionId", sessionId).getResultList();
        return processes;
    }

    public Collection<ProcessDesc> getProcesses() {
        List<ProcessDesc> processes = em.createQuery("select pd from ProcessDesc pd GROUP BY pd.id ORDER BY pd.dataTimeStamp DESC").getResultList();
        return processes;
    }

    public ProcessInstanceDesc getProcessInstanceById(int sessionId, long processId) {
         List<ProcessInstanceDesc> processInstances = em.createQuery("select pid from ProcessInstanceDesc pid where pid.id=:processId AND pid.sessionId=:sessionId ORDER BY pid.dataTimeStamp DESC")
                .setParameter("processId", processId)
                .setParameter("sessionId", sessionId)
                .setMaxResults(1).getResultList();

        return processInstances.get(0);
    }

    public Collection<NodeInstanceDesc> getProcessInstanceHistory(int sessionId, long processId) {
        List<NodeInstanceDesc> nodeInstances = em.createQuery("select nid from NodeInstanceDesc nid where nid.processInstanceId=:processId AND nid.sessionId=:sessionId ORDER BY nid.dataTimeStamp DESC")
                .setParameter("processId", processId)
                .setParameter("sessionId", sessionId)
                .getResultList();

        return nodeInstances;
    }

    public Collection<VariableStateDesc> getVariableHistory(long processInstanceId, long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
