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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Uses an HTML5 input type=time control to implement the UTCTimeBox
 */
public class UTCTimeBoxImplHtml5 extends UTCTimeBoxImplShared {

    private static final DateTimeFormat timeInputFormat = DateTimeFormat.getFormat(DateUtils.DEFAULT_TIME_FORMAT_MASK);

    private InputWidget widget;

    public UTCTimeBoxImplHtml5() {
        widget = new InputWidget("time");
        setTimeFormat(timeInputFormat);

        widget.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                fireValueChangeEvent(getValue());
            }
        });

        initWidget(widget);
    }

    @Override
    public Long getValue() {
        return string2long(widget.getValue());
    }

    @Override
    public void setValue(Long value,
                         boolean fireEvents) {
        widget.setValue(long2string(value),
                        fireEvents);
    }

    @Override
    public String getText() {
        return value2text(getValue());
    }

    @Override
    public void setText(String text) {
        setValue(text2value(text),
                 true);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Long> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void fireValueChangeEvent(Long value) {
        ValueChangeEvent.fire(this,
                              value);
    }

    @Override
    public void setTabIndex(int tabIndex) {
        widget.setTabIndex(tabIndex);
    }

    // ----------------------------------------------------------------------
    // the core translation methods of this class using the form HH:mm

    // we only obey hh:mm
    private Long string2long(String value) {
        return parseUsingFormat(value,
                                timeInputFormat);
    }

    private String long2string(Long value) {
        return formatUsingFormat(value,
                                 timeInputFormat);
    }
}
 