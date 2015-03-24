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

package org.jbpm.console.ng.gc.client.experimental.workbranch.configuration;

import static org.jbpm.console.ng.ga.model.ContextualView.ADVANCED_MODE;
import static org.jbpm.console.ng.ga.model.ContextualView.BASIC_MODE;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.Descriptor.Iterator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.ga.model.ContextualView;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class WorkbranchConfigurationPopup extends BaseModal {
	private static AddNewKBasePopupViewImplBinder uiBinder = GWT
			.create(AddNewKBasePopupViewImplBinder.class);

	@UiField
	ListBox languageListItems;

	@UiField
	ListBox multipleModeItems;

	@Inject
	private PerspectiveManager perspectiveManager;

	@Inject
	private ContextualView contextualView;

	@Inject
	protected PlaceManager placeManager;

	private Constants constants = GWT.create(Constants.class);
	private Map<String, String> languageMap = new HashMap<String, String>();

	interface AddNewKBasePopupViewImplBinder extends
			UiBinder<Widget, WorkbranchConfigurationPopup> {

	}

	public WorkbranchConfigurationPopup() {
		setTitle(constants.Workbranch_Settings());

		add(uiBinder.createAndBindUi(this));
		add(new ModalFooterOKCancelButtons(new Command() {
			@Override
			public void execute() {
				onOk();
				hide();
			}
		}, new Command() {
			@Override
			public void execute() {
				hide();
			}
		}));
		languageMap.put("default", constants.English());
		languageMap.put("zh_CN", constants.Chinese());
		languageMap.put("de", constants.German());
		languageMap.put("es", constants.Spanish());
		languageMap.put("fr", constants.French());
		languageMap.put("ja", constants.Japanese());
		languageMap.put("pt_BR", constants.Portuguese());
		setLanguageListItems(getAllSupportLanguage());
		setMultipleModeItems(getAllMutipleModes());

	}

	public void onOk() {

		final Node<String, String> selectedLanguageItem = getSelectedLanguageListItem();
		final Node<String, String> selectedMutipleMode = getSelectedMultipleModeItem();
		boolean refrashPerspectiveFlag = true;
		boolean refrashWorkbranchFlag = true;
		if (selectedLanguageItem.getValue().equals(
				LocaleInfo.getCurrentLocale().getLocaleName())) {
			refrashWorkbranchFlag = false;
		}
		if (selectedMutipleMode.getValue().equals(
				contextualView.getModeName())) {
			refrashPerspectiveFlag = false;
		}
		switchMode(refrashPerspectiveFlag, refrashWorkbranchFlag);
		refrashWorkbranch(selectedLanguageItem, refrashWorkbranchFlag);

	}

	/**
	 * language function
	 *
	 **/

	private void setLanguageListItems(List<Node<String, String>> items) {
		languageListItems.clear();
		for (Node<String, String> item : items) {
			languageListItems.addItem(item.getKey(), item.getValue());
		}
	}

	private Node<String, String> getSelectedLanguageListItem() {
		final int selectedIndex = languageListItems.getSelectedIndex();
		if (selectedIndex == -1) {
			Node<String, String> entry = new Node<String, String>("", "");
			return entry;
		}
		final String text = languageListItems.getItemText(selectedIndex);
		final String value = languageListItems.getValue(selectedIndex);
		Node<String, String> entry = new Node<String, String>(text, value);
		return entry;
	}

	private List<Node<String, String>> getAllSupportLanguage() {
		String[] languages = LocaleInfo.getAvailableLocaleNames();
		List<Node<String, String>> allSupportLanguage = new ArrayList<Node<String, String>>(
				languages.length);
		for (String language : languages) {
			Node<String, String> entry = new Node<String, String>(
					languageMap.get(language), language);
			allSupportLanguage.add(entry);
		}
		return allSupportLanguage;

	}

	private void setCurrentLanguage(String languageName) {
		Window.Location.assign(Window.Location
				.createUrlBuilder()
				.removeParameter(LocaleInfo.getLocaleQueryParam())
				.setParameter(
						LocaleInfo.getCurrentLocale().getLocaleQueryParam(),
						languageName).buildString());
	}


	private void refrashWorkbranch(
			final Node<String, String> selectedLanguageItem,
			boolean refrashWorkbranchFlag) {
		if (refrashWorkbranchFlag == false) {
			return;
		}
		if (selectedLanguageItem != null
				&& !selectedLanguageItem.getValue().isEmpty()) {
			setCurrentLanguage(selectedLanguageItem.getValue());
		} else {
			showFieldEmptyWarning();
		}
	}
	/**
	 * Multiple view mode function
	 **/
	
	private List<Node<String, String>> getAllMutipleModes() {
		List<Node<String, String>> allMultipleMode = new ArrayList<Node<String, String>>(
				2);
		allMultipleMode.add(new Node<String, String>(constants.Advanced(),
				contextualView.ADVANCED_MODE));
		allMultipleMode.add(new Node<String, String>(constants.Basic(),
				contextualView.BASIC_MODE));
		return allMultipleMode;

	}

	private void setMultipleModeItems(List<Node<String, String>> items) {
		multipleModeItems.clear();
		for (Node<String, String> item : items) {
			multipleModeItems.addItem(item.getKey(), item.getValue());
		}

	}

	private Node<String, String> getSelectedMultipleModeItem() {
		final int selectedIndex = multipleModeItems.getSelectedIndex();
		if (selectedIndex == -1) {
			return new Node<String, String>("", "");
		}
		final String text = multipleModeItems.getItemText(selectedIndex);
		final String value = multipleModeItems.getValue(selectedIndex);
		return new Node<String, String>(text, value);
	}

	private void switchMode(boolean refrashPerspectiveFlag,
			boolean refrashWorkbranchFlag) {
		if (refrashPerspectiveFlag && !refrashWorkbranchFlag) {
			String modeName = contextualView.getModeName();
			if (modeName.equals(BASIC_MODE)) {
				contextualView.setModeName(ADVANCED_MODE);
			} else {
				contextualView.setModeName(BASIC_MODE);
			}
			refrashPerspective();
		}
	}

	private void refrashPerspective() {
		final PerspectiveActivity currentPerspective = perspectiveManager
				.getCurrentPerspective();
		perspectiveManager
				.removePerspectiveStates(new org.uberfire.mvp.Command() {
					@Override
					public void execute() {
						if (currentPerspective != null) {
							final PlaceRequest pr = new ForcedPlaceRequest(
									currentPerspective.getIdentifier(),
									currentPerspective.getPlace()
											.getParameters());
							placeManager.goTo(pr);
						}
					}
				});
	}

	private void showFieldEmptyWarning() {
		ErrorPopup.showMessage(constants.PleaseSetAName());
	}

	class Node<K, V> {
		K key;
		V value;

		public Node(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
	}
}
