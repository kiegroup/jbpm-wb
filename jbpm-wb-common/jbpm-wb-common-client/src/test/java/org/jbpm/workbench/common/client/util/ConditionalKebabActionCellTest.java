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

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.model.GenericSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ConditionalKebabActionCellTest {

    @Mock
    HTMLDocument document;

    @Mock
    ManagedInstance<KebabMenu> kebabMenus;

    @Mock
    ManagedInstance<KebabMenuItem> kebabMenuItems;

    @Mock
    ManagedInstance<Button> buttons;

    @Mock
    KebabMenu kebabMenu;

    @Mock
    Button button;

    @InjectMocks
    ConditionalKebabActionCell kebabActionCell;

    @Before
    public void setup() {
        final HTMLDivElement div = mock(HTMLDivElement.class);
        div.innerHTML = "";
        when(document.createElement("div")).thenReturn(div);
        when(kebabMenus.get()).thenReturn(kebabMenu);
        when(kebabMenu.getElement()).thenReturn(mock(HTMLElement.class));
        final KebabMenuItem kebabMenuItem = mock(KebabMenuItem.class);
        when(kebabMenuItem.getElement()).thenReturn(mock(HTMLLIElement.class));
        when(kebabMenuItems.get()).thenReturn(kebabMenuItem);
        when(button.getElement()).thenReturn(mock(HTMLElement.class));
        when(buttons.get()).thenReturn(button);
    }

    @Test
    public void testNoActions() {
        kebabActionCell.setActions(emptyList());

        final SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
        kebabActionCell.render(null,
                               null,
                               htmlBuilder);

        assertTrue(htmlBuilder.toSafeHtml().asString().isEmpty());
        verifyZeroInteractions(kebabMenus,
                               document);
    }

    @Test
    public void testNoActionsAvailable() {
        kebabActionCell.setActions(asList(new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> false,
                                                                  true),
                                          new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> false,
                                                                  false)));

        final SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
        kebabActionCell.render(null,
                               null,
                               htmlBuilder);

        assertTrue(htmlBuilder.toSafeHtml().asString().isEmpty());
        verifyZeroInteractions(kebabMenus,
                               document);
    }

    @Test
    public void testSingleAction() {
        final String label = "label";
        kebabActionCell.setActions(singletonList(new ConditionalAction<>(label,
                                                                         gs -> {
                                                                         },
                                                                         gs -> true,
                                                                         true)));

        kebabActionCell.render(null,
                               mock(GenericSummary.class),
                               new SafeHtmlBuilder());

        verify(button).setText(label);
        verify(buttons).get();
        verify(document).createElement("div");
        verifyNoMoreInteractions(document);
        verifyZeroInteractions(kebabMenus);
    }

    @Test
    public void testMultipleActions() {
        kebabActionCell.setActions(asList(new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  true),
                                          new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  false)));

        kebabActionCell.render(null,
                               mock(GenericSummary.class),
                               new SafeHtmlBuilder());

        verify(kebabMenu,
               times(2)).addKebabItem(any());
        verify(kebabMenu).addSeparator();
    }

    @Test
    public void testMultipleActionsOnly() {
        kebabActionCell.setActions(asList(new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  false),
                                          new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  false)));

        kebabActionCell.render(null,
                               mock(GenericSummary.class),
                               new SafeHtmlBuilder());

        verify(kebabMenu,
               times(2)).addKebabItem(any());
        verify(kebabMenu,
               never()).addSeparator();
    }

    @Test
    public void testMultipleActionsNavigationOnly() {
        kebabActionCell.setActions(asList(new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  true),
                                          new ConditionalAction<>(null,
                                                                  gs -> {
                                                                  },
                                                                  gs -> true,
                                                                  true)));

        kebabActionCell.render(null,
                               mock(GenericSummary.class),
                               new SafeHtmlBuilder());

        verify(kebabMenu,
               times(2)).addKebabItem(any());
        verify(kebabMenu,
               never()).addSeparator();
    }
}
