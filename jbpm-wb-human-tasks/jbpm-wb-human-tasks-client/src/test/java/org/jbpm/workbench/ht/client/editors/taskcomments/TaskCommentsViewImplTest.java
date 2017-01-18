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
package org.jbpm.workbench.ht.client.editors.taskcomments;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jbpm.workbench.ht.client.i18n.Constants;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import com.google.gwt.user.cellview.client.Column;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskCommentsViewImplTest {


    @Mock
    ListDataProvider<CommentSummary> dataProviderMock;

    @Mock
    PagedTable<CommentSummary> pagedTableMock;

    @InjectMocks
    private TaskCommentsViewImpl view;

    @Mock
    private TaskCommentsPresenter presenterMock;


    @Before
    public void setupMocks() {
        when(presenterMock.getDataProvider()).thenReturn(dataProviderMock);
    }

    @Test
    public void testDataStoreNameIsSet() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final Column column = (Column) invocationOnMock.getArguments()[0];
                assertNotNull( column.getDataStoreName() );
                return null;
            }
        } ).when(pagedTableMock).addColumn(any(Column.class), anyString());

        view.init(presenterMock);

        verify(pagedTableMock, times(4)).addColumn(any(Column.class), anyString());

        final InOrder inOrder = inOrder(pagedTableMock);
        inOrder.verify(pagedTableMock).addColumn(any(Column.class), eq(Constants.INSTANCE.Added_By()));
        inOrder.verify(pagedTableMock).addColumn(any(Column.class), eq(Constants.INSTANCE.At()));
        inOrder.verify(pagedTableMock).addColumn(any(Column.class), eq(Constants.INSTANCE.Comment()));
        inOrder.verify(pagedTableMock).addColumn(any(Column.class), eq(""));

    }


}
