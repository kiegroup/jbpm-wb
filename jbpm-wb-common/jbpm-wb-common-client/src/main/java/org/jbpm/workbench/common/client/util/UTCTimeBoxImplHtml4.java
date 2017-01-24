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

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;


/**
 * Time is represented as the number of milliseconds after midnight
 * independent of time zone.
 * 
 * <p>
 * It will use the GWT DateTimeFormat to parse in the browser
 * timezone, but it will then convert the time to be independent of
 * timezone.
 * </p>
 * 
 * <p>
 * It will first parse a manually typed date using the time format
 * specified (defaulting to TIME_SHORT). It will then attempt the
 * following formats in order: "hh:mm a", "hh:mma", "HH:mm". If all of
 * these fail, it will attempt to insert a colon in the right place
 * (e.g. 12 -> 12:00, 123 -> 1:23, and 1234 -> 12:34) and try the
 * specified time format and the fallback formats again.
 * </p>
 * 
 * <p>
 * The control supports an unspecified value of null with a blank
 * textbox.
 * </p>
 * 
 */
public class UTCTimeBoxImplHtml4 extends UTCTimeBoxImplShared {

    private static final String CLASSNAME_INVALID = "invalid";
    private static final long INTERVAL = 30 * 60 * 1000L;
    private static final long DAY = 24 * 60 * 60 * 1000L;

    private Select textbox;
    private Long lastKnownValue;

    /**
     * Keyboard handler for the TextBox shows and hides the menu,
     * scrolls through the menu, and accepts a value.
     */
    private class TextBoxHandler implements BlurHandler, ValueChangeHandler<String> {

        @Override
        public void onBlur( final BlurEvent event ) {
            clearInvalidStyle();
            validate();
        }

        @Override
        public void onValueChange( final ValueChangeEvent<String> event ) {
            clearInvalidStyle();
            validate();
        }

    }

    /**
     * Allows a UTCTimeBox to be created with a specified format.
     */
    public UTCTimeBoxImplHtml4() {
        this.textbox = new Select();

        final TextBoxHandler handler = new TextBoxHandler();
        textbox.addDomHandler( handler, BlurEvent.getType() );
        textbox.addValueChangeHandler( handler );
        textbox.setFixedMenuSize( 5 );

        initWidget( textbox );
    }

    @Override
    public void setTimeFormat( DateTimeFormat timeFormat ) {
        super.setTimeFormat( timeFormat );
        generateTimeOptions();
    }

    private void generateTimeOptions(){
        int numOptions = (int) (DAY / INTERVAL);

        // we need to use times for formatting, but we don't keep
        // them around. the purpose is only to generate text to
        // insert into the textbox.
        for (int i = 0; i < numOptions; i++) {
            long offsetFromMidnight = i * INTERVAL;
            final String value = generateTimeValue( offsetFromMidnight );
            Option option = new Option();
            option.setText( value );
            option.setValue( value );
            textbox.add( option );
        }

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                textbox.refresh();
            }
        } );
    }

    private String generateTimeValue( long offsetFromMidnight ){
        // format the time in the local time zone
        long time = UTCDateBox.timezoneOffsetMillis(new Date(0)) + offsetFromMidnight;
        return timeFormat.format(new Date(time));
    }

    /**
     * Returns the TextBox on which this control is based.
     */
    public Select getTextBox() {
        return textbox;
    }

    // ----------------------------------------------------------------------
    // HasValue

    public boolean hasValue() {
        return getText().trim().length() > 0;
    }

    /**
     * A valid value is either empty (null) or filled with a valid
     * time.
     */
    public boolean hasValidValue() {
        return !hasValue() || getValue() != null;
    }

    /**
     * Returns the time value (as milliseconds since midnight
     * independent of time zone)
     */
    @Override
    public Long getValue() {
        return text2value(getText());
    }

    /**
     * Sets the time value (as milliseconds since midnight independent
     * of time zone)
     */
    @Override
    public void setValue(Long value, boolean fireEvents) {
        setValue( value, true, fireEvents );
    }

    protected void setValue(Long value, boolean updateTextBox, boolean fireEvents) {
        if (updateTextBox) {
            syncTextToValue(value);
        }

        // keep track of the last known value so that we only fire
        // when it's different.
        Long oldValue = lastKnownValue;
        lastKnownValue = value;

        if (fireEvents && !isSameValue(oldValue, value)) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    protected boolean isSameValue(Long a, Long b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    // ----------------------------------------------------------------------
    // Interaction with the textbox

    @Override
    public String getText() {
        return textbox.getValue();
    }

    @Override
    public void setText(String text) {
        textbox.setValue(text);
        syncValueToText();
    }

    // ----------------------------------------------------------------------
    // synchronization between text and value
    protected void syncTextToValue( final Long value ) {
        final Long valueToSelect = text2value( value2text( value ) );
        final Iterator<Widget> iterator = textbox.iterator();
        while( iterator.hasNext() ){
            final Option option = (Option) iterator.next();
            final Long optionValue = text2value( option.getValue() );
            if( optionValue <= valueToSelect){
                textbox.setValue( option.getValue() );
            }
        }
    }

    protected void syncValueToText() {
        setValue( text2value( getText() ), false, true );
    }

    // ----------------------------------------------------------------------
    // styling
    
    @Override
    public void validate() {
        boolean valid = true;
        if (hasValue()) {
            Long value = getValue();
            if (value != null) {
                // scrub the value to format properly
                setText(value2text(value));
            }
            else {
                // empty is ok and value != null ok, this is invalid
                valid = false;
            }
        }
        setStyleName( CLASSNAME_INVALID, !valid);
    }

    @Override
    public void setVisibleLength( int length ) {
        textbox.setFixedMenuSize( length );
    }

    @Override
    public void setTabIndex(int tabIndex) {
        textbox.setTabIndex(tabIndex);
    }

    public void clearInvalidStyle() {
        removeStyleName(CLASSNAME_INVALID);
    }

}