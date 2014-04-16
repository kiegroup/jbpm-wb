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

package org.jbpm.console.ng.mobile.core.client;

import java.util.Map;
import org.uberfire.client.mvp.UberView;

/**
 *
 * @author salaboy
 */
public interface MGWTUberView<T> extends UberView<T> {
    
    void refresh();
    
    void setParameters(Map<String,Object> params);
    
}
