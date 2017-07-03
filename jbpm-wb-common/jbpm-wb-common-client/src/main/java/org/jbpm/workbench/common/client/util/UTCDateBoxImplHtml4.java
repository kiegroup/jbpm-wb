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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.uberfire.ext.widgets.common.client.common.DatePickerFormatUtilities;

public class UTCDateBoxImplHtml4 extends UTCDateBoxImplShared {

    private HandlerManager handlerManager;
    private DatePicker datebox;

    public UTCDateBoxImplHtml4() {
        handlerManager = new HandlerManager(this);
        datebox = new DatePicker();
        datebox.setAutoClose(true);
        datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                // pass this event onto our handlers after converting
                // the value
                fireValueChangeEvent(UTCDateBox.date2utc(event.getValue()));
            }
        });
    }

    @Override
    public Widget asWidget() {
        return datebox;
    }

    @Override
    public Long getValue() {
        return UTCDateBox.date2utc(datebox.getValue());
    }

    @Override
    public void setValue(Long value,
                         boolean fireEvents) {
        datebox.setValue(UTCDateBox.utc2date(value),
                         fireEvents);
    }

    @Override
    public String getText() {
        return datebox.getTextBox().getValue();
    }

    @Override
    public void setText(String text) {
        String oldValue = getText();
        datebox.getTextBox().setValue(text,
                                      true);
        if (oldValue == null || !oldValue.equals(text)) {
            ValueChangeEvent.fire(this,
                                  getValue());
        }
    }

    @Override
    public boolean isEnabled() {
        return DomUtils.isEnabled(datebox.getTextBox().getElement());
    }

    @Override
    public void setEnabled(boolean enabled) {
        DomUtils.setEnabled(datebox.getTextBox().getElement(),
                            enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(),
                                         handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    public void fireValueChangeEvent(long value) {
        ValueChangeEvent.fire(this,
                              new Long(value));
    }

    // ----------------------------------------------------------------------    

    @Override
    public void setDateFormat(final DateTimeFormat dateFormat) {
        datebox.setFormat(DatePickerFormatUtilities.convertToBS3DateFormat(dateFormat.getPattern()));
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                datebox.reload();
            }
        });
    }

    @Override
    public void setTabIndex(int tabIndex) {
        datebox.getTextBox().setTabIndex(tabIndex);
    }

    @Override
    public DatePicker getDateBox() {
        return datebox;
    }
}