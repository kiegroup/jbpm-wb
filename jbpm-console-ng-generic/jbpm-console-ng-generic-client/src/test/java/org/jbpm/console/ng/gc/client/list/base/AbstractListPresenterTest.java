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


import java.util.List;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwt.user.client.Timer;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({YesNoCancelPopup.class})
public class AbstractListPresenterTest {

    @Mock
    private Timer timer;

    @Mock
    private TestGridViewImpl viewMock;

    private TestListPresenter testListPresenter;



    @Test
    public void autoRefreshDisabledByDefaultTest() {
        testListPresenter = new TestListPresenter();

        testListPresenter.setRefreshTimer( null);
        testListPresenter.updateRefreshTimer();
        assertNotNull(testListPresenter.getRefreshTimer());
        assertFalse(testListPresenter.isAutoRefreshEnabled());


        testListPresenter.setRefreshTimer( timer);
        testListPresenter.setAutoRefreshSeconds(60);
        testListPresenter.updateRefreshTimer();
        assertFalse(testListPresenter.isAutoRefreshEnabled());
        verify(timer).cancel();

    }

    @Test
    public void autoRefreshEnabledScheduleTimerTest() {
        testListPresenter = new TestListPresenter();

        testListPresenter.setAutoRefreshEnabled(true);
        testListPresenter.setAutoRefreshSeconds(60);
        testListPresenter.setRefreshTimer( timer);
        testListPresenter.updateRefreshTimer();
        assertNotNull(testListPresenter.getRefreshTimer());
        verify(timer).cancel();
        verify(timer).schedule(60000);

    }


    @Test
    public void restoreTabsTest() {
        testListPresenter = new TestListPresenter(viewMock);

        testListPresenter.restoredTabCallTest();
        verify(viewMock).showRestoreDefaultFilterConfirmationPopup();
    }


    private class TestListPresenter extends AbstractScreenListPresenter{

        TestGridViewImpl view;

        public TestListPresenter(){

        }
        public TestListPresenter(TestGridViewImpl view){
            this.view= view;
        }


        @Override protected AbstractListView.ListView getListView() {
            return view;
        }

        @Override public void getData(Range visibleRange) {

        }

        public void restoredTabCallTest(){
            getListView().showRestoreDefaultFilterConfirmationPopup();
        }

        protected int getRefreshValue(){
            return 10;
        }
    }

    public class TestGridViewImpl extends AbstractMultiGridView implements AbstractListView.ListView {

        @Override public void initColumns(ExtendedPagedTable extendedPagedTable) {

        }

        @Override public void initSelectionModel() {

        }

        @Override public void init(Object presenter) {

        }

        @Override public void showRestoreDefaultFilterConfirmationPopup() {
            restoreTabs();
        }

        public void restoreTabs() {
            super.restoreTabs();
        }
    }

}

