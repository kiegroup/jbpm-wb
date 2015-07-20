package org.jbpm.console.ng.pr.client.editors.instance.list.dash.advance;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.BaseDataSetProcessInstanceListPresenter;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Advanced DataSet Process Instance List")
public class AdvancedDataSetProcessInstanceListPresenter extends BaseDataSetProcessInstanceListPresenter {
    
    public interface AdvancedDataSetProcessInstanceListView extends BaseDataSetProcessInstanceListView {

    }

    @Inject 
    AdvancedDataSetProcessInstanceListView view;
    
    @Override
    protected BaseDataSetProcessInstanceListView getSpecificView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<BaseDataSetProcessInstanceListPresenter> getView() {
        return view;
    }
    
    public void suspendProcessInstance(String processDefId,
            long processInstanceId) {
      kieSessionServices.call(new RemoteCallback<Void>() {
        @Override
        public void callback(Void v) {
          refreshGrid(  );

        }
      }, new ErrorCallback<Message>() {
        @Override
        public boolean error(Message message, Throwable throwable) {
          ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
          return true;
        }
      }).suspendProcessInstance(processInstanceId);
    }

    public void bulkSignal(List<ProcessInstanceSummary> processInstances) {
      StringBuilder processIdsParam = new StringBuilder();
      if (processInstances != null) {

        for (ProcessInstanceSummary selected : processInstances) {
          if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
            view.displayNotification(constants.Signaling_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                    + ")");
            continue;
          }
          processIdsParam.append(selected.getId() + ",");
        }
        // remove last ,
        if (processIdsParam.length() > 0) {
          processIdsParam.deleteCharAt(processIdsParam.length() - 1);
        }
      } else {
        processIdsParam.append("-1");
      }
      PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
      placeRequestImpl.addParameter("processInstanceId", processIdsParam.toString());

      placeManager.goTo(placeRequestImpl);
      view.displayNotification(constants.Signaling_Process_Instance());

    }
}
