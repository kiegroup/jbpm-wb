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

package org.jbpm.console.ng.dm.client.experimental.pagination;

import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.jbpm.console.ng.dm.client.resources.DocumentsImages;
import org.jbpm.console.ng.dm.model.CMSContentSummary;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;

public abstract class ActionsCellDocuments extends Composite {

	private DocumentsImages images = GWT.create(DocumentsImages.class);

	private Constants constants = GWT.create(Constants.class);
	
	protected class RemoveHasCell implements
			HasCell<CMSContentSummary, CMSContentSummary> {
		private ActionCell<CMSContentSummary> cell;

		public RemoveHasCell(String text,
				ActionCell.Delegate<CMSContentSummary> delegate) {
			cell = new ActionCell<CMSContentSummary>(text, delegate) {
				@Override
				public void render(Cell.Context context,
						CMSContentSummary value, SafeHtmlBuilder sb) {
					ImageResource detailsIcon = images.removeIcon();
					AbstractImagePrototype imageProto = AbstractImagePrototype
							.create(detailsIcon);
					SafeHtmlBuilder mysb = new SafeHtmlBuilder();
					mysb.appendHtmlConstant("<span title='" + constants.Remove()
							+ "' style='margin-right:5px;'>"); // TODO add
																// constants
					mysb.append(imageProto.getSafeHtml());
					mysb.appendHtmlConstant("</span>");
					sb.append(mysb.toSafeHtml());
				}
			};

		}

		@Override
		public Cell<CMSContentSummary> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CMSContentSummary, CMSContentSummary> getFieldUpdater() {
			return null;
		}

		@Override
		public CMSContentSummary getValue(CMSContentSummary object) {
			return object;
		}
	}

	protected class GoHasCell implements
			HasCell<CMSContentSummary, CMSContentSummary> {
		private ActionCell<CMSContentSummary> cell;

		public GoHasCell(String text,
				ActionCell.Delegate<CMSContentSummary> delegate) {
			cell = new ActionCell<CMSContentSummary>(text, delegate) {
				@Override
				public void render(Cell.Context context,
						CMSContentSummary value, SafeHtmlBuilder sb) {
					ImageResource detailsIcon = images.goIcon();
					AbstractImagePrototype imageProto = AbstractImagePrototype
							.create(detailsIcon);
					SafeHtmlBuilder mysb = new SafeHtmlBuilder();
					mysb.appendHtmlConstant("<span title='" + constants.Go()
							+ "' style='margin-right:5px;'>");
					mysb.append(imageProto.getSafeHtml());
					mysb.appendHtmlConstant("</span>");
					sb.append(mysb.toSafeHtml());
				}
			};

		}

		@Override
		public Cell<CMSContentSummary> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CMSContentSummary, CMSContentSummary> getFieldUpdater() {
			return null;
		}

		@Override
		public CMSContentSummary getValue(CMSContentSummary object) {
			return object;
		}
	}
}
