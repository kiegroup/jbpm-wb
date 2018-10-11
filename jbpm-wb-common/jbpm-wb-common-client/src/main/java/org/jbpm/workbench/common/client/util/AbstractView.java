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

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;

public abstract class AbstractView<T> implements UberElement<T> {

    protected T presenter;

    public void init(T presenter) {
        this.presenter = presenter;
    }

    protected native void tooltip(final HTMLElement e) /*-{
        $wnd.jQuery(e)
                .tooltip()
                .on("click", function () {
                    $wnd.jQuery(this).tooltip("hide");
                })

    }-*/;
}