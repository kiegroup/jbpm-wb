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
package org.jbpm.workbench.common.client.util;

import com.google.gwt.user.client.ui.Composite;

public abstract class UTCDateBoxImplShared extends Composite implements UTCDateBoxImpl {

    /**
     * Sets the visible length of the date input. The HTML5
     * implementation will ignore this.
     */
    @Override
    public void setVisibleLength(int length) {
    }

    /**
     * Sets the date value (as milliseconds at midnight UTC since 1/1/1970)
     */
    @Override
    public final void setValue(Long value) {
        setValue(value,
                 true);
    }
}