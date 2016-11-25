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

package org.jbpm.console.ng.workbench.forms.display.backend.provider;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.ga.forms.service.providing.FormProvider;
import org.jbpm.console.ng.ga.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.providing.TaskRenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;

@Dependent
public class KieWorkbenchFormsProvider implements FormProvider<KieWorkbenchFormRenderingSettings> {

    protected ProcessFormsValuesProcessor processInterpreter;

    protected TaskFormValuesProcessor taskInterpreter;

    @Inject
    public KieWorkbenchFormsProvider( ProcessFormsValuesProcessor processInterpreter,
                                      TaskFormValuesProcessor taskInterpreter ) {
        this.processInterpreter = processInterpreter;
        this.taskInterpreter = taskInterpreter;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public KieWorkbenchFormRenderingSettings render( ProcessRenderingSettings settings ) {
        return processInterpreter.generateRenderingContext( settings );
    }

    @Override
    public KieWorkbenchFormRenderingSettings render( TaskRenderingSettings settings ) {
        return taskInterpreter.generateRenderingContext( settings );
    }
}
