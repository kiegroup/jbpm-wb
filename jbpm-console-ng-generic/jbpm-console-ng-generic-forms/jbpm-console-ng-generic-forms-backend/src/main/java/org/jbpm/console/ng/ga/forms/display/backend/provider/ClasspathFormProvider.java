/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ga.forms.display.backend.provider;

import java.io.InputStream;
import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.ga.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.providing.TaskRenderingSettings;

@Dependent
public class ClasspathFormProvider extends FreemakerFormProvider {
    @Override
    protected InputStream getProcessTemplateInputStream( ProcessRenderingSettings settings ) {
        return this.getClass().getResourceAsStream( "/forms/DefaultProcess.ftl" );
    }

    @Override
    protected InputStream getTaskTemplateInputStream( TaskRenderingSettings settings ) {
        return this.getClass().getResourceAsStream( "/forms/DefaultTask.ftl" );
    }

    @Override
    public int getPriority() {
        return 1000;
    }

}
