package org.jbpm.console.ng.pr.backend.server.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.EventListenerProducer;
import org.jbpm.runtime.manager.api.qualifiers.Process;
@Process
public class IdentityProviderAwareProcessListenerProducer implements EventListenerProducer<IdentityProviderAwareProcessListener> {

    @Override
    public List<IdentityProviderAwareProcessListener> getEventListeners(String identifier,
                                                                        Map<String, Object> params) {
        List<IdentityProviderAwareProcessListener> identityProviderAwareProcessListenerList =  new ArrayList<IdentityProviderAwareProcessListener>();
        IdentityProviderAwareProcessListener identityProviderAwareProcessListener = new IdentityProviderAwareProcessListener( (KieSession)params.get( "ksession" ) );
        identityProviderAwareProcessListenerList.add( identityProviderAwareProcessListener );
        return identityProviderAwareProcessListenerList;
    }

}
