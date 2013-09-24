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

import org.jbpm.console.ng.ht.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
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
import com.google.gwt.user.datepicker.client.CalendarUtil;
import org.jbpm.console.ng.ht.model.CalendarListContainer;

/**
 * Encapsulates set of components which are able to select day/week/month.
 * Contains also buttons for going to previous or next day/week/month.
 */
public class LiCalendarPicker extends Composite implements HasValueChangeHandlers<Date> {

    private Constants constants = GWT.create(Constants.class);

    public enum ViewType {

        DAY, WEEK, MONTH, GRID;
    }

    private Date currentDate;
    private ViewType viewType;

    private NavLink calendarLink;

    private IconAnchor calendarIcon;

    public NavLink dayViewTasksNavLink;

    public NavLink weekViewTasksNavLink;

    public NavLink monthViewTasksNavLink;

    public NavLink buttonGroupLi;

    public ButtonGroup buttonGroup;

    private UnorderedList ul;

    private Button previousButton;

    private Button nextButton;

    private Button todayButton;

    private CalendarListContainer listContainer;

    public LiCalendarPicker() {
        ul = new UnorderedList();
        currentDate = new Date();
        viewType = ViewType.DAY;
        initWidget(ul);
    }

    public void init() {
        calendarLink = new NavLink();
        dayViewTasksNavLink = new NavLink();
        weekViewTasksNavLink = new NavLink();
        monthViewTasksNavLink = new NavLink();

        previousButton = new Button();
        previousButton.setSize(ButtonSize.SMALL);
        nextButton = new Button();
        nextButton.setSize(ButtonSize.SMALL);
        todayButton = new Button();
        todayButton.setSize(ButtonSize.SMALL);
        buttonGroup = new ButtonGroup();
        buttonGroupLi = new NavLink();

        ul.add(calendarLink);
        
        initPrevNextTodayButtons();

        ul.add(buttonGroupLi);

        NavLink dividerNavLink1 = new NavLink();
        dividerNavLink1.setStyleName("divider-vertical");
        dividerNavLink1.remove(dividerNavLink1.getAnchor());
        ul.add(dividerNavLink1);
        
        ul.add(dayViewTasksNavLink);

        ul.add(weekViewTasksNavLink);

        ul.add(monthViewTasksNavLink);
        NavLink dividerNavLink2 = new NavLink();
        dividerNavLink2.setStyleName("divider-vertical");
        dividerNavLink2.remove(dividerNavLink1.getAnchor());
        ul.add(dividerNavLink2);

        dayViewTasksNavLink.setStyleName("");
        weekViewTasksNavLink.setStyleName("");
        monthViewTasksNavLink.setStyleName("");
        dayViewTasksNavLink.setText(constants.Day());
        dayViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listContainer.setDayView();
            }

        });

        weekViewTasksNavLink.setText(constants.Week());
        weekViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listContainer.setWeekView();

            }
        });

        monthViewTasksNavLink.setText(constants.Month());
        monthViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listContainer.setMonthView();

            }
        });

       

        calendarIcon = new IconAnchor();
        calendarLink.add(calendarIcon);

        initCalendarIcon();
        updateCalendarLabelText();
    }

    public void clear() {
        ul.clear();
    }

    public CalendarListContainer getListContainer() {
        return listContainer;
    }

    public void setListContainer(CalendarListContainer listContainer) {
        this.listContainer = listContainer;
    }

    private void initPrevNextTodayButtons() {
        previousButton.setIcon(IconType.CARET_LEFT);
        previousButton.setIconSize(IconSize.SMALL);
        previousButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                adjustDate(true);
                updateTodayButtonEnabled();
            }
        });
        nextButton.setIcon(IconType.CARET_RIGHT);
        nextButton.setIconSize(IconSize.SMALL);
        nextButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                adjustDate(false);
                updateTodayButtonEnabled();
            }
        });
        todayButton.setText(constants.Today());
        todayButton.setEnabled(false);
        todayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setCurrentDate(new Date());
                propagateDateChanges();
                updateTodayButtonEnabled();
            }
        });

        buttonGroup.add(previousButton);
        buttonGroup.add(todayButton);
        buttonGroup.add(nextButton);
        buttonGroupLi.remove(buttonGroupLi.getAnchor());
        buttonGroupLi.addWidget(buttonGroup);

    }

    public void setDayView() {

        dayViewTasksNavLink.setStyleName("active");
        weekViewTasksNavLink.setStyleName("");
        monthViewTasksNavLink.setStyleName("");
        setViewType("day");
    }

    public void setWeekView() {

        dayViewTasksNavLink.setStyleName("");
        monthViewTasksNavLink.setStyleName("");

        weekViewTasksNavLink.setStyleName("active");
        setViewType("week");

    }

    public void setMonthView() {
        dayViewTasksNavLink.setStyleName("");
        weekViewTasksNavLink.setStyleName("");
        monthViewTasksNavLink.setStyleName("active");
        setViewType("month");
        updateTodayButtonEnabled();
    }

    public String getViewType() {
        return viewType.toString();
    }

    public void setViewType(String viewType) {
        this.viewType = ViewType.valueOf(viewType.toUpperCase());
        updateTodayButtonEnabled();
        updateCalendarLabelText();
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * Adjust the date (back or to future) based on current view type
     * (day/week/month).
     *
     * @param back flag that indicates if the date should be adjusted to future
     * (+) or back (-)
     */
    public void adjustDate(boolean back) {
        switch (viewType) {
            case DAY:
                if (back) {
                CalendarUtil.addDaysToDate(currentDate, -1);
            } else {
                CalendarUtil.addDaysToDate(currentDate, 1);
            }
                break;

            case WEEK:
                if (back) {
                CalendarUtil.addDaysToDate(currentDate, -7);
            } else {
                CalendarUtil.addDaysToDate(currentDate, 7);
            }

                break;

            case MONTH:
                if (back) {
                currentDate = DateUtils.getSameOrClosestDateInPreviousMonth(currentDate);
            } else {
                currentDate = DateUtils.getSameOrClosestDateInNextMonth(currentDate);
            }

                break;
            case GRID:
                // no date change needed
                break;

        }
        propagateDateChanges();
    }

    public void propagateDateChanges() {
        updateCalendarLabelText();
        ValueChangeEvent.fire(this, currentDate);
    }

    /**
     * Updates the label text based on current view type and current date.
     * <p/>
     * Examples for day, week and month:
     * <ul>
     * <li>day view: 2013-05-02
     * <li>week view: May 06 - May 12 2013
     * <li>month view: May 2013
     * </ul>
     */
    private void updateCalendarLabelText() {
        switch (viewType) {
            case DAY: {
                calendarLink.setVisible(true);
                DateTimeFormat fmt = DateTimeFormat.getFormat("EEE, dd MMMM");
                calendarLink.setText(fmt.format(currentDate));
                break;
            }
            case WEEK: {
                calendarLink.setVisible(true);
                DateTimeFormat fmt = DateTimeFormat.getFormat("dd MMM");
                DateRange weekRange = DateUtils.getWeekDateRange(currentDate);
                String text = fmt.format(weekRange.getStartDate());
                text = text + " - " + fmt.format(weekRange.getEndDate());
                calendarLink.setText(text);
                break;
            }
            case MONTH: {
                calendarLink.setVisible(true);
                DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM yy");
                calendarLink.setText(fmt.format(currentDate));
                break;
            }
            case GRID:
                calendarLink.setVisible(false);
                break;

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
        calendarIcon.setIconSize(IconSize.SMALL);
        calendarIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DateBox dateBox = new DateBox();
                dateBox.setAutoClose(true);
                dateBox.setValue(currentDate, false);
                dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Date> event) {
                        currentDate = event.getValue();
                        propagateDateChanges();
                        updateTodayButtonEnabled();
                    }
                });
                calendarLink.add(dateBox);
                dateBox.show();
                dateBox.removeFromParent();
            }
        });
    }

    /**
     * Determines if 'today' is within the currently displayed date range (e.g.
     * week or month). If it is, the 'Today' button is disabled, otherwise its
     * enabled.
     * <p/>
     * Clicking on "Today" button when current day (today) is already displayed
     * is useless, so its better to disable the button.
     */
    private void updateTodayButtonEnabled() {
        boolean todayBtnEnabled = true;
        Date today = new Date();

        switch (viewType) {
            case DAY:
                if (DateUtils.areDatesEqual(today, currentDate)) {
                todayBtnEnabled = false;
            }
                break;

            case WEEK:
                DateRange weekRange = DateUtils.getWeekDateRange(currentDate);
                if (DateUtils.isDateInRange(today, weekRange)) {
                    todayBtnEnabled = false;
                }
                break;

            case MONTH:
                DateRange monthRange = DateUtils.getMonthDateRange(currentDate);
                if (DateUtils.isDateInRange(today, monthRange)) {
                    todayBtnEnabled = false;
                }
                break;
            case GRID:
                todayBtnEnabled = false;
                break;
            default:
                throw new IllegalStateException("Unrecognized calendar view type: " + viewType);
        }
        todayButton.setEnabled(todayBtnEnabled);
    }

}
