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

package org.jbpm.workbench.common.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DateTimeNAConverterTest {

    @InjectMocks
    private DateTimeNAConverter converter;

    @Test
    public void testToModelValue() {
        Date date = converter.toModelValue("28/06/2017 13:51");

        assertEquals(117,
                     date.getYear());
        assertEquals(5,
                     date.getMonth());
        assertEquals(28,
                     date.getDate());
        assertEquals(13,
                     date.getHours());
        assertEquals(51,
                     date.getMinutes());
    }

    @Test
    public void testToWidgetValue() {
        Date date = new Date();
        String dateStr = converter.toWidgetValue(date);

        final String expected = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);

        assertEquals(expected,
                     dateStr);
    }

    @Test
    public void testToWidgetValueEmpty() {
        String dateStr = converter.toWidgetValue(null);

        assertEquals(Constants.INSTANCE.NA(),
                     dateStr);
    }
}
