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

package org.jbpm.workbench.common.client.util;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractMultiGridPresenter;
import org.jbpm.workbench.common.model.GenericErrorSummary;
import org.jbpm.workbench.common.client.resources.i18n.Constants;

@Dependent
@Templated(value = "GenericErrorSummaryCountCell.html", stylesheet = "GenericErrorSummaryCountCell.css")
public class GenericErrorSummaryCountCell<C extends GenericErrorSummary> extends AbstractCell<C> implements IsElement {

    private static final String TOOLTIP_NAME = "pisepc-tooltip";
    private static final String DATA_INSTANCE_ATTRIBUTE = "data-jbpm-id";
    private static final String ERROR_PRESENT_STYLE = "error-present";
    private static final String LINK_AVAILABLE_STYLE = "link-available";
    @Inject
    @DataField(TOOLTIP_NAME)
    private Anchor tooltip;
    private AbstractMultiGridPresenter viewPresenter;
    
    public GenericErrorSummaryCountCell init(final AbstractMultiGridPresenter viewPresenter) {
        this.viewPresenter = viewPresenter;
        return this;
    }

    @Override
    public void render(Context context,
                       C value,
                       SafeHtmlBuilder sb) {
        Integer errCount = (value != null && value.getErrorCount() != null ? value.getErrorCount() : 0);

        DOMTokenList tooltipClasses = tooltip.getClassList();
        tooltip.setTextContent(Integer.toString(errCount));

        if (errCount > 0) {
            tooltipClasses.add(ERROR_PRESENT_STYLE);
        } else {
            tooltipClasses.remove(ERROR_PRESENT_STYLE);
        }
        
        if (viewPresenter.getViewErrorsActionCondition().test(value)) {
            tooltip.setTitle(Constants.INSTANCE.ErrorCountNumberView(errCount));
            tooltip.setAttribute(DATA_INSTANCE_ATTRIBUTE,
                                 value.getId().toString());
            tooltipClasses.add(LINK_AVAILABLE_STYLE);

            initTooltipsAsync();

        } else {
            tooltip.removeAttribute("title");
            tooltip.removeAttribute(DATA_INSTANCE_ATTRIBUTE);
            tooltipClasses.remove(LINK_AVAILABLE_STYLE);
        }

        sb.appendHtmlConstant(tooltip.getOuterHTML());
    }

    public void openErrorView(final String id){
        viewPresenter.openErrorView(id);
    }
    
    private void initTooltipsAsync(){
        Scheduler.get().scheduleDeferred(() -> initTooltips(TOOLTIP_NAME,
                                                            LINK_AVAILABLE_STYLE,
                                                            DATA_INSTANCE_ATTRIBUTE));
    }

    private native void initTooltips(String dataAttrVal,
                                     String linkClassName,
                                     String IdAttrName) /*-{
        var thisCellRef = this;
        $wnd.jQuery(document).ready(function () {
            $wnd.jQuery("[data-field='" + dataAttrVal + "']")
                .off("click")
                .tooltip("destroy")
                .filter("." + linkClassName)
                .tooltip()
                .on("click", function () {
                    var Id = $wnd.jQuery(this).attr(IdAttrName);
                    thisCellRef.@org.jbpm.workbench.common.client.util.GenericErrorSummaryCountCell::openErrorView(Ljava/lang/String;)(Id);
                });
        });
    }-*/;
}
