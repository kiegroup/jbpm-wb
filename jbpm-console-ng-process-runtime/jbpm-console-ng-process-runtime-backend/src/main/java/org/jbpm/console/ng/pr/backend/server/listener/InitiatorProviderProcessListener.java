package org.jbpm.console.ng.pr.backend.server.listener;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

public class InitiatorProviderProcessListener implements ProcessEventListener {
    
    private KieSession ksession;
    private IdentityProvider identityProvider;
    
    public InitiatorProviderProcessListener(KieSession ksession) {
        this.ksession = ksession;
    }
    
    private void resolveIdentityProvider() {
        if (identityProvider != null) {
            return;
        }
        Object identityProvider = ksession.getEnvironment().get("IdentityProvider");
        Environment env = ksession.getEnvironment();
        if (identityProvider instanceof IdentityProvider) {
            this.identityProvider = (IdentityProvider) identityProvider;
        }
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        resolveIdentityProvider();
        if (identityProvider != null) {
            WorkflowProcessInstance wpi = (WorkflowProcessInstance)event.getProcessInstance();
            wpi.setVariable( "initiator", identityProvider.getName() );
        }
        
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub
        
    }
}
