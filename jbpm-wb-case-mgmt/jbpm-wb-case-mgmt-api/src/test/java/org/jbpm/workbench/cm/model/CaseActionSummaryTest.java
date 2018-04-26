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

package org.jbpm.workbench.cm.model;

import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class CaseActionSummaryTest {

    @Test
    public void testSameNameAndStatusEquals() {
        CaseActionSummary cas1 = CaseActionSummary.builder().name("test").id(1l).actionStatus(CaseActionStatus.AVAILABLE).build();
        CaseActionSummary cas2 = CaseActionSummary.builder().name("test").id(2l).actionStatus(CaseActionStatus.AVAILABLE).build();
        assertEquals(cas1,
                     cas2);
    }

    @Test
    public void testSameNameAndDifferentStatusEquals() {
        CaseActionSummary cas1 = CaseActionSummary.builder().name("test").id(1l).actionStatus(CaseActionStatus.AVAILABLE).build();
        CaseActionSummary cas2 = CaseActionSummary.builder().name("test").id(1l).actionStatus(CaseActionStatus.IN_PROGRESS).build();
        assertNotEquals(cas1,
                        cas2);
    }

    @Test
    public void testSameIdAndStatusEquals() {
        CaseActionSummary cas1 = CaseActionSummary.builder().name("test1").id(1l).actionStatus(CaseActionStatus.IN_PROGRESS).build();
        CaseActionSummary cas2 = CaseActionSummary.builder().name("test2").id(1l).actionStatus(CaseActionStatus.IN_PROGRESS).build();
        assertEquals(cas1,
                     cas2);
    }
}
