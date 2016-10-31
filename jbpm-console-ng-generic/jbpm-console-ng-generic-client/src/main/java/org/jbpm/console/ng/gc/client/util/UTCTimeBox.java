/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.gc.client.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

public class UTCTimeBox extends Composite implements HasValue<Long>, HasValueChangeHandlers<Long>, HasText, HasEnabled {

    public UTCTimeBoxImplHtml4 impl;
    
    /**
     * By default the predefined SHORT time format will be used.
     */
    public UTCTimeBox() {
        this(DateTimeFormat.getFormat(PredefinedFormat.TIME_SHORT));
    }
    
    /**
     * Allows a UTCTimeBox to be created with a specified format.
     */
    public UTCTimeBox(DateTimeFormat timeFormat) {
        // used deferred binding for the implementation
        impl = GWT.create(UTCTimeBoxImplHtml4.class);
        impl.setTimeFormat(timeFormat);
        initWidget(impl.asWidget());
    }
    
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return impl.addValueChangeHandler(handler);
    }

    @Override
    public Long getValue() {
        return impl.getValue();
    }

    @Override
    public void setValue(Long value) {
        impl.setValue(value);
    }

    @Override
    public void setValue(Long value, boolean fireEvents) {
        impl.setValue(value, fireEvents);
    }

    @Override
    public String getText() {
        return impl.getText();
    }

    @Override
    public void setText(String text) {
        impl.setText(text);
    }

    @Override
    public boolean isEnabled() {
        return DomUtils.isEnabled(getElement());
    }

    @Override
    public void setEnabled(boolean enabled) {
        DomUtils.setEnabled(getElement(), enabled);
    }    
    
    /**
     * The HTML5 implementation will ignore this.
     */
    public void setVisibleLength(int length) {
        impl.setVisibleLength(length);
    }

    public void setTabIndex(int tabIndex) {
        impl.setTabIndex(tabIndex);
    }
    
    /**
     * If this is a text based control, it will validate the value
     * that has been typed.
     */
    public void validate() {
        impl.validate();
    }
    
    // ----------------------------------------------------------------------
    // utils

    public static final Long getValueForNextHour() {
        Date date = new Date();
        long value = UTCDateBox.date2utc(date);

        // remove anything after an hour and add an hour
        long hour = 60 * 60 * 1000;
        value = value % UTCDateBox.DAY_IN_MS;
        return value - (value % hour) + hour;
    }

}