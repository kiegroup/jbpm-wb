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
package org.jbpm.workbench.df.client.filter;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jbpm.workbench.df.client.i18n.FiltersConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class FilterEditorPopupTest {

    @Mock
    FilterSettings tableDisplayerSettings;

    @Mock
    DataSetLookup previousLookup;

    @GwtMock
    DataSetFilterEditor filterEditor;

    @Mock
    Consumer<FilterSettings> editorListener;

    @GwtMock
    private TextBox tableNameText;

    @GwtMock
    private HelpBlock tableNameHelpInline;

    @GwtMock
    private FormGroup tableNameControlGroup;

    @InjectMocks
    private FilterEditorPopup filterEditorPopup;

    @Before
    public void setupMocks() {
        filterEditorPopup.setFilterSettings(tableDisplayerSettings);
    }

    @Test
    public void testEmptyFilterName_shouldCauseValidationError() {
        when(tableNameText.getText()).thenReturn(""); // Return empty string

        boolean isValid = filterEditorPopup.validateForm();
        assertFalse("Form with an empty filter name should be rejected",
                    isValid);

        verify(tableNameControlGroup,
               times(2)).setValidationState(ValidationState.ERROR);
        verify(tableNameHelpInline).setText(FiltersConstants.INSTANCE.Name_must_be_defined());
    }

    @Test
    public void testOkEditorSelection() {
        String filterName = "filterName";

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo("test"));

        when(tableNameText.getValue()).thenReturn(filterName);
        when(tableDisplayerSettings.getDataSetLookup()).thenReturn(previousLookup);
        when(filterEditor.getFilter()).thenReturn(filter);

        filterEditorPopup.setEditorListener(editorListener);
        filterEditorPopup.ok();

        verify(filterEditor,
               times(3)).getFilter();
        verify(tableDisplayerSettings).setTableName(filterName);
        verify(previousLookup).addOperation(0,
                                            filter);
        verify(editorListener).accept(tableDisplayerSettings);
    }
}
