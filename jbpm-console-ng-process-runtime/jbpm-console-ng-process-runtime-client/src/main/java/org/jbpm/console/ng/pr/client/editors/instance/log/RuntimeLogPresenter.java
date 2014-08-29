/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.editors.instance.log;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.model.RuntimeLogSummary;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogOrder;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogType;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;

@Dependent
public class RuntimeLogPresenter {

    private String currentProcessInstanceId;

    public interface RuntimeLogView extends IsWidget {

        void init( final RuntimeLogPresenter presenter );

        void displayNotification( final String text );

        HTML getLogTextArea();
    }

    @Inject
    private RuntimeLogView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getWidget() {
        return view;
    }

    public void refreshProcessInstanceData( final LogOrder logOrder,
                                            final LogType logType ) {

        view.getLogTextArea().setText( "" );

        if ( LogType.TECHNICAL.equals( logType ) ) {
            dataServices.call( new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback( List<RuntimeLogSummary> logs ) {
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();

                    if ( logOrder == LogOrder.DESC ) {
                        Collections.reverse( logs );
                    }

                    for ( RuntimeLogSummary rls : logs ) {
                        safeHtmlBuilder.appendEscapedLines( rls.getTime() + ": " + rls.getLogLine() + " - " + rls.getType() + "\n" );
                    }
                    view.getLogTextArea().setHTML( safeHtmlBuilder.toSafeHtml() );
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message,
                                      Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).getAllRuntimeLogs( Long.valueOf( currentProcessInstanceId ) );
        } else {
            dataServices.call( new RemoteCallback<List<RuntimeLogSummary>>() {
                @Override
                public void callback( List<RuntimeLogSummary> logs ) {
                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    if ( logOrder == LogOrder.DESC ) {
                        Collections.reverse( logs );
                    }

                    for ( RuntimeLogSummary rls : logs ) {
                        safeHtmlBuilder.appendEscapedLines( rls.getTime() + ": " + rls.getLogLine() + "\n" );
                    }
                    view.getLogTextArea().setHTML( safeHtmlBuilder.toSafeHtml() );
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message,
                                      Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).getBusinessLogs( Long.valueOf( currentProcessInstanceId ) );
        }
    }

    public void onProcessInstanceSelectionEvent( @Observes final ProcessInstanceSelectionEvent event ) {
        this.currentProcessInstanceId = String.valueOf( event.getProcessInstanceId() );

        refreshProcessInstanceData( LogOrder.ASC, LogType.BUSINESS );
    }

}
