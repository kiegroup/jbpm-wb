/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.pr.backend.server;

import java.util.Date;

import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.junit.Test;
import org.kie.server.api.model.admin.TimerInstance;

import static org.junit.Assert.*;

public class TimerInstanceSummaryMapperTest {

    public static void assertTimerInstanceSummary(final TimerInstance ti,
                                                  final TimerInstanceSummary ts) {
        assertNotNull(ts);

        assertEquals(ti.getTimerId(),
                     ts.getId().longValue());
        assertEquals(ti.getTimerName(),
                     ts.getName());
        assertEquals(ti.getActivationTime(),
                     ts.getActivationTime());
        assertEquals(ti.getLastFireTime(),
                     ts.getLastFireTime());
        assertEquals(ti.getNextFireTime(),
                     ts.getNextFireTime());
        assertEquals(ti.getDelay(),
                     ts.getDelay().longValue());
        assertEquals(ti.getPeriod(),
                     ts.getPeriod().longValue());
        assertEquals(ti.getRepeatLimit(),
                     ts.getRepeatLimit().intValue());
        assertEquals(ti.getProcessInstanceId(),
                     ts.getProcessInstanceId().longValue());
    }

    @Test
    public void testTimerInstanceSummary() {
        TimerInstance ti = TimerInstance.builder()
                .timerId(1l)
                .timerName("timer")
                .activationTime(new Date())
                .delay(2l)
                .nextFireTime(new Date())
                .lastFireTime(new Date())
                .repeatLimit(10)
                .period(3l)
                .processInstanceId(4l)
                .build();

        assertTimerInstanceSummary(ti,
                                   new TimerInstanceSummaryMapper().apply(ti));
    }

    @Test
    public void testTimerInstanceSummaryNull() {
        assertNull(new TimerInstanceSummaryMapper().apply(null));
    }
}
