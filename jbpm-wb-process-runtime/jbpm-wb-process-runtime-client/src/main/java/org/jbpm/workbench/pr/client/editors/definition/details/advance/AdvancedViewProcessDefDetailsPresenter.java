/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.definition.details.advance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.TaskDefSummary;
import org.jbpm.workbench.pr.client.editors.definition.details.BaseProcessDefDetailsPresenter;
import org.jbpm.workbench.pr.client.i18n.Constants;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;


@Dependent
public class AdvancedViewProcessDefDetailsPresenter extends
        BaseProcessDefDetailsPresenter {

    public interface AdvancedProcessDefDetailsView extends
            BaseProcessDefDetailsPresenter.BaseProcessDefDetailsView {

        HTML getNroOfHumanTasksText();

        HTML getHumanTasksListBox();

        HTML getUsersGroupsListBox();

        HTML getProcessDataListBox();

        HTML getProcessServicesListBox();

        HTML getSubprocessListBox();
    }

    private Constants constants = Constants.INSTANCE;

    @Inject
    private AdvancedProcessDefDetailsView view;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @Override
    public IsWidget getWidget() {
        return view;
    }

    @Override
    protected void refreshView( String serverTemplateId, String processId, String deploymentId ) {
        view.getProcessIdText().setText( processId );
        view.getDeploymentIdText().setText( deploymentId );
    }

    private void refreshServiceTasks(  Map<String, String> services ) {

        view.getProcessServicesListBox().setText("");
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (services.keySet().isEmpty()) {
            safeHtmlBuilder.appendEscaped(constants.NoServicesRequiredForThisProcess());
            view.getProcessServicesListBox().setStyleName( "muted" );
            view.getProcessServicesListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        } else {
            for (String key : services.keySet()) {
                safeHtmlBuilder.appendEscapedLines( key + " - "
                        + services.get( key ) + "\n" );
            }
            view.getProcessServicesListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        }
    }


    private void refreshProcessItems( ProcessSummary process ) {
        if (process != null) {

            view.getProcessNameText().setText( process.getName() );
            changeStyleRow( process.getName(), process.getVersion() );
        } else {
            // set to null to ensure it's clear state
            view.setEncodedProcessSource( null );
            view.setProcessAssetPath( null );
        }
    }

    private void refreshReusableSubProcesses(Collection<String> subprocesses ) {
        view.getSubprocessListBox().setText( "" );
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (subprocesses.isEmpty()) {
            safeHtmlBuilder.appendEscapedLines(constants.NoSubprocessesRequiredByThisProcess());
            view.getSubprocessListBox().setStyleName( "muted" );
            view.getSubprocessListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        } else {
            for (String key : subprocesses) {
                safeHtmlBuilder.appendEscapedLines( key + "\n" );
            }
            view.getSubprocessListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        }
    }

    private void refreshRequiredInputData( Map<String, String> inputs ) {
        view.getProcessDataListBox().setText( "" );
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (inputs.keySet().isEmpty()) {
            safeHtmlBuilder.appendEscapedLines(constants.NoProcessVariablesDefinedForThisProcess());
            view.getProcessDataListBox().setStyleName( "muted" );
            view.getProcessDataListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        } else {
            for (String key : inputs.keySet()) {
                safeHtmlBuilder.appendEscapedLines( key + " - "
                        + inputs.get( key ) + "\n" );
            }
            view.getProcessDataListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        }
    }


    private void refreshAssociatedEntities( Map<String, String[]> entities ) {
        view.getUsersGroupsListBox().setText( "" );
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        if (entities.keySet().isEmpty()) {
            safeHtmlBuilder
                    .appendEscapedLines(constants.NoUserOrGroupUsedInThisProcess());
            view.getUsersGroupsListBox().setStyleName( "muted" );
            view.getUsersGroupsListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        } else {
            for (String key : entities.keySet()) {
                StringBuffer names = new StringBuffer();
                String[] entityNames = entities.get( key );
                if (entityNames != null) {
                    for (String entity : entityNames) {
                        names.append( "'" + entity + "' " );
                    }
                }
                safeHtmlBuilder.appendEscapedLines( names
                        + " - " + key + "\n" );
            }
            view.getUsersGroupsListBox().setHTML(
                    safeHtmlBuilder.toSafeHtml() );
        }
    }

    private void refreshTaskDef( final String serverTemplateId, final String deploymentId, final String processId ) {
        view.getNroOfHumanTasksText().setText( "" );
        view.getHumanTasksListBox().setText( "" );

        processRuntimeDataService.call(new RemoteCallback<List<TaskDefSummary>>() {

            @Override
            public void callback(final List<TaskDefSummary> userTaskSummaries) {
                view.getNroOfHumanTasksText().setText( String.valueOf(userTaskSummaries.size()) );

                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                if (userTaskSummaries.isEmpty()) {
                    safeHtmlBuilder.appendEscapedLines(constants.NoUserTasksDefinedInThisProcess());
                    view.getHumanTasksListBox().setStyleName( "muted" );
                    view.getHumanTasksListBox().setHTML(
                            safeHtmlBuilder.toSafeHtml() );
                } else {
                    for (TaskDefSummary t : userTaskSummaries) {
                        safeHtmlBuilder.appendEscapedLines( t.getName() + "\n" );
                    }
                    view.getHumanTasksListBox().setHTML(
                            safeHtmlBuilder.toSafeHtml() );
                }
            }

        } ).getProcessUserTasks(serverTemplateId, deploymentId, processId);

    }

    @Override
    protected void refreshProcessDef( final String serverTemplateId, final String deploymentId, final String processId ) {

        processRuntimeDataService.call(new RemoteCallback<ProcessSummary>() {

            @Override
            public void callback( ProcessSummary process ) {
                if (process != null) {

                    refreshTaskDef( serverTemplateId, deploymentId, processId );

                    refreshAssociatedEntities( process.getAssociatedEntities() );

                    refreshRequiredInputData( process.getProcessVariables() );

                    refreshReusableSubProcesses( process.getReusableSubProcesses() );

                    refreshProcessItems( process );

                    refreshServiceTasks( process.getServiceTasks() );

                } else {
                    // set to null to ensure it's clear state
                    view.setEncodedProcessSource( null );
                    view.setProcessAssetPath( null );
                }
            }
        } ).getProcess(serverTemplateId, new ProcessDefinitionKey(serverTemplateId, deploymentId, processId));

    }
}
