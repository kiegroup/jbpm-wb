package org.jbpm.console.ng.pr.backend.server.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener;
import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.EventListenerProducer;
@Process
public class IdentityProviderAwareProcessListenerProducer implements EventListenerProducer<ProcessEventListener> {

    @Override
    public List<ProcessEventListener> getEventListeners(String identifier,
                                                                        Map<String, Object> params) {
        
        List<ProcessEventListener> identityProviderAwareProcessListenerList =  new ArrayList<ProcessEventListener>();
        InitiatorProviderProcessListener initiatorProviderProcessListener = new InitiatorProviderProcessListener( (KieSession)params.get( "ksession" ) );
        identityProviderAwareProcessListenerList.add( initiatorProviderProcessListener );
        return identityProviderAwareProcessListenerList;
    }

}
