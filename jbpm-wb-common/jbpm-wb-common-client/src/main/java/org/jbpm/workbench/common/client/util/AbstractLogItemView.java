package org.jbpm.workbench.common.client.util;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;

public abstract class AbstractLogItemView<T> extends AbstractView<T> {

    @Inject
    @DataField("list-group-item")
    private Div listGroupItem;

    @Inject
    @DataField("logIcon")
    protected Span logIcon;

    @Inject
    @DataField("logTime")
    protected Span logTime;

    @Inject
    @DataField("logTypeDesc")
    protected Span logTypeDesc;

    @Inject
    @DataField("logInfo")
    protected Span logInfo;

    @PostConstruct
    public void init() {
        tooltip(logIcon);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }
}
