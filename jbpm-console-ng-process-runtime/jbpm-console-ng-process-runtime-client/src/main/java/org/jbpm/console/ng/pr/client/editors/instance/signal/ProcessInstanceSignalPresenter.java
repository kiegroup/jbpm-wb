package org.jbpm.console.ng.pr.client.editors.instance.signal;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.KnowledgeDomainServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
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
@WorkbenchPopup(identifier = "Signal Process Popup")
public class ProcessInstanceSignalPresenter {

    public interface PopupView extends UberView<ProcessInstanceSignalPresenter> {

        void displayNotification(String text);

        void addProcessInstanceId(long processInstanceId);

        String getSignalRefText();
        
        String getEventText();
        
        void setAvailableSignals(Collection<String> signals);

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
    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    
    @Inject 
    private Caller<KieSessionEntryPoint> sessionServices;
    
    @PostConstruct
    public void init() {
     

    }

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Signal process instance";
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceSignalPresenter> getView() {
        return view;
    }
    
    public void signalProcessInstance(long processInstanceId) {

        sessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                close();
                
            }
        }).signalProcessInstance(processInstanceId, view.getSignalRefText(), view.getEventText());
    }
    
    @OnReveal
    public void onReveal() {
        String processInstanceIds = place.getParameter("processInstanceId", "-1").toString();
        String[] ids = processInstanceIds.split(",");
        for (String id : ids) {
            long processInstanceId = Long.parseLong(id);
            view.addProcessInstanceId(processInstanceId);
        }
        
        // for single process instance load available signals
        if (ids.length == 1 && Long.parseLong(ids[0]) != -1) {
            getAvailableSignals("general", Long.parseLong(ids[0]));
        }
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
    
    public void getAvailableSignals(String sessionId, long processInstanceId) {
        knowledgeServices.call(new RemoteCallback<Collection<String>>() {
            @Override
            public void callback(Collection<String> signals) {
                for(String s: signals){
                    System.out.println("Signal: ");
                }
                view.setAvailableSignals(signals);
                
            }
        }).getAvailableSignals(sessionId, processInstanceId);
    }
}
