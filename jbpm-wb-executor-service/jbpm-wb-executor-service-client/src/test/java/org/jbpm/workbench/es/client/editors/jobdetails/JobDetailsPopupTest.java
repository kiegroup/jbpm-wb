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

package org.jbpm.workbench.es.client.editors.jobdetails;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;

import org.jbpm.workbench.es.model.ErrorSummary;
import org.jbpm.workbench.es.model.RequestSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class JobDetailsPopupTest {

    @GwtMock
    public HTML errorsOccurredList;

    @InjectMocks
    private JobDetailsPopup jobDetailsPopup;

    @Test
    public void errorGenerationTest() {
        RequestSummary r = new RequestSummary();
        r.setExecutions(1);
        ErrorSummary error = new ErrorSummary();
        error.setMessage("errorMessage");
        error.setStacktrace("stackTrace");
        List<ErrorSummary> errors = new ArrayList<>();
        errors.add(error);

        jobDetailsPopup.setRequest(r,
                                   errors,
                                   null);

        verify(errorsOccurredList).setHTML(SafeHtmlUtils.fromTrustedString("<strong>" + error.getMessage() + "</strong><br/>" + error.getStacktrace() + "<br><br>"));
    }
}
