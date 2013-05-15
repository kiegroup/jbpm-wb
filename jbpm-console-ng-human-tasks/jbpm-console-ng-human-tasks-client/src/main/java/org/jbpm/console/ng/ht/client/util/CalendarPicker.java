/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.util;

import java.util.Date;

import org.jbpm.console.ng.ht.client.i8n.Constants;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;

/**
 * Encapsulates set of components which are able to select day/week/month. Contains also buttons for going to previous or next
 * day/week/month.
 */
public class CalendarPicker extends Composite implements HasValueChangeHandlers<Date> {
    private Constants constants = GWT.create(Constants.class);

    public enum ViewType {
        DAY, WEEK, MONTH;
    }

    private Date currentDate;
    private ViewType viewType;

    private UnorderedList mainPanel = new UnorderedList();
    
   
    private NavLink calendarPanel = new NavLink();
    private Label calendarLabel;
    private IconAnchor calendarIcon;
    private NavLink previousButton;
    private NavLink nextButton;
    private NavLink todayButton;
    
    

    public CalendarPicker() {
        currentDate = new Date();
        viewType = ViewType.DAY;

        calendarLabel = new Label();
        calendarLabel.setStyleName("");
        calendarIcon = new IconAnchor();
        previousButton = new NavLink();
        nextButton = new NavLink();
        todayButton = new NavLink();
        initWidget(mainPanel);
    }

    public String getViewType() {
        return viewType.toString();
    }

    public void setViewType(String viewType) {
        this.viewType = ViewType.valueOf(viewType.toUpperCase());
        updateCalendarLabelText();
    }

    public void init() {
        
        

        initCalendarIcon();
        calendarPanel.add(calendarLabel);
        calendarPanel.add(calendarIcon);
        mainPanel.add(calendarPanel);
        
        
        initPrevNextTodayButtons();
        mainPanel.add(previousButton);
        mainPanel.add(todayButton);
        mainPanel.add(nextButton);
     
        
        updateCalendarLabelText();
    }

    /**
     * Adjust the date (back or to future) based on current view type (day/week/month).
     *
     * @param back flag that indicates if the date should be adjusted to future (+) or back (-)
     */
    private void adjustDate(boolean back) {
        int dayDiff = 0;
        switch (viewType) {
            case DAY:
                dayDiff = 1;
                break;

            case WEEK:
                dayDiff = 7;
                break;

            case MONTH:
                dayDiff = 30;
                break;

        }
        if (back) {
            CalendarUtil.addDaysToDate(currentDate, -dayDiff);
        } else {
            CalendarUtil.addDaysToDate(currentDate, dayDiff);
        }
        propagateDateChanges();
    }

    private void propagateDateChanges() {
        updateCalendarLabelText();
        ValueChangeEvent.fire(this, currentDate);
    }

    /**
     * Updates the label text based on current view type and current date.
     *
     * Examples for day, week and month:
     * <ul>
     * <li>day view: 2013-05-02
     * <li>week view: May 06 - May 10 2013
     * <li>month view: May 2013
     * </ul>
     */
    private void updateCalendarLabelText() {
        switch (viewType) {
            case DAY: {
                DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
                calendarLabel.setText(fmt.format(currentDate));
                break;
            }
            case WEEK: {
                DateTimeFormat fmt = DateTimeFormat.getFormat("MMM dd");
                DateRange weekRange = DateUtils.getWeekDateRange(currentDate);
                String text = fmt.format(weekRange.getStartDate());
                text = text + " - " + fmt.format(weekRange.getEndDate());
                fmt = DateTimeFormat.getFormat("yyyy");
                text = text + " " + fmt.format(weekRange.getEndDate());
                calendarLabel.setText(text);
                break;
            }
            case MONTH: {
                DateTimeFormat fmt = DateTimeFormat.getFormat("MMM yyyy");
                calendarLabel.setText(fmt.format(currentDate));
                break;
            }
            default:
                throw new IllegalStateException("Unrecognized view type " + viewType);
        }
    }

    /**
     * Registers handler that is called when the current date value changes.
     */
    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void initCalendarIcon() {
        calendarIcon.setIcon(IconType.CALENDAR);
        calendarIcon.setIconSize(IconSize.LARGE);
        calendarIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                calendarPanel.clear();
                DateBox dateBox = new DateBox();
                dateBox.setAutoClose(true);
                dateBox.setValue(currentDate, false);
                dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Date> event) {
                        currentDate = event.getValue();
                        propagateDateChanges();
                    }
                });
                calendarPanel.add(dateBox);
                dateBox.show();
                dateBox.removeFromParent();
                calendarPanel.add(calendarIcon);
            }
        });
    }

    private void initPrevNextTodayButtons() {
        previousButton.setIcon(IconType.CARET_LEFT);
        previousButton.setIconSize(IconSize.LARGE);
        previousButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                adjustDate(true);
            }
        });
        nextButton.setIcon(IconType.CARET_RIGHT);
        nextButton.setIconSize(IconSize.LARGE);
        nextButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                adjustDate(false);
            }
        });
        todayButton.setText(constants.Today());
        todayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                currentDate = new Date();
                propagateDateChanges();
            }
        });
    }

}
