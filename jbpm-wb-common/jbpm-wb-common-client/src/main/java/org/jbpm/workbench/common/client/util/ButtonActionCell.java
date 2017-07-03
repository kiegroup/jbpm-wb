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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

public class ButtonActionCell<T> implements HasCell<T, T> {

    private ActionCell<T> cell;

    private String text;

    public ButtonActionCell(final ActionCell.Delegate<T> delegate) {
        this("",
             delegate);
    }

    public ButtonActionCell(final String text,
                            final ActionCell.Delegate<T> delegate) {
        this.text = text;
        this.cell = new ActionCell<T>(text,
                                      delegate) {
            @Override
            public void render(final Context context,
                               final T value,
                               final SafeHtmlBuilder sb) {
                ButtonActionCell.this.render(context,
                                             value,
                                             sb);
            }
        };
    }

    public void render(final Cell.Context context,
                       final T value,
                       final SafeHtmlBuilder sb) {
        final SafeHtmlBuilder mysb = new SafeHtmlBuilder();
        final Button btn = GWT.create(Button.class);
        btn.setText(getText(value));
        btn.setTitle(getText(value));
        btn.setType(ButtonType.DEFAULT);
        btn.setSize(ButtonSize.SMALL);
        btn.getElement().getStyle().setMarginRight(5,
                                                   Style.Unit.PX);
        mysb.appendHtmlConstant(btn.getElement().getString());
        sb.append(mysb.toSafeHtml());
    }

    public String getText(final T value) {
        return text;
    }

    @Override
    public Cell<T> getCell() {
        return cell;
    }

    @Override
    public FieldUpdater<T, T> getFieldUpdater() {
        return null;
    }

    @Override
    public T getValue(T object) {
        return object;
    }
}