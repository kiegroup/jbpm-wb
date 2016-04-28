/*
 * Copyright 2010 Traction Software, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.gc.client.util;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface for UTCTimeBox implementations that are quite different
 * in appearance (HTML4 vs HTML5).
 */
public interface UTCTimeBoxImpl extends IsWidget, HasValue<Long>, HasValueChangeHandlers<Long>, HasText {

    /**
     * Sets the DateTimeFormat for this UTCTimeBox. The HTML5
     * implementation will ignore this.
     */
    void setTimeFormat(DateTimeFormat timeFormat);

    /**
     * Sets the visible length of the time input. The HTML5
     * implementation will ignore this.
     */
    void setVisibleLength(int length);

    /**
     * Validates the value that has been typed into the text input.
     * The HTML5 implementation will do nothing.
     */
    void validate();

    /**
     * Sets the tab index for the control.
     */
    void setTabIndex(int tabIndex);

}