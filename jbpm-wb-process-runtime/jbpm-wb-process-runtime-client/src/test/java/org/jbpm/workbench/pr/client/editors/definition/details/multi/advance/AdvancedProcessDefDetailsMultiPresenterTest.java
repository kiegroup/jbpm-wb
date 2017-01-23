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

package org.jbpm.workbench.pr.client.editors.definition.details.multi.advance;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiPresenterTest;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

@RunWith(GwtMockitoTestRunner.class)
public class AdvancedProcessDefDetailsMultiPresenterTest extends BaseProcessDefDetailsMultiPresenterTest {

    @InjectMocks
    AdvancedProcessDefDetailsMultiPresenter presenter;

    @Override
    public AdvancedProcessDefDetailsMultiPresenter getPresenter() {
        return presenter;
    }

}