///*
// * Copyright 2012 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.console.ng.client.editors.executor.list;
//
//import com.google.gwt.user.cellview.client.ColumnSortEvent;
//import com.google.gwt.user.cellview.client.DataGrid;
//import com.google.gwt.user.client.ui.CheckBox;
//
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.enterprise.context.Dependent;
//import javax.inject.Inject;
//
//
//import com.google.gwt.view.client.HasData;
//import com.google.gwt.view.client.ListDataProvider;
//import org.jboss.errai.bus.client.api.RemoteCallback;
//import org.jboss.errai.ioc.client.api.Caller;
//import org.jbpm.console.ng.client.model.RequestSummary;
//import org.jbpm.console.ng.shared.ExecutorServiceEntryPoint;
//import org.uberfire.client.annotations.WorkbenchPartTitle;
//import org.uberfire.client.annotations.WorkbenchPartView;
//import org.uberfire.client.annotations.WorkbenchScreen;
//import org.uberfire.client.mvp.UberView;
//
//@Dependent
//@WorkbenchScreen(identifier = "Errai UI - Requests List")
//public class RequestListPresenter {
//
//    public interface InboxView
//            extends
//            UberView<RequestListPresenter> {
//
//        void displayNotification(String text);
//
//        
//
//        CheckBox getShowCompletedCheck();
//
//        DataGrid<RequestSummary> getDataGrid();
//        
//        ColumnSortEvent.ListHandler<RequestSummary> getSortHandler();
//    }
//    @Inject
//    private InboxView view;
//    @Inject
//    private Caller<ExecutorServiceEntryPoint> executorServices;
//    private ListDataProvider<RequestSummary> dataProvider = new ListDataProvider<RequestSummary>();
//
//    @WorkbenchPartTitle
//    public String getTitle() {
//        return "Errai UI - Requests List";
//    }
//
//    @WorkbenchPartView
//    public UberView<RequestListPresenter> getView() {
//        return view;
//    }
//
//    public RequestListPresenter() {
//    }
//
//    @PostConstruct
//    public void init() {
//    }
//
//    public void refreshRequests(boolean showCompleted) {
//
////        if (showCompleted) {
//            executorServices.call(new RemoteCallback<List<RequestSummary>>() {
//                @Override
//                public void callback(List<RequestSummary> requests) {
//                    dataProvider.setList(requests);
//                    dataProvider.refresh();
//                    view.getSortHandler().getList().addAll(dataProvider.getList());
//                    
//                }
//            }).getPendingRequests();
//       
//
//
//
//    }
//
//   
//
//    public void addDataDisplay(HasData<RequestSummary> display) {
//        dataProvider.addDataDisplay(display);
//    }
//
//    public ListDataProvider<RequestSummary> getDataProvider() {
//        return dataProvider;
//    }
//
//    public void refreshData() {
//        dataProvider.refresh();
//    }
//}
