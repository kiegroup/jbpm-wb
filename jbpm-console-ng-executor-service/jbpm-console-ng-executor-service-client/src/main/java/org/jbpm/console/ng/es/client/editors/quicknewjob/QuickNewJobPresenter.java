package org.jbpm.console.ng.es.client.editors.quicknewjob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.events.RequestCreatedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.Focusable;

@Dependent
@WorkbenchPopup(identifier = "Quick New Job")
public class QuickNewJobPresenter {

    public interface QuickNewJobView
   			extends
   			UberView<QuickNewJobPresenter> {

    	Focusable getJobNameText();
    	
    	void removeRow(RequestParameterSummary parameter);
    	
    	void addRow(RequestParameterSummary parameter);
    	
        void displayNotification(String notification);
    }
    @Inject
    QuickNewJobView view;
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    @Inject
    private Event<RequestCreatedEvent> requestCreatedEvent;
    private PlaceRequest place;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Quick New Job";
    }

    @WorkbenchPartView
    public UberView<QuickNewJobPresenter> getView() {
        return view;
    }

    public QuickNewJobPresenter() {
    }

    @PostConstruct
    public void init() {
    }

	@OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    public void removeParameter(RequestParameterSummary parameter) {
    	view.removeRow(parameter);
    }
    
    public void addNewParameter() {
    	view.addRow(new RequestParameterSummary("click to edit", "click to edit"));
    }

	public void createJob(String jobName, Date dueDate, String jobType,
			Integer numberOfTries, List<RequestParameterSummary> parameters) {
		
        Map<String, String> ctx = new HashMap<String, String>();
        if (parameters != null) {
        	for (RequestParameterSummary param : parameters) {
        		ctx.put(param.getKey(), param.getValue());
        	}
        }
        ctx.put("retries", String.valueOf(numberOfTries)); //TODO make legacy keys hard to repeat by accident
        ctx.put("jobName", jobName); //TODO make legacy keys hard to repeat by accident 
        
        executorServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long requestId) {
                view.displayNotification("Request Schedulled: " + requestId);
                requestCreatedEvent.fire(new RequestCreatedEvent(requestId));
                close();
            }
        }).scheduleRequest(jobType, dueDate, ctx);
        
	}

    @OnReveal
    public void onReveal() {
        view.getJobNameText().setFocus(true);
    }
    
	public void close() {
		closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
	}
}
