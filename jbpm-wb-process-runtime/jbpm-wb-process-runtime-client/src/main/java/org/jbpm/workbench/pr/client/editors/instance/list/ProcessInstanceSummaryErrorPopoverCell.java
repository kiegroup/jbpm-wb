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

package org.jbpm.workbench.pr.client.editors.instance.list;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "ProcessInstanceSummaryErrorPopoverCell.html", stylesheet = "ProcessInstanceSummaryErrorPopoverCell.css")
public class ProcessInstanceSummaryErrorPopoverCell extends AbstractCell<ProcessInstanceSummary> implements IsElement {

    private static final Constants I18N = Constants.INSTANCE;
    private static final String VIEW_ERROR_LINK_NAME = "viewErrAnchor";
    private static final String PROCESS_INSTANCE_ATTRIBUTE = "data-jbpm-processInstanceId";
    private static final String ERROR_PRESENT_STYLE = "error-present";

    @Inject
    private PlaceManager placeManager;
    
    @Inject
    @DataField("popover")
    private Popover popover;
     
    @Inject
    @DataField("popoverContent")
    private Span popoverContent;

    @Inject
    @DataField("contentErrCount")
    private Span contentErrCount;
    
    @Inject
    @DataField(VIEW_ERROR_LINK_NAME)
    private Anchor viewErrAnchor;

    @Override
    public void render(Context context, ProcessInstanceSummary value, SafeHtmlBuilder sb) {
        int errCount = (value != null ? value.getErrorCount() : 0);

        HTMLElement popoverAnchor = popover.getElement();
        popoverAnchor.setTextContent(Integer.toString(errCount));

        if(errCount > 0){
            viewErrAnchor.setTitle(I18N.ErrorCountViewLink());
            viewErrAnchor.setTextContent(I18N.ErrorCountViewLink());
            viewErrAnchor.setAttribute(PROCESS_INSTANCE_ATTRIBUTE, Long.toString(value.getProcessInstanceId()));
            contentErrCount.setTextContent(I18N.ErrorCountNumber(errCount));

            popoverAnchor.setAttribute("data-toggle", "popover");
            popoverAnchor.getClassList().add(ERROR_PRESENT_STYLE);
            Scheduler.get().scheduleDeferred(() -> initPopovers(VIEW_ERROR_LINK_NAME, PROCESS_INSTANCE_ATTRIBUTE));
        }else{
            popoverAnchor.removeAttribute("data-toggle");
            popoverAnchor.getClassList().remove(ERROR_PRESENT_STYLE);
        }
        sb.appendHtmlConstant(popoverAnchor.getOuterHTML());
    }
    
    public void openErrorView(String pidStr){
        Long pid = (pidStr != null ? Long.valueOf(pidStr) : null);
        PlaceRequest request = new DefaultPlaceRequest(PerspectiveIds.EXECUTION_ERRORS);
        if(pid != null){
            //TODO: add process instance ID to PlaceRequest when navigation is implemented. E.g.:
            //request.addParameter(ErrorManagementPerspective.PROCESS_ID, pid);
        }
        placeManager.goTo(request);
    }
    
    public String getPopoverContent(){
        return popoverContent.getInnerHTML();
    }
    
    private native void initPopovers(String linkName, String procIdAttrName) /*-{
        var thisCellRef = this;
        $wnd.jQuery(document).ready(function(){
            $wnd.jQuery("[data-toggle='popover']")
                .popover({
                    content: thisCellRef.@org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceSummaryErrorPopoverCell::getPopoverContent().bind(thisCellRef)
                })
                .off("inserted.bs.popover")
                .on("inserted.bs.popover", function(){
                    $wnd.jQuery("[data-field='" + linkName + "']")
                        .off("click")
                        .on("click", onViewErrorsClick);                
                });
            function onViewErrorsClick(){
                var processInstId = $wnd.jQuery(this).attr(procIdAttrName);
                thisCellRef.@org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceSummaryErrorPopoverCell::openErrorView(Ljava/lang/String;)(processInstId);
            }
        });
    }-*/;

}
