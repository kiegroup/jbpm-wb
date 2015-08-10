/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.pr.client.editors.definition.details;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class BaseProcessDefDetailsViewImpl extends Composite implements
                                                                      BaseProcessDefDetailsPresenter.BaseProcessDefDetailsView {

    @Inject
    @DataField
    protected HTML processIdText;

    @Inject
    @DataField
    protected HTML processNameText;

    @Inject
    @DataField
    protected HTML deploymentIdText;

    @Inject
    @DataField
    protected Label processNameLabel;

    @Inject
    @DataField
    protected Label processIdLabel;

    @Inject
    @DataField
    protected Label deploymentIdLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Path processAssetPath;

    private String encodedProcessSource;

    @PostConstruct
    public void initView() {
        init();
    }

    public abstract void init();

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );

    }

    @Override
    public HTML getProcessNameText() {
        return this.processNameText;
    }

    @Override
    public HTML getDeploymentIdText() {
        return this.deploymentIdText;
    }

    @Override
    public void setProcessAssetPath( Path processAssetPath ) {
        this.processAssetPath = processAssetPath;
    }

    @Override
    public void setEncodedProcessSource( String encodedProcessSource ) {
        this.encodedProcessSource = encodedProcessSource;
    }

    @Override
    public HTML getProcessIdText() {
        return processIdText;
    }

    public Path getProcessAssetPath() {
        return processAssetPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }
}
