package org.jbpm.console.ng.client.editors.tasks.indentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;
import org.jbpm.console.ng.shared.model.IdentitySummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@WorkbenchScreen(identifier = "Users and groups List")
public class IdentityPresenter {

    public interface InboxView extends UberView<IdentityPresenter> {

        void displayNotification(String text);

        TextBox getUserText();

        DataGrid<IdentitySummary> getDataGrid();

    }

    @Inject
    private InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    private ListDataProvider<IdentitySummary> dataProvider = new ListDataProvider<IdentitySummary>();
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Users and Groups";
    }

    @WorkbenchPartView
    public UberView<IdentityPresenter> getView() {
        return view;
    }

    public IdentityPresenter() {
    }

    @PostConstruct
    public void init() {
    }
    
    public void addDataDisplay(HasData<IdentitySummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<IdentitySummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }
    
    @OnReveal
    public void onReveal() {
       refreshIdentityList();
    }

    public void refreshIdentityList() {
        taskServices.call(new RemoteCallback<List<IdentitySummary>>() {
            @Override
            public void callback(List<IdentitySummary> entities) {
                dataProvider.getList().clear();
                if(entities != null){
                    dataProvider.getList().addAll(entities);
                    dataProvider.refresh();
                }

            }
        }).getOrganizationalEntities();
    }
    
    public void getEntityById(String entityId) {
        taskServices.call(new RemoteCallback<IdentitySummary>() {
            @Override
            public void callback(IdentitySummary identity) {
                dataProvider.getList().clear();
                if(identity != null){
                    List<IdentitySummary> values = new ArrayList<IdentitySummary>();
                    values.add(identity);
                    
                    dataProvider.getList().addAll(values);
                    dataProvider.refresh();
                }

            }
        }).getOrganizationalEntityById(entityId);
    }
}
