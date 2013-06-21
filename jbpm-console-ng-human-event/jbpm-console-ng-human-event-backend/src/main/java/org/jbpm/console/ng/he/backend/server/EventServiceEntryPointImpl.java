package org.jbpm.console.ng.he.backend.server;

import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;

@Service
@ApplicationScoped
public class EventServiceEntryPointImpl implements EventServiceEntryPoint {

    HttpSession session = RpcContext.getHttpSession();

    @Override
    public Queue<HumanEventSummary> getAllHumanEvent() {
        Queue<HumanEventSummary> colaProvisoria = null;
        if (session.getAttribute("humanEvent") == null) {
            // TODO deberia obtenerlo de session
            colaProvisoria = new LinkedList<HumanEventSummary>();
            HumanEventSummary testEvent = new HumanEventSummary("Sin eventos en el queue", 11111l, "Sin estado");
            colaProvisoria.add(testEvent);
        }else{
            colaProvisoria = (Queue<HumanEventSummary>) session.getAttribute("humanEvent");
            HumanEventSummary testEventqqq = new HumanEventSummary("Agregue otro el session andaria", 222l, "Sin estado");
            colaProvisoria.add(testEventqqq);
        }
        session.setAttribute("humanEvent", colaProvisoria);
        return (Queue<HumanEventSummary>) session.getAttribute("humanEvent");

    }

    @Override
    public Queue<HumanEventSummary> saveNewHumanEvent(HumanEventSummary pointHistory) {
        Queue<HumanEventSummary> points = null;
        if (session.getAttribute("humanEvent") == null) {
            points = new LinkedList<HumanEventSummary>();
        }else{
            points = (Queue<HumanEventSummary>) session.getAttribute("humanEvent");
        }
        points.add(pointHistory);
        session.setAttribute("humanEvent", points);
        return points;
    }

}
