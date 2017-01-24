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
import com.google.gwt.user.datepicker.client.CalendarUtil;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ColumnOffset;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.jbpm.workbench.common.client.i18n.Constants;

/**
 * Encapsulates set of components which are able to select day/week/month. Contains also buttons for going to previous or next
 * day/week/month.
 */
public class CalendarPicker extends Composite implements HasValueChangeHandlers<Date> {

    private Constants constants = GWT.create( Constants.class );

    public enum ViewType {
        DAY, WEEK, MONTH, GRID;
    }

    private Date currentDate;
    private ViewType viewType;

    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel calendarPanel = new FlowPanel();
    private FlowPanel iconPanel = new FlowPanel();

    private FlowPanel rightPanel = new FlowPanel();
    private FlowPanel controlsPanel = new FlowPanel();

    private Heading calendarLabel;
    private Heading iconLabel;
    private Heading controlsLabel;
    private Anchor calendarIcon;
    private Button previousButton;
    private Button nextButton;
    private Button todayButton;

    public CalendarPicker() {
        currentDate = new Date();
        viewType = ViewType.DAY;
        calendarPanel.addStyleName( ColumnSize.MD_2.getCssName() );
        calendarPanel.addStyleName( ColumnOffset.MD_1.getCssName() );
        calendarLabel = new Heading( HeadingSize.H4 );
        iconLabel = new Heading( HeadingSize.H4 );
        iconLabel.addStyleName( ColumnSize.MD_2.getCssName() );
        iconPanel.add( iconLabel );
        controlsLabel = new Heading( HeadingSize.H4 );
        controlsPanel.add( controlsLabel );

        calendarIcon = new Anchor();
        previousButton = new Button();
        nextButton = new Button();
        todayButton = new Button();
        initWidget( mainPanel );
    }

    public String getViewType() {
        return viewType.toString();
    }

    public void setViewType( String viewType ) {
        this.viewType = ViewType.valueOf( viewType.toUpperCase() );
        updateCalendarLabelText();
        updateTodayButtonEnabled();
    }

    public void init() {
        initCalendarIcon();
        calendarPanel.add( calendarLabel );
        mainPanel.add( calendarPanel );
        initPrevNextTodayButtons();
        rightPanel.add( previousButton );
        rightPanel.add( todayButton );
        rightPanel.add( nextButton );
        iconLabel.add( calendarIcon );
        iconPanel.add( iconLabel );
        mainPanel.add( iconPanel );
        rightPanel.addStyleName( Styles.BTN_GROUP );
        rightPanel.addStyleName( Styles.PULL_RIGHT );
        controlsLabel.add( rightPanel );
        mainPanel.add( controlsPanel );

        updateCalendarLabelText();
    }

    /**
     * Adjust the date (back or to future) based on current view type (day/week/month).
     * @param back flag that indicates if the date should be adjusted to future (+) or back (-)
     */
    private void adjustDate( boolean back ) {
        switch ( viewType ) {
            case DAY:
                if ( back ) {
                    CalendarUtil.addDaysToDate( currentDate, -1 );
                } else {
                    CalendarUtil.addDaysToDate( currentDate, 1 );
                }
                break;

            case WEEK:
                if ( back ) {
                    CalendarUtil.addDaysToDate( currentDate, -7 );
                } else {
                    CalendarUtil.addDaysToDate( currentDate, 7 );
                }

                break;

            case MONTH:
                if ( back ) {
                    currentDate = DateUtils.getSameOrClosestDateInPreviousMonth( currentDate );
                } else {
                    currentDate = DateUtils.getSameOrClosestDateInNextMonth( currentDate );
                }

                break;
            case GRID:
                // no date change needed
                break;

        }
        propagateDateChanges();
    }

    private void propagateDateChanges() {
        updateCalendarLabelText();
        updateTodayButtonEnabled();
        ValueChangeEvent.fire( this, currentDate );
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
        switch ( viewType ) {
            case DAY: {
                mainPanel.setVisible( true );
                DateTimeFormat fmt = DateTimeFormat.getFormat( "EEE, dd MMMM" );
                calendarLabel.setText( fmt.format( currentDate ) );
                break;
            }
            case WEEK: {
                mainPanel.setVisible( true );
                DateTimeFormat fmt = DateTimeFormat.getFormat( "dd MMM" );
                DateRange weekRange = DateUtils.getWeekDateRange( currentDate );
                String text = fmt.format( weekRange.getStartDate() );
                text = text + " - " + fmt.format( weekRange.getEndDate() );
                calendarLabel.setText( text );
                break;
            }
            case MONTH: {
                mainPanel.setVisible( true );
                DateTimeFormat fmt = DateTimeFormat.getFormat( "MMMM yy" );
                calendarLabel.setText( fmt.format( currentDate ) );
                break;
            }
            case GRID:
                mainPanel.setVisible( false );
                break;

            default:
                throw new IllegalStateException( "Unrecognized view type " + viewType );
        }
    }

    /**
     * Registers handler that is called when the current date value changes.
     */
    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<Date> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void initCalendarIcon() {
        calendarIcon.setIcon( IconType.CALENDAR );
        calendarIcon.setTitle( Constants.INSTANCE.Select_Date() );
        calendarIcon.setIconSize( IconSize.LARGE );
        calendarIcon.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                calendarPanel.clear();
                DatePicker dateBox = new DatePicker();
                dateBox.setAutoClose( true );
                dateBox.setValue( currentDate, false );
                dateBox.addValueChangeHandler( new ValueChangeHandler<Date>() {
                    @Override
                    public void onValueChange( ValueChangeEvent<Date> event ) {
                        currentDate = event.getValue();
                        propagateDateChanges();
                    }
                } );
                calendarPanel.add( dateBox );
                dateBox.show();
                dateBox.removeFromParent();

                calendarPanel.add( calendarLabel );
            }
        } );
    }

    private void initPrevNextTodayButtons() {
        previousButton.setIcon( IconType.CARET_LEFT );
        previousButton.setTitle( Constants.INSTANCE.Previous() );
        previousButton.setIconSize( IconSize.LARGE );
        previousButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                adjustDate( true );
            }
        } );
        nextButton.setIcon( IconType.CARET_RIGHT );
        nextButton.setTitle( Constants.INSTANCE.Next() );
        nextButton.setIconSize( IconSize.LARGE );
        nextButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                adjustDate( false );
            }
        } );
        todayButton.setText( constants.Today() );
        todayButton.setEnabled( false );
        todayButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                currentDate = new Date();
                propagateDateChanges();
            }
        } );
    }

    /**
     * Determines if 'today' is within the currently displayed date range (e.g. week or month). If it is, the 'Today' button is
     * disabled, otherwise its enabled.
     * <p/>
     * Clicking on "Today" button when current day (today) is already displayed is useless, so its better to disable the button.
     */
    private void updateTodayButtonEnabled() {
        boolean todayBtnEnabled = true;
        Date today = new Date();

        switch ( viewType ) {
            case DAY:
                if ( DateUtils.areDatesEqual( today, currentDate ) ) {
                    todayBtnEnabled = false;
                }
                break;

            case WEEK:
                DateRange weekRange = DateUtils.getWeekDateRange( currentDate );
                if ( DateUtils.isDateInRange( today, weekRange ) ) {
                    todayBtnEnabled = false;
                }
                break;

            case MONTH:
                DateRange monthRange = DateUtils.getMonthDateRange( currentDate );
                if ( DateUtils.isDateInRange( today, monthRange ) ) {
                    todayBtnEnabled = false;
                }
                break;
            case GRID:
                todayBtnEnabled = false;
                break;
            default:
                throw new IllegalStateException( "Unrecognized calendar view type: " + viewType );
        }
        todayButton.setEnabled( todayBtnEnabled );
    }

}