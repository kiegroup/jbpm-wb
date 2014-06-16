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
package org.jbpm.console.ng.gc.client.experimental.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.gc.client.experimental.grid.base.DataMockSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.DataService;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;

@Dependent
@WorkbenchScreen(identifier = "Grid Base Test")
public class GridBasePresenter {

  public interface GridBaseListView extends UberView<GridBasePresenter> {

    void displayNotification(String text);

    void showBusyIndicator(String message);

    void hideBusyIndicator();
    
    ExtendedPagedTable<DataMockSummary> getListGrid();
  }


  private Constants constants = GWT.create(Constants.class);

  private AsyncDataProvider<DataMockSummary> dataProvider;


  @Inject
  private GridBaseListView view;

  @Inject
  private DataService dataServices;

  

  @WorkbenchPartTitle
  public String getTitle() {
    return "Grid Experiment";
  }

  @WorkbenchPartView
  public UberView<GridBasePresenter> getView() {
    return view;
  }

  public GridBasePresenter() {
    dataProvider = new AsyncDataProvider<DataMockSummary>() {

      @Override
      protected void onRangeChanged(HasData<DataMockSummary> display) {
        
        Range visibleRange = display.getVisibleRange();
        if(dataServices.getDataCount() > 0){
            QueryFilter filter = new PortableQueryFilter(visibleRange.getStart(), 
                                                          visibleRange.getLength(), 
                                                          false, "", 
                                                          view.getListGrid().getColumnSortList().get(0)
                                                                    .getColumn().getDataStoreName(), 
                                                          view.getListGrid().getColumnSortList().get(0)
                                                                    .isAscending());
            dataProvider.updateRowData(visibleRange.getStart(), dataServices.getData(filter));
        }
        
      }
    };
    
  }
  
  public void addDataDisplay(final HasData<DataMockSummary> display ){
     dataProvider.addDataDisplay(display);
  
  }
  
  public void createData(){
    dataServices.createData();
  }

  public void refreshList() {
    QueryFilter filter = new PortableQueryFilter(0, 
                                                          10, // Page size 
                                                          false, "", 
                                                          view.getListGrid().getColumnSortList().get(0)
                                                                    .getColumn().getDataStoreName(), 
                                                          view.getListGrid().getColumnSortList().get(0)
                                                                    .isAscending());
    dataProvider.updateRowData(0, dataServices.getData(filter));
    
     
  }


  public AsyncDataProvider<DataMockSummary> getDataProvider() {
    return dataProvider;
  }

  @OnOpen
  public void onOpen() {
    refreshList();
  }

  @OnFocus
  public void onFocus() {
    refreshList();
  }

  
}
