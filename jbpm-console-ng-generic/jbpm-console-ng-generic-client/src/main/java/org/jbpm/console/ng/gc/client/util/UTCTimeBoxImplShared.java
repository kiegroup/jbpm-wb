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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author andy
 *
 */
public abstract class UTCTimeBoxImplShared extends Composite implements UTCTimeBoxImpl {

    protected static DateTimeFormat[] fallbackTimeFormats = null;
    protected static String[] fallbackFormatStrings = new String[] { "hh:mma", "HH:mm" };

    protected DateTimeFormat timeFormat;

    // ----------------------------------------------------------------------
    
    /**
     * Sets the DateTimeFormat for this UTCTimeBox. The HTML5
     * implementation will ignore this.
     */
    @Override
    public void setTimeFormat(DateTimeFormat timeFormat) { 
        this.timeFormat = timeFormat;
    }
    
    /**
     * Sets the visible length of the time input. The HTML5
     * implementation will ignore this.
     */
    @Override
    public void setVisibleLength(int length) {}    

    /**
     * Validates the value that has been typed into the text input.
     * The HTML5 implementation will do nothing.
     */
    @Override
    public void validate() {}
    

    /**
     * Sets the time value (as milliseconds since midnight independent
     * of time zone)
     */
    @Override
    public final void setValue(Long value) {
        setValue(value, false);
    }    
    
    // ----------------------------------------------------------------------
    
    public UTCTimeBoxImplShared() {
        if (fallbackTimeFormats == null) {
            fallbackTimeFormats = new DateTimeFormat[fallbackFormatStrings.length];
            for (int i=0; i<fallbackFormatStrings.length; i++) {
                fallbackTimeFormats[i] = DateTimeFormat.getFormat(fallbackFormatStrings[i]);
            }
        }    
    }
    
    // ----------------------------------------------------------------------
    // parsing and formatting

    protected final String value2text( Long value ) {
        return formatUsingFormat(value, timeFormat);
    }
    
    protected final Long text2value( String text ) {

        if (text == null ){
            return null;
        }
        text = text.trim();
        if (text.length() == 0) {
            return null;
        }
        else {
            // normalize p->pm, a->am
            if (text.endsWith("p") || text.endsWith("a")) {
                text += "m";
            }
            
            Long ret = parseUsingFallbacks(text, timeFormat);
            if (ret == null) {
                ret = parseUsingFallbacksWithColon(text, timeFormat);
            }
            return ret;
        }
    }
    
    /**
     * Formats the value provided with the specified DateTimeFormat
     */
    protected static final String formatUsingFormat(Long value, DateTimeFormat fmt) {
        if (value == null) {
            return "";
        }
        else {
            // midnight GMT
            Date date = new Date(0);
            // offset by timezone and value
            date.setTime(UTCDateBox.timezoneOffsetMillis(date) + value.longValue());
            // format it
            return fmt.format(date);
        }
    }
    
    /**
     * Attempts to insert a colon so that a value without a colon can
     * be parsed.
     */
    protected static final Long parseUsingFallbacksWithColon(String text, DateTimeFormat timeFormat) {
        if (text.indexOf(':') == -1) {
            text = text.replace(" ", "");
            int numdigits = 0;
            int lastdigit = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isDigit(c)) {
                    numdigits++;
                    lastdigit = i;
                }
            }
            if (numdigits == 1 || numdigits == 2) {
                // insert :00
                int colon = lastdigit + 1;
                text = text.substring(0, colon) + ":00" + text.substring(colon);
            }
            else if (numdigits > 2) {
                // insert :
                int colon = lastdigit - 1;
                text = text.substring(0, colon) + ":" + text.substring(colon);
            }
            return parseUsingFallbacks(text, timeFormat);
        }
        else {
            return null;
        }
    }
    
    protected static final Long parseUsingFormat(String text, DateTimeFormat fmt) {
        Date date = new Date(0);
        int num = fmt.parse(text, 0, date);
        return (num != 0) ? new Long(normalizeInLocalRange(date.getTime() - UTCDateBox.timezoneOffsetMillis(date))) : null;
    }
    
    protected static final Long parseUsingFallbacks(String text, DateTimeFormat primaryTimeFormat) {
        Long ret = parseUsingFormat(text, primaryTimeFormat);
        for (int i = 0; ret == null && i < fallbackTimeFormats.length; i++) {
            ret = parseUsingFormat(text, fallbackTimeFormats[i]);
        }
        return ret;
    }

    protected static long normalizeInLocalRange(long time) {
        return (time + UTCDateBox.DAY_IN_MS) % UTCDateBox.DAY_IN_MS;
    }

    
}