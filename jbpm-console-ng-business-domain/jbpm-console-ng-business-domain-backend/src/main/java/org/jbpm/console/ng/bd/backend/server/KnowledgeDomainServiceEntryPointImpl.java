///*
// * Copyright 2012 JBoss by Red Hat.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.console.ng.bd.backend.server;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
//import org.droolsjbpm.services.api.RuntimeDataService;
//import org.droolsjbpm.services.api.KnowledgeDomainService;
//import org.droolsjbpm.services.api.RulesNotificationService;
//import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
//import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
//import org.jboss.errai.bus.server.annotations.Service;
//import org.jboss.seam.transaction.Transactional;
//import org.jbpm.console.ng.bd.model.RuleNotificationSummary;
//import org.jbpm.console.ng.bd.model.KieSessionSummary;
//import org.jbpm.console.ng.bd.service.KnowledgeDomainServiceEntryPoint;
//import org.jbpm.console.ng.ht.backend.server.TaskDefHelper;
//import org.jbpm.console.ng.ht.model.TaskDefSummary;
//import org.jbpm.console.ng.pr.backend.server.NodeInstanceHelper;
//import org.jbpm.console.ng.pr.backend.server.ProcessHelper;
//import org.jbpm.console.ng.pr.backend.server.ProcessInstanceHelper;
//import org.jbpm.console.ng.pr.backend.server.VariableHelper;
//import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
//import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
//import org.jbpm.console.ng.pr.model.ProcessSummary;
//import org.jbpm.console.ng.pr.model.VariableSummary;
//import org.jbpm.shared.services.api.FileException;
//import org.jbpm.shared.services.api.FileService;
//
//
//import org.jbpm.process.instance.impl.ProcessInstanceImpl;
//import org.jbpm.workflow.instance.WorkflowProcessInstance;
//import org.kie.commons.java.nio.file.Path;
//import org.kie.api.runtime.KieSession;
//import org.kie.api.runtime.process.NodeInstance;
//import org.kie.api.runtime.process.ProcessInstance;
//import org.uberfire.backend.vfs.ActiveFileSystems;
//import org.uberfire.backend.vfs.PathFactory;
//
///**
// *
// * @author salaboy
// */
//@Service
//@ApplicationScoped
//@Transactional
//public class KnowledgeDomainServiceEntryPointImpl implements KnowledgeDomainServiceEntryPoint {
//
//    
////    @Inject
////    RulesNotificationService rulesNotificationService;
////    
////    @Inject
////    FileService fs;
////    @Inject
////    ActiveFileSystems fileSystems;
////
////    public KnowledgeDomainServiceEntryPointImpl() {
////    }
////
////
////
//////    @Override
//////    public Collection<String> getAvailableSignals(String businessKey, long processInstanceId) {
//////        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
//////        KieSession ksession = domainService.getSessionById(piDesc.getSessionId());
//////        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
//////        Collection<String> activeSignals = new ArrayList<String>();
//////
//////        if (processInstance != null) {
//////            ((ProcessInstanceImpl) processInstance).setProcess(ksession.getKieBase().getProcess(processInstance.getProcessId()));
//////            Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance) processInstance).getNodeInstances();
//////
//////            activeSignals.addAll(ProcessInstanceHelper.collectActiveSignals(activeNodes));
//////        }
//////
//////        return activeSignals;
//////    }
////
//////    @Override
//////    public void setProcessVariable(long processInstanceId, String variableId, Object value) {
//////        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
//////        KieSession ksession = domainService.getSessionById(piDesc.getSessionId());
//////        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
//////
//////        ((WorkflowProcessInstance) processInstance).setVariable(variableId, value);
//////
//////    }
////
////    
////
////   
////
////    public void checkFileSystem() {
////        fs.fetchChanges();
////    }
////
////    public String createProcessDefinitionFile(String name) {
////      return fs.createFile(name).toString();
////    }
////    
////    public void fetchChanges() {
////        fs.fetchChanges();
////    }
////
////    public byte[] loadFile(Path file) {
////        try {
////            return fs.loadFile(file);
////        } catch (FileException ex) {
////            Logger.getLogger(KnowledgeDomainServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return null;
////    }
////
////    public Iterable<Path> loadFilesByType(String path, String fileType) {
////        try {
////            return fs.loadFilesByType(path, fileType);
////        } catch (FileException ex) {
////            Logger.getLogger(KnowledgeDomainServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return null;
////    }
////
////   
////
////   
////    public void insertNotification(int sessionId, String notification) {
////        rulesNotificationService.insertNotification(sessionId, notification);
////    }
////
////    public Collection<RuleNotificationSummary> getAllNotificationInstance() {
////        return RuleNotificationHelper.adaptCollection(rulesNotificationService.getAllNotificationInstance());
////    }
////
////    public Collection<RuleNotificationSummary> getAllNotificationInstanceBySessionId(int sessionId) {
////        return RuleNotificationHelper.adaptCollection(rulesNotificationService.getAllNotificationInstanceBySessionId(sessionId));
////    }
////
////    @Override
////    public org.uberfire.backend.vfs.Path getProcessAssetPath(String processId) {
////        String reporoot = fs.getRepositoryRoot();
////        if (reporoot.endsWith("/")) {
////            reporoot = reporoot.substring(0, (reporoot.length() - 1));
////        }
////        String uri = reporoot + domainService.getProcessAssetPath(processId);
////        String name = uri.substring(uri.lastIndexOf("/") + 1);
////        return PathFactory.newPath(fileSystems.getBootstrapFileSystem(), name, uri);
////        
////    }
//
//    
//    
//}
