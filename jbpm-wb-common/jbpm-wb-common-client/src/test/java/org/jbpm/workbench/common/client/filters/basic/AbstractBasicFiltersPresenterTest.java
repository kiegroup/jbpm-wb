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

package org.jbpm.workbench.common.client.filters.basic;

import javax.enterprise.event.Event;

import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class AbstractBasicFiltersPresenterTest {

    @Mock
    BasicFiltersView view;

    @Spy
    Event<BasicFilterAddEvent> activeFilters = new EventSourceMock<>();

    @Spy
    Event<BasicFilterRemoveEvent> basicFilterRemoveEvent = new EventSourceMock<>();

    @Spy
    Event<ClearAllBasicFilterEvent> clearAllBasicFilterEvent = new EventSourceMock<>();

    public abstract BasicFiltersPresenter getPresenter();

    public BasicFiltersView getView() {
        return view;
    }

    @Before
    public void init() {
        doNothing().when(activeFilters).fire(any());
        doNothing().when(basicFilterRemoveEvent).fire(any());
        doNothing().when(clearAllBasicFilterEvent).fire(any());
    }

    @Test
    public void testOnOpen() {
        getPresenter().onOpen();
        verify(view).clearAllSelectFilter();
        verify(clearAllBasicFilterEvent).fire(any());
    }

    @Test
    public void testSearchFilterListEmpty() {
        getPresenter().addSearchFilterList("columnId",
                                           new ActiveFilterItem<>(null,
                                                                  null,
                                                                  null,
                                                                  emptyList(),
                                                                  null));

        verify(basicFilterRemoveEvent).fire(any());
    }

    @Test
    public void testSearchFilterList() {
        getPresenter().addSearchFilterList("columnId",
                                           new ActiveFilterItem<>(null,
                                                                  null,
                                                                  null,
                                                                  singletonList("value"),
                                                                  null));

        verify(activeFilters).fire(any());
    }

    public abstract void testLoadFilters();
}
