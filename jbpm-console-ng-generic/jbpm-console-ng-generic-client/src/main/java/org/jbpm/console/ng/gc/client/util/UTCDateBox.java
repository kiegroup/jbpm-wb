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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A wrapper around a DateBox that implements HasValue<Long> where the value is
 * the number of milliseconds since January 1, 1970, 00:00:00 GMT <b>at midnight
 * on the day, month, and year selected</b>. This avoids time zone conversion
 * issues encountered using the DateBox.
 * 
 * <p>
 * Note: In keeping with the behavior of the GWT DateBox, null is used to
 * represent no value. This means that you should check for null when calling
 * getValue(), just as you would for DateBox. With auto-boxing Long/long, this
 * may seem strange but is consistent.
 */
public class UTCDateBox extends Composite implements HasValue<Long>, HasValueChangeHandlers<Long>, HasText, HasEnabled {

    private UTCDateBoxImplHtml4 impl;
    
    private static final DateTimeFormat dateInputFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
    /**
     * Creates a new UTCDateBox with the medium date format for the
     * current locale.
     */
    public UTCDateBox() {
        this(dateInputFormat);
    }
    
    /**
     * Creates a new UTCDateBox with the specified date format.
     */
    public UTCDateBox(DateTimeFormat format) {
        impl = GWT.create(UTCDateBoxImplHtml4.class);
        impl.setDateFormat(format);
        initWidget(impl.asWidget());
    }

    /**
     * Creates a new UTCDateBox
     * 
     * @deprecated Use {@link UTCDateBox#UTCDateBox(DateTimeFormat)}
     *             instead. DatePicker and DateBox.Format are now
     *             ignored.
     */
    @Deprecated
    public UTCDateBox(DatePicker picker, long date, DateBox.Format format) {
        this();
        impl.setValue(date);
    }
    
    // ----------------------------------------------------------------------
    // Interaction with the textbox
    
    @Override
    public String getText() {
        return impl.getText();
    }
    
    @Override
    public void setText(String text) {
        impl.setText(text);
    }

    // ----------------------------------------------------------------------
    // HasValue 
    
    /**
     * Returns the date value specified by the DateBox measured in number of
     * milliseconds since January 1, 1970, 00:00:00 GMT. This time will always
     * correspond to midnight in GMT on the date selected.
     * 
     * @return The time selected or null if no value is specified by the
     *         DateBox.
     */
    @Override
    public Long getValue() {
        return impl.getValue();
    }
    
    /**
     * Sets the value in the DateBox.
     * 
     * @param value
     *            A time measured in the number of milliseconds since January 1,
     *            1970, 00:00:00 GMT. This time should be at midnight in GMT for
     *            the Date selected.
     *            <p>
     *            If value is null or represents a negative number, the DateBox
     *            will have no value.
     */
    @Override
    public void setValue(Long value) {
        setValue(value, false);
    }
    
    /**
     * Sets the value in the DateBox.
     * 
     * @param value
     *            A time measured in the number of milliseconds since January 1,
     *            1970, 00:00:00 GMT. This time should be at midnight in GMT for
     *            the Date selected.
     *            <p>
     *            If value is null or represents a negative number, the DateBox
     *            will have no value.
     */
    @Override
    public void setValue(Long value, boolean fireEvents) {
        impl.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return impl.addValueChangeHandler(handler);
    }

    // ----------------------------------------------------------------------
    // HasEnabled

    @Override
    public boolean isEnabled() {
        return impl.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        impl.setEnabled(enabled);
    }

    // ----------------------------------------------------------------------
    
    /**
     * Sets the visible length of the text input for this date box.
     * This will be ignored for HTML5 inputs.
     */
    public void setVisibleLength(int length) {
        impl.setVisibleLength(length);
    }
    
    /**
     * Sets the tabindex for this control.
     */
    public void setTabIndex(int tabIndex) {
        impl.setTabIndex(tabIndex);
    }

    public DateBox getDateBox() {
        return impl.getDateBox();
    }
    
    // ----------------------------------------------------------------------
    // conversion methods that convert to dates in UTC time
    
    public static final long DAY_IN_MS = 24L*60L*60L*1000L; 
    
    public static final long trimTimeToMidnight(long time) {
        // first trim to midnight
        return time - time % DAY_IN_MS;
    }
    
    /**
     * Converts a time in UTC to a gwt Date object which is in the timezone of
     * the current browser.
     * 
     * @return The Date corresponding to the time, adjusted for the timezone of
     *         the current browser. null if the specified time is null or
     *         represents a negative number.
     */
    public static final Date utc2date(Long time) {

        // don't accept negative values
        if (time == null || time < 0) return null;
        
        // add the timezone offset
        time += timezoneOffsetMillis(new Date(time));

        return new Date(time);
    }

    /**
     * Converts a gwt Date in the timezone of the current browser to a time in
     * UTC.
     * 
     * @return A Long corresponding to the number of milliseconds since January
     *         1, 1970, 00:00:00 GMT or null if the specified Date is null.
     */
    public static final Long date2utc(Date date) {

        // use null for a null date
        if (date == null) return null;
        
        long time = date.getTime();
        
        // remove the timezone offset        
        time -= timezoneOffsetMillis(date);
        
        return time;
    }

    public static final Long getValueForToday() {
        return trimTimeToMidnight(date2utc(new Date()));
    }
    
    /**
     * Returns the timezone offset for the specified Date.
     */
    @SuppressWarnings("deprecation")
    public static final long timezoneOffsetMillis(Date date) {
        return date.getTimezoneOffset()*60*1000;        
    }

}