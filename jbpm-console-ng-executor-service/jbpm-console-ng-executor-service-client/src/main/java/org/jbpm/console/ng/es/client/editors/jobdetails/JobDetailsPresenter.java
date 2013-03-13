package org.jbpm.console.ng.es.client.editors.jobdetails;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Job Request Details")
public class JobDetailsPresenter {

    public interface JobDetailsView
		extends
		UberView<JobDetailsPresenter> {
    	
    	void setRequest(RequestSummary request, List<ErrorSummary> errors, List<RequestParameterSummary> params);
    }
    @Inject
    JobDetailsView view;
    private Long requestId;
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Job Request Details";
    }

    @WorkbenchPartView
    public UberView<JobDetailsPresenter> getView() {
        return view;
    }

    public JobDetailsPresenter() {
    }

    @PostConstruct
    public void init() {
    }

	@OnStart
    public void onStart( final PlaceRequest place ) {
        this.requestId = Long.valueOf(place.getParameter("requestId", "0"));
        this.executorServices.call(new RemoteCallback<RequestDetails>() {
        	@Override
        	public void callback(RequestDetails response) {
        		view.setRequest(response.getRequest(), response.getErrors(), response.getParams());
        	}
        }).getRequestDetails(Long.valueOf(this.requestId));
    }
}
