/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.list.base;


import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwt.user.client.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith( GwtMockitoTestRunner.class )
public class AbstractListPresenterTest {

    @Mock
    private Timer timer;

    private TestListPresenter testListPresenter;


    @Test
    public void autoRefreshDisabledByDefaultTest() {
        testListPresenter = new TestListPresenter();

        testListPresenter.setRefreshTimer( null);
        testListPresenter.updateRefreshTimer();
        assertNotNull(testListPresenter.getRefreshTimer());
        assertFalse(testListPresenter.isAutoRefreshEnabled());


        testListPresenter.setRefreshTimer( timer );
        testListPresenter.setAutoRefreshSeconds( 60 );
        testListPresenter.updateRefreshTimer();
        assertFalse(testListPresenter.isAutoRefreshEnabled());
        verify(timer).cancel();

    }

    @Test
    public void autoRefreshEnabledScheduleTimerTest() {
        testListPresenter = new TestListPresenter();

        testListPresenter.setAutoRefreshEnabled( true );
        testListPresenter.setAutoRefreshSeconds( 60 );
        testListPresenter.setRefreshTimer( timer);
        testListPresenter.updateRefreshTimer();
        assertNotNull(testListPresenter.getRefreshTimer());
        verify(timer).cancel();
        verify(timer).schedule(60000);

    }

    private class TestListPresenter extends AbstractScreenListPresenter{

        @Override protected AbstractListView.ListView getListView() {
            return null;
        }

        @Override public void getData(Range visibleRange) {

        }

        protected int getRefreshValue(){
            return 10;
        }
    }

}

