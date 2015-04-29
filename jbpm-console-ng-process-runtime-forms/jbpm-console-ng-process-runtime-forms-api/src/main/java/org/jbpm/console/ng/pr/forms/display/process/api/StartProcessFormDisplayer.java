/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.pr.forms.display.process.api;

import java.util.Map;

import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;

/**
 *
 * @author salaboy
 */
public interface StartProcessFormDisplayer extends GenericFormDisplayer<ProcessDefinitionKey> {

    void startProcessFromDisplayer();

    void startProcess(Map<String, Object> params);

    void addResizeFormContent(FormContentResizeListener resizeListener);
}
