/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.gc.client.list.base;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.resources.GenericImages;
import org.jbpm.console.ng.ht.model.GenericSummary;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public abstract class GenericActions<T extends GenericSummary> extends BaseGenericCRUD {

    protected Constants genericConstants = GWT.create(Constants.class);
    protected GenericImages genericImages = GWT.create(GenericImages.class);

    protected String readActionTitle = null;
    protected String deleteActionTitle = null;
    protected String updateActionTitle = null;

    protected ImageResource READ_ACTION_IMAGE = genericImages.detailsIcon();
    protected ImageResource UPDATE_ACTION_IMAGE = genericImages.editGridIcon();
    protected ImageResource DELETE_ACTION_IMAGE = genericImages.deleteGridIcon();

    protected class ReadActionHasCell implements HasCell<T, T> {
        private ActionCell<T> cell;

        public ReadActionHasCell(final String text, ActionCell.Delegate<T> delegate) {
            cell = new ActionCell<T>(text, delegate) {
                @Override
                public void render(Cell.Context context, T value, SafeHtmlBuilder sb) {
                    String title = (readActionTitle != null) ? readActionTitle : text;
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(READ_ACTION_IMAGE);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + title + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };

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

    protected class DeleteActionHasCell implements HasCell<T, T> {
        private ActionCell<T> cell;

        public DeleteActionHasCell(final String text, ActionCell.Delegate<T> delegate) {
            cell = new ActionCell<T>(text, delegate) {
                @Override
                public void render(Cell.Context context, T value, SafeHtmlBuilder sb) {
                    String title = (deleteActionTitle != null) ? deleteActionTitle : text;
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(DELETE_ACTION_IMAGE);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + title + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };

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

    protected class UpdateActionHasCell implements HasCell<T, T> {
        private ActionCell<T> cell;

        public UpdateActionHasCell(final String text, ActionCell.Delegate<T> delegate) {
            cell = new ActionCell<T>(text, delegate) {
                @Override
                public void render(Cell.Context context, T value, SafeHtmlBuilder sb) {
                    String title = (updateActionTitle != null) ? updateActionTitle : text;
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(UPDATE_ACTION_IMAGE);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + title + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };

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

}