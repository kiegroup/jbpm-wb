/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.es.client.editors.jobdetails;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Job Request Details")
public class JobDetailsPresenter {

    public interface JobDetailsView extends UberView<JobDetailsPresenter> {

        void setRequest( RequestSummary request,
                         List<ErrorSummary> errors,
                         List<RequestParameterSummary> params );
        void refreshTable();
    }

    private Long requestId;
    private PlaceRequest place;

    @Inject
    JobDetailsView view;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;

    public JobDetailsPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Job Request Details";
    }

    @WorkbenchPartView
    public UberView<JobDetailsPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        this.requestId = Long.valueOf( place.getParameter( "requestId", "0" ) );
        Modal modal = null;
        Widget parent = ((JobDetailsViewImpl)view).getParent();
        while (parent != null) {
            if (parent instanceof Modal) {
                modal = (Modal)parent;
                break;
            } else {
                parent = parent.getParent();
            }
        }
        if (modal != null) {
            modal.addShownHandler( new ShownHandler() {
                @Override
                public void onShown(ShownEvent shownEvent) {
                    view.refreshTable();
                }
            });
        }
        this.executorServices.call( new RemoteCallback<RequestDetails>() {
            @Override
            public void callback( RequestDetails response ) {
                view.setRequest( response.getRequest(), response.getErrors(), response.getParams() );
            }
        } ).getRequestDetails( Long.valueOf( this.requestId ) );
    }

}
