/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.client.editors.deployment.list;

import org.jbpm.console.ng.bd.model.DeploymentUnitSummary;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.paging.PageResponse;

@Dependent
@WorkbenchScreen(identifier = "Deployments List")
public class DeploymentUnitsListPresenter extends AbstractScreenListPresenter<KModuleDeploymentUnitSummary> {

  public interface DeploymentUnitsListView extends ListView<KModuleDeploymentUnitSummary,DeploymentUnitsListPresenter> {

  }

  @Inject
  protected DeploymentUnitsListView view;

  @Inject
  private Caller<DeploymentManagerEntryPoint> deploymentManagerService;

  private Constants constants = GWT.create(Constants.class);

  public DeploymentUnitsListPresenter() {
    dataProvider = new AsyncDataProvider<KModuleDeploymentUnitSummary>() {

      @Override
      protected void onRangeChanged(HasData<KModuleDeploymentUnitSummary> display) {
        view.showBusyIndicator(constants.Loading());
        final Range visibleRange = display.getVisibleRange();
        ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
        if(currentFilter == null){
          currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                visibleRange.getLength(),
                false, "",
                (columnSortList.size() > 0) ? columnSortList.get(0)
                .getColumn().getDataStoreName() : "",
                (columnSortList.size() > 0) ? columnSortList.get(0)
                .isAscending() : true);
        }
        // If we are refreshing after a search action, we need to go back to offset 0
        if(currentFilter.getParams() == null || currentFilter.getParams().isEmpty() 
                || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")){
          currentFilter.setOffset(visibleRange.getStart());
          currentFilter.setCount(visibleRange.getLength());
        }else{
          currentFilter.setOffset(0);
          currentFilter.setCount(view.getListGrid().getPageSize());
        }
        
        currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
                .getColumn().getDataStoreName() : "");
        currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
                .isAscending() : true);
        deploymentManagerService.call(new RemoteCallback<PageResponse<KModuleDeploymentUnitSummary>>() {
          @Override
          public void callback(PageResponse<KModuleDeploymentUnitSummary> response) {
            view.hideBusyIndicator();
            dataProvider.updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
            dataProvider.updateRowData( response.getStartRowIndex(),
                                       response.getPageRowList() );
          }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error(Message message, Throwable throwable) {
            view.hideBusyIndicator();
            view.displayNotification("Error: Getting deployment units: " + message);
            GWT.log(throwable.toString());
            return true;
          }
        }).getData(currentFilter);

      }
    };
  }


  public void undeployUnit(final String id, final String group, final String artifact, final String version,
          final String kbaseName, final String kieSessionName) {
    view.showBusyIndicator(constants.Please_Wait());
    deploymentManagerService.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void nothing) {
        view.hideBusyIndicator();
        view.displayNotification(" Kjar Undeployed " + group + ":" + artifact + ":" + version);
        refreshGrid();
      }
    }, new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        view.hideBusyIndicator();
        view.displayNotification("Error: Undeploy failed, check Problems panel");
        return true;
      }
    }).undeploy(new KModuleDeploymentUnitSummary(id, group, artifact, version, kbaseName, kieSessionName, null, null));
  }

  public void activateOrDeactivate(final DeploymentUnitSummary unitSummary, boolean activate) {
      if (activate) {
          view.showBusyIndicator(constants.Please_Wait());
          deploymentManagerService.call(new RemoteCallback<Void>() {
                                            @Override
                                            public void callback(Void nothing) {
                view.hideBusyIndicator();
                view.displayNotification(" Kjar activated " + unitSummary.getId());
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.hideBusyIndicator();
                view.displayNotification("Error: Activation failed, check Problems panel");
                return true;
            }
        }).activate(unitSummary);
      }  else {
          view.showBusyIndicator(constants.Please_Wait());
          deploymentManagerService.call(new RemoteCallback<Void>() {
                                            @Override
                                            public void callback(Void nothing) {
                    view.hideBusyIndicator();
                    view.displayNotification(" Kjar deactivated " + unitSummary.getId());
                    refreshGrid();
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error(Message message, Throwable throwable) {
                    view.hideBusyIndicator();
                    view.displayNotification("Error: Deactivation failed, check Problems panel");
                    return true;
                }
            }).deactivate(unitSummary);
      }
  }

  @WorkbenchPartView
  public UberView<DeploymentUnitsListPresenter> getView() {
    return view;
  }

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Deployment_Units();
  }
}
