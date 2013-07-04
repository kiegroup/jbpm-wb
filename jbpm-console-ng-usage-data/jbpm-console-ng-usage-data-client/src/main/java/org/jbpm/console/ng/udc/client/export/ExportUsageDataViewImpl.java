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

package org.jbpm.console.ng.udc.client.export;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

@Dependent
@Templated(value = "ExportUsageDataViewImpl.html")
public class ExportUsageDataViewImpl extends Composite implements ExportUsageDataPresenter.ExportUsageDataEventView {
    
    @Inject
    @DataField
    private TextArea textAreaExportCsv;
    
    @Inject
    @DataField
    public ControlLabel exportCsvNameText;
    
    @Inject
    private Event<NotificationEvent> notification;
    
    private ExportUsageDataPresenter presenter;

    @Override
    public void init(ExportUsageDataPresenter presenter) {
        this.presenter = presenter;
        exportCsvNameText.add( new HTMLPanel( "Export Usage Data to CSV" ) );
        setDataCsv();
    }
    
    private void setDataCsv(){
        textAreaExportCsv.setText(presenter.getTxtExportCsv());
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
        
    }
    
}
