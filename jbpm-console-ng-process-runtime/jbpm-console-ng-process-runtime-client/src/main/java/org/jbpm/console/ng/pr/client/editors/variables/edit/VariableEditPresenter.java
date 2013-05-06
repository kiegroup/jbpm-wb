package org.jbpm.console.ng.pr.client.editors.variables.edit;

import com.google.gwt.core.client.GWT;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
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
@WorkbenchPopup(identifier = "Edit Variable Popup")
public class VariableEditPresenter {

    public interface PopupView extends UberView<VariableEditPresenter> {

        void displayNotification(String text);

        void setProcessInstanceId(long processInstanceId);

        long getProcessInstanceId();
        
        String getVariableText();
        
        void setVariableText(String value);
        
        void setVariableId(String variableId);
        
        void setVariableIdLabel(String variableId);
        
        String getVariableId();
        
    }
    
    @Inject
    private PopupView view;
    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest place;
    
    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;
    
    private Constants constants = GWT.create(Constants.class);
    
    @PostConstruct
    public void init() {
     

    }

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Edit_Variable();
    }

    @WorkbenchPartView
    public UberView<VariableEditPresenter> getView() {
        return view;
    }

    @OnReveal
    public void onReveal() {
        view.setProcessInstanceId(Long.parseLong(place.getParameter("processInstanceId", "-1").toString()));
        view.setVariableId(place.getParameter("variableId", "-1").toString());
        view.setVariableIdLabel(view.getVariableId());
        view.setVariableText(place.getParameter("value", "-1").toString());
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
    
    public void setProcessVariable(Object value) {

        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                close();
                
            }
        }).setProcessVariable( view.getProcessInstanceId(), view.getVariableId(), value);
    }
}
