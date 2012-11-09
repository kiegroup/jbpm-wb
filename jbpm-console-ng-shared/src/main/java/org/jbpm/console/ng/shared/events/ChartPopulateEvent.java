package org.jbpm.console.ng.shared.events;

import org.jboss.errai.common.client.api.annotations.Portable;

public class ChartPopulateEvent {

    private String columnName;
    private double value;

    public ChartPopulateEvent() {
    }

    public ChartPopulateEvent(String columnName, double value) {
        this.columnName = columnName;
        this.value = value;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public double getValue() {
        return value;
    }
}
