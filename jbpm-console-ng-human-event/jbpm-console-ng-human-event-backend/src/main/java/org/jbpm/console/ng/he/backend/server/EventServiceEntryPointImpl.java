/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.he.backend.server;

import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.he.model.ActionHistoryEnum;
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;

@Service
@ApplicationScoped
public class EventServiceEntryPointImpl extends SessionManager implements EventServiceEntryPoint {

    private final String KEY_SESSION = "humanEvent";

    @Override
    public Queue<HumanEventSummary> getAllHumanEvent() {
        Queue<HumanEventSummary> colaProvisoria = null;
        HumanEventSummary testEvent = null;
        if (super.getSession().getAttribute(KEY_SESSION) == null) {
            // TODO deberia obtenerlo de session
            colaProvisoria = new LinkedList<HumanEventSummary>();
            testEvent = new HumanEventSummary(ActionHistoryEnum.TEST, 11l, "use test");
            colaProvisoria.add(testEvent);
        } else {
            colaProvisoria = (Queue<HumanEventSummary>) super.getSession().getAttribute(KEY_SESSION);
            testEvent = new HumanEventSummary(ActionHistoryEnum.TEST, 12l, "use test");
            colaProvisoria.add(testEvent);
        }
        super.getSession().setAttribute(KEY_SESSION, colaProvisoria);
        return (Queue<HumanEventSummary>) super.getSession().getAttribute(KEY_SESSION);

        /*
         * return (session.getAttribute(KEY_SESSION) != null) ?
         * (Queue<HumanEventSummary>) session.getAttribute(KEY_SESSION) : null;
         */

    }

    @Override
    public Queue<HumanEventSummary> saveNewHumanEvent(HumanEventSummary pointHistory) {
        Queue<HumanEventSummary> points = (super.getSession().getAttribute(KEY_SESSION) == null) ? new LinkedList<HumanEventSummary>()
                : (Queue<HumanEventSummary>) super.getSession().getAttribute(KEY_SESSION);
        points.add(pointHistory);
        super.getSession().setAttribute(KEY_SESSION, points);
        return points;
    }
}
