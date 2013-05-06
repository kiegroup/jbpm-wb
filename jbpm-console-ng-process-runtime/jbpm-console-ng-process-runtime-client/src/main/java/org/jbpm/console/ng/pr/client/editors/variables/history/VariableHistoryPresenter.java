package org.jbpm.console.ng.pr.client.editors.variables.history;

import com.google.gwt.core.client.GWT;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Variable History Popup")
public class VariableHistoryPresenter {

    public interface PopupView extends UberView<VariableHistoryPresenter> {

        void displayNotification( String text );

        void setProcessInstanceId( long processInstanceId );

        long getProcessInstanceId();

        void setVariableId( String variableId );

        String getVariableId();
    }

    @Inject
    private PopupView                    view;
    @Inject
    private Identity                     identity;
    @Inject
    private PlaceManager                 placeManager;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest                 place;

    private ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    private Constants constants = GWT.create(Constants.class);
    
    @PostConstruct
    public void init() {

    }

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Variable_History();
    }

    @WorkbenchPartView
    public UberView<VariableHistoryPresenter> getView() {
        return view;
    }

    @OnReveal
    public void onReveal() {
        view.setProcessInstanceId( Long.parseLong( place.getParameter( "processInstanceId", "-1" ).toString() ) );
        view.setVariableId( place.getParameter( "variableId", "-1" ).toString() );

        loadVariableHistory();
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void loadVariableHistory() {
        dataServices.call( new RemoteCallback<List<VariableSummary>>() {
            @Override
            public void callback( List<VariableSummary> processInstances ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( processInstances );
                dataProvider.refresh();
            }
        } ).getVariableHistory( view.getProcessInstanceId(), view.getVariableId() );
    }

    public void addDataDisplay( HasData<VariableSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<VariableSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }
}
