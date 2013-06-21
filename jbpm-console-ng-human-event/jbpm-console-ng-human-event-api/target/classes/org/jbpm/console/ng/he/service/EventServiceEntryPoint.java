package org.jbpm.console.ng.he.service;

import java.util.Queue;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.he.model.HumanEventSummary;

@Remote
public interface EventServiceEntryPoint {
	
	Queue<HumanEventSummary> getAllHumanEvent();
	
	Queue<HumanEventSummary> saveNewHumanEvent(HumanEventSummary pointHistory);

}
