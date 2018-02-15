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

package org.jbpm.workbench.common.client.menu;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RestoreDefaultFiltersMenuBuilderTest {

    @Mock
    RestoreDefaultFiltersMenuBuilder.SupportsRestoreDefaultFilters supportsRestoreDefaultFilters;

    RestoreDefaultFiltersMenuBuilder restoreDefaultFiltersMenuBuilder;

    @Mock
    Button button;

    @Mock
    HTMLDocument document;

    @Before
    public void setup() {
        button.ownerDocument = document;
        button.classList = mock(DOMTokenList.class);
        when(document.createElement("button")).thenReturn(button);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        htmlElement.classList = mock(DOMTokenList.class);
        when(document.createElement("span")).thenReturn(htmlElement);
        restoreDefaultFiltersMenuBuilder = new RestoreDefaultFiltersMenuBuilder(document,
                                                                                supportsRestoreDefaultFilters);
    }

    @Test
    public void testRestoreDefaultFilters() {
        restoreDefaultFiltersMenuBuilder.getClickHandler().execute();

        verify(supportsRestoreDefaultFilters).onRestoreDefaultFilters();
    }
}