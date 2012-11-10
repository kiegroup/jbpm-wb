/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.client.editors.tasks.statistics;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jbpm.console.ng.shared.events.ChartPopulateEvent;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gchart.client.GChart;

import org.jbpm.console.ng.client.i18n.Constants;

@Dependent
public class PersonalTasksStatisticsViewImpl
        extends Composite
        implements
    PersonalTasksStatisticsPresenter.InboxView {

    interface PersonalTasksStatisticsViewImplBinder
        extends
        UiBinder<Widget, PersonalTasksStatisticsViewImpl> {
    }

    private static PersonalTasksStatisticsViewImplBinder uiBinder = GWT.create( PersonalTasksStatisticsViewImplBinder.class );

    private PersonalTasksStatisticsPresenter             presenter;

    @UiField
    public Button                                        refreshButton;
    @UiField
    public TextBox                                       userText;
    @UiField
    public VerticalPanel                                graphContainer;

    public GChart                                        chart;

    private Map<String, Column>                          columns  = new HashMap<String, Column>();
    private int                                          position = 1;
    private Column                                       completed;
    private Column                                       pending;
    
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(PersonalTasksStatisticsPresenter presenter) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        chart = createChart();

        completed = new Column( "Completed",
                                0 );
        pending = new Column( "Pending",
                              0 );

        chart.update();
    }

    @UiHandler("refreshButton")
    public void refreshButton(ClickEvent e) {
        presenter.refreshGraphs( userText.getText() );
        graphContainer.add( chart );
        chart.update();
    }

    private GChart createChart() {
        GChart gChart = new GChart();
        gChart.setChartSize( 300,
                             200 );
        gChart.setChartTitle( "<b><big><big>"
                              + constants.Personal_Task_Statistics()
                              + "</big></big><br>&nbsp;</b>" );
        gChart.addCurve();
        gChart.getCurve().getSymbol().setSymbolType(
                                                     GChart.SymbolType.VBAR_SOUTH );
        gChart.getCurve().getSymbol().setBackgroundColor( "#DDF" );
        gChart.getCurve().getSymbol().setModelWidth( 0.5 );
        gChart.getCurve().getSymbol().setBorderColor( "red" );
        gChart.getCurve().getSymbol().setBorderWidth( 1 );

        gChart.getXAxis().setTickThickness( 0 );
        gChart.getXAxis().setAxisMin( 0 );

        gChart.getYAxis().setAxisMin( 0 );
        gChart.getYAxis().setAxisMax( 100 );
        gChart.getYAxis().setTickCount( 11 );
        gChart.getYAxis().setHasGridlines( true );
        gChart.getXAxis().addTick( 0,
                                   "" );

        return gChart;
    }

    @Override
    public void displayCompletedTasks(Integer completedTasks) {
        completed.setValue( completedTasks );
    }

    @Override
    public void displayPendingTasks(Integer pendingTasks) {
        pending.setValue( pendingTasks );
    }

    public void addData(@Observes ChartPopulateEvent event) {

        addColumn( event.getColumnName(),
                   event.getValue() );

        chart.update();
    }

    private void addColumn(String name,
                           double value) {
        columns.put( name,
                     new Column( name,
                                 value ) );
    }

    class Column {

        private final int myPosition;

        Column(String name,
               double value) {
            myPosition = position++;
            chart.getCurve().addPoint( myPosition,
                                       value );
            chart.getCurve().getPoint().setAnnotationText( name );
            chart.getCurve().getPoint().setAnnotationLocation( GChart.AnnotationLocation.SOUTH );

            // Hides X axis ticks that get in our labels way
            chart.getXAxis().addTick( myPosition,
                                      "" );
            // Looks nicer if there is room after the last column
            chart.getXAxis().setAxisMax( position );
        }

        public void setValue(double value) {
            chart.getCurve( 0 ).getPoint( myPosition - 1 ).setY( value );
        }
    }
}
