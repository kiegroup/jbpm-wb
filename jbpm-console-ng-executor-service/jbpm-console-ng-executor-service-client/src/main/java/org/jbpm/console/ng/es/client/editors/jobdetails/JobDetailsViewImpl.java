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
package org.jbpm.console.ng.es.client.editors.jobdetails;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.es.client.editors.jobdetails.JobDetailsPresenter.JobDetailsView;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@Templated(value = "JobDetailsViewImpl.html")
public class JobDetailsViewImpl extends Composite implements JobDetailsView {

	@Inject
	@DataField
	private Label jobRetries;
	@Inject
	@DataField
	private DataGrid<RequestParameterSummary> executionParametersGrid;
	@Inject
	@DataField
	private VerticalPanel errorsOccurredList;
	private Constants constants = GWT.create(Constants.class);
	private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();
	
	@Override
	public void init(JobDetailsPresenter p) {
		Column<RequestParameterSummary, String> paramKeyColumn = new Column<RequestParameterSummary, String>(new TextCell()) {
        	public String getValue(RequestParameterSummary rowObject) {
        		return rowObject.getKey();
        	}
        };
        executionParametersGrid.setHeight("200px");

        //      Set the message to display when the table is empty.
        executionParametersGrid.setEmptyTableWidget(new Label(constants.No_Parameters_added_yet()));
        executionParametersGrid.addColumn(paramKeyColumn, 
        		new ResizableHeader<RequestParameterSummary>("Key", executionParametersGrid, paramKeyColumn));

        Column<RequestParameterSummary, String> paramValueColumn = new Column<RequestParameterSummary, String>(new TextCell()) {
        	public String getValue(RequestParameterSummary rowObject) {
        		return rowObject.getValue();
        	}
        };
        executionParametersGrid.addColumn(paramValueColumn, 
        		new ResizableHeader<RequestParameterSummary>("Value", executionParametersGrid, paramValueColumn));
	}
	
	public void setRequest(RequestSummary r, List<ErrorSummary> errors, List<RequestParameterSummary> params) {
		this.jobRetries.setText(String.valueOf(r.getExecutions()));
		if (errors != null) {
			for (ErrorSummary error : errors) {
				String html = "<strong>" + error.getMessage() + "</strong><br/>" + error.getStacktrace();
				this.errorsOccurredList.add(new HTML(SafeHtmlUtils.fromTrustedString(html)));
			}
		}
		if (params != null) {
			for (RequestParameterSummary param : params) {
				this.dataProvider.getList().add(param);
			}
			dataProvider.addDataDisplay(executionParametersGrid);
		}
	}
}
