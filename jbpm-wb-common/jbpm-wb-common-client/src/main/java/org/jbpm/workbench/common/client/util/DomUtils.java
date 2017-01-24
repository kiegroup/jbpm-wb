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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Created for a uniform implementation of HasEnabled, but will be
 * useful in the future for other DOM related things.
 * 
 * <p>This is a subclass of UIObject to gain access to the useful
 * setStyleName method of UIObject which is implemented as protected.
 * 
 */
public class DomUtils extends UIObject {
    
    /**
     * This object is never created.
     */
    private DomUtils() {}
    
    /**
     * It's enough to just set the disabled attribute on the
     * element, but we want to also add a "disabled" class so that we can
     * style it.
     *
     * At some point we'll just be able to use .button:disabled, 
     * but that doesn't work in IE8-
     */
    public static void setEnabled(Element element, boolean enabled) {
        element.setPropertyBoolean("disabled", !enabled);
        setStyleName(element, "disabled", !enabled);
    }

    /**
     * Returns true if the element has the disabled attribute.
     */
    public static boolean isEnabled(Element element) {
        return element.getPropertyBoolean("disabled");        
    }
    
}