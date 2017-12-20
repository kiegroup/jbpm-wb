/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.list;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractScreenListPresenterTest {

    @Spy
    AbstractScreenListPresenter presenter;

    @Test
    public void testServerTemplate() {
        doNothing().when(presenter).refreshGrid();

        presenter.setSelectedServerTemplate("",
                                            false);

        assertEquals("",
                     presenter.getSelectedServerTemplate());

        presenter.setSelectedServerTemplate(" ",
                                            false);

        assertEquals("",
                     presenter.getSelectedServerTemplate());

        presenter.setSelectedServerTemplate("testId",
                                            false);

        assertEquals("testId",
                     presenter.getSelectedServerTemplate());

        verify(presenter,
               times(1)).refreshGrid();

        presenter.setSelectedServerTemplate("testId",
                                            false);

        assertEquals("testId",
                     presenter.getSelectedServerTemplate());



        presenter.setSelectedServerTemplate("testId",
                                            true);

        verify(presenter,
               times(2)).refreshGrid();
    }
}
