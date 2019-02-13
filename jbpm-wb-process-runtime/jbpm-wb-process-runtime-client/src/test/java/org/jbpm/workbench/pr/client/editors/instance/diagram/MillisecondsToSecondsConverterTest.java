/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class MillisecondsToSecondsConverterTest {

    private MillisecondsToSecondsConverter converter = new MillisecondsToSecondsConverter();

    @Test
    public void testToModelValue() {
        assertEquals(0, converter.toModelValue(null).longValue());
        assertEquals(0, converter.toModelValue("").longValue());
        assertEquals(0, converter.toModelValue("   ").longValue());
        assertEquals(1, converter.toModelValue("1").longValue());
        assertEquals(0, converter.toModelValue("abc").longValue());
    }

    @Test
    public void testToWidgetValue() {
        assertEquals("", converter.toWidgetValue(null));
        assertEquals("0", converter.toWidgetValue(1l));
        assertEquals("1", converter.toWidgetValue(1000l));
    }
}
