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

package org.jbpm.workbench.pr.client.util;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class SlaStatusConverterTest {

    @Test
    public void testConvert() {
        Constants constants = Constants.INSTANCE;
        assertEquals(constants.Unknown(),
                     new SlaStatusConverter().toWidgetValue(null));
        assertEquals(constants.Unknown(),
                     new SlaStatusConverter().toWidgetValue(Integer.MAX_VALUE));
        assertEquals(constants.SlaNA(),
                     new SlaStatusConverter().toWidgetValue(ProcessInstance.SLA_NA));
        assertEquals(constants.SlaPending(),
                     new SlaStatusConverter().toWidgetValue(ProcessInstance.SLA_PENDING));
        assertEquals(constants.SlaMet(),
                     new SlaStatusConverter().toWidgetValue(ProcessInstance.SLA_MET));
        assertEquals(constants.SlaViolated(),
                     new SlaStatusConverter().toWidgetValue(ProcessInstance.SLA_VIOLATED));
        assertEquals(constants.SlaAborted(),
                     new SlaStatusConverter().toWidgetValue(ProcessInstance.SLA_ABORTED));
        assertEquals(constants.Unknown(),
                     new SlaStatusConverter().toWidgetValue(5));
    }
}
