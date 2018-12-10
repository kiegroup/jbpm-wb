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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class TimerInstanceDelayConverterTest {

    @InjectMocks
    private TimerInstanceDelayConverter converter;

    @Test
    public void testDelay() {
        final String widgetValue = converter.toWidgetValue(2000l);

        assertEquals(" ( 2 Seconds )",
                     widgetValue);
    }

    @Test
    public void testEmptyDelay() {
        final String widgetValue = converter.toWidgetValue(null);

        assertEquals(Constants.INSTANCE.NA(),
                     widgetValue);
    }
}
