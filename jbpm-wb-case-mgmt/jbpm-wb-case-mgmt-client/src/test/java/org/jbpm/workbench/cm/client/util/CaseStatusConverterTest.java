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

package org.jbpm.workbench.cm.client.util;

import org.jbpm.workbench.cm.util.CaseStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class CaseStatusConverterTest {

    private CaseStatusConverter converter = new CaseStatusConverter();

    @Test
    public void testConvertToModel() {
        assertNull(converter.toModelValue(null));
        assertNull(converter.toModelValue(""));
        assertNull(converter.toModelValue("  "));
        assertEquals(CaseStatus.OPEN,
                     converter.toModelValue("OPEN"));
        assertEquals(CaseStatus.CANCELLED,
                     converter.toModelValue("CANCELLED"));
        assertEquals(CaseStatus.CLOSED,
                     converter.toModelValue("CLOSED"));
    }

    @Test
    public void testConvertToWidget() {
        assertEquals("",
                     converter.toWidgetValue(null));
        assertEquals("OPEN",
                     converter.toWidgetValue(CaseStatus.OPEN));
        assertEquals("CANCELLED",
                     converter.toWidgetValue(CaseStatus.CANCELLED));
        assertEquals("CLOSED",
                     converter.toWidgetValue(CaseStatus.CLOSED));
    }
}
