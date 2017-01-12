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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;

/**
 * Uses an HTML5 input type=date control to implement the UTCDateBox
 * 
 */
public class UTCDateBoxImplHtml5 extends UTCDateBoxImplShared {

    private static final DateTimeFormat dateInputFormat = DateTimeFormat.getFormat(DateUtils.getDateFormatMask());
    
    private DateTimeFormat dateFormat;
    private InputWidget widget;
    
    public UTCDateBoxImplHtml5() {
        widget = new InputWidget("date");
        setDateFormat(dateInputFormat);
        
        widget.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireValueChangeEvent(getValue());
            }
            
        });
        
        initWidget(widget);        
    }

    /**
     * Sets the DateTimeFormat for this UTCDateBox. The HTML5
     * implementation will ignore this.
     */
    @Override
    public void setDateFormat(DateTimeFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        widget.setTabIndex(tabIndex);
    }

    // ----------------------------------------------------------------------
    
    @Override
    public Long getValue() {
        return string2long(widget.getValue(), dateInputFormat); 
    }

    @Override
    public void setValue(Long value, boolean fireEvents) {
        String dateInputValue = long2string(value, dateInputFormat);
        widget.setValue(dateInputValue, fireEvents);
    }

    // ----------------------------------------------------------------------
    
    @Override
    public String getText() {
        return long2string(getValue(), dateFormat != null ? dateFormat : dateInputFormat);
    }

    @Override
    public void setText(String text) {
        // attempt to parse using dateInputFormat or dateFormat
        Long value = null;
        if (dateFormat != null) {
            value = string2long(text, dateFormat);
        }       
        if (value == null) {
            value = string2long(text, dateInputFormat);
        }
        setValue(value, true);
    }

    // ----------------------------------------------------------------------
    
    @Override
    public boolean isEnabled() {
        return DomUtils.isEnabled(widget.getElement());
    }

    @Override
    public void setEnabled(boolean enabled) {
        DomUtils.setEnabled(widget.getElement(), enabled);
    }    
    
    // ----------------------------------------------------------------------
    
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    protected void fireValueChangeEvent(Long value) {
        ValueChangeEvent.fire(this, value);
    }

    // ----------------------------------------------------------------------
    
    /**
     * Parses the supplied text and converts it to a Long
     * corresponding to that midnight in UTC on the specified date.
     * 
     * @return null if it fails to parsing using the specified
     *         DateTimeFormat
     */
    private Long string2long(String text, DateTimeFormat fmt) {
        
        // null or "" returns null
        if (text == null) return null;
        text = text.trim();
        if (text.length() == 0) return null;
        
        Date date = fmt.parse(text);
        return date != null ? UTCDateBox.date2utc(date) : null;
    }

    /**
     * Formats the supplied value using the specified DateTimeFormat.
     * 
     * @return "" if the value is null
     */
    private String long2string(Long value, DateTimeFormat fmt) {
        // for html5 inputs, use "" for no value
        if (value == null) return "";
        Date date = UTCDateBox.utc2date(value);
        return date != null ? fmt.format(date) : null;
    }

    @Override
    public DatePicker getDateBox() {
        return null;
    }
}