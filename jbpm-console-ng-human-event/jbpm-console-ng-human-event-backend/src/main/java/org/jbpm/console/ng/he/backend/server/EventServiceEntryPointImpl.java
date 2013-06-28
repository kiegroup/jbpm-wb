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
import org.jbpm.console.ng.he.model.HumanEventSummary;
import org.jbpm.console.ng.he.service.EventServiceEntryPoint;

@Service
@ApplicationScoped
@SuppressWarnings("unchecked")
public class EventServiceEntryPointImpl extends SessionManager implements EventServiceEntryPoint {

    private static final String KEY_EVENTS = "humanEvent";
    
    @Override
    public Queue<HumanEventSummary> getAllHumanEvent() {
         return (super.getSession().getAttribute(KEY_EVENTS) != null) ?
          (Queue<HumanEventSummary>) super.getSession().getAttribute(KEY_EVENTS) : null;
    }

    @Override
    public Queue<HumanEventSummary> saveNewHumanEvent(HumanEventSummary pointHistory) {
        Queue<HumanEventSummary> points = (super.getSession().getAttribute(KEY_EVENTS) == null) ? new LinkedList<HumanEventSummary>()
                : (Queue<HumanEventSummary>) super.getSession().getAttribute(KEY_EVENTS);
        points.add(pointHistory);
        super.getSession().setAttribute(KEY_EVENTS, points);
        return points;
    }

    @Override
    public void clearHumanEvent() {
        super.getSession().removeAttribute(KEY_EVENTS);
    }
}
