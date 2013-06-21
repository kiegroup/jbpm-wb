package org.jbpm.console.ng.he.backend.server;

import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;

@Service
@ApplicationScoped
@Transactional
public class EventServiceEntryPointImpl implements EventServiceEntryPoint {

	@Override
	public Queue<HumanEventSummary> getAllHumanEvent() {
		//TODO deberia obtenerlo de session
		Queue<HumanEventSummary> colaProvisoria = new LinkedList<HumanEventSummary>();
		HumanEventSummary testEvent = new HumanEventSummary("test TODo event", 22332l);
		colaProvisoria.add(testEvent);
		return colaProvisoria;
	}

}
