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

package org.jbpm.workbench.pr.client.editors.instance.details;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Anchor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsTabViewImplTest {

    @Mock
    private Anchor parentAnchor;

    @InjectMocks
    private ProcessInstanceDetailsTabViewImpl processInstanceDetailsTabView;

    @Test
    public void setParentProcessInstanceIdTextTest() {
        processInstanceDetailsTabView.setParentProcessInstanceIdText("No Parent Process Instance", false);
        verify(parentAnchor).setEnabled(false);
        verify(parentAnchor).setText("No Parent Process Instance");

        processInstanceDetailsTabView.setParentProcessInstanceIdText("1", true);
        verify(parentAnchor).setEnabled(true);
        verify(parentAnchor).setText("1");
    }
}
