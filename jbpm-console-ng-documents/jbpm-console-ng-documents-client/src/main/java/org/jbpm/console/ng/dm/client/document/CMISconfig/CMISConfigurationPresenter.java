/*
 * Copyright 2012 JBoss Inc
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

package org.jbpm.console.ng.dm.client.document.CMISconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "CMIS Configuration")
public class CMISConfigurationPresenter {

	private PlaceRequest place;

	public interface CMISConfigurationView extends
			UberView<CMISConfigurationPresenter> {

		TextBox getWSACLTextBox();
		
		TextBox getWSDiscoveryTextBox();
		
		TextBox getWSMultifilingTextBox();
		
		TextBox getWSNavigationTextBox();
		
		TextBox getWSObjectTextBox();
		
		TextBox getWSPolicyTextBox();
		
		TextBox getWSRelationshipTextBox();
		
		TextBox getWSRepositoryTextBox();
		
		TextBox getWSVersioningTextBox();
		
		TextBox getRepositoryIDTextBox();
				
		TextBox getUserTextBox();
		
		TextBox getPasswordTextBox();

		void displayNotification(String text);

		void displayNotification(String text,NotificationType type);

		Button getConfigureButton();
		
		Button getTestButton();

	}

	private Menus menus;

	@Inject
	private ErrorPopupPresenter errorPopup;

	@Inject
	private PlaceManager placeManager;

	private Constants constants = GWT.create(Constants.class);

	@Inject
	private CMISConfigurationView view;

	@Inject
	private Caller<DocumentServiceEntryPoint> dataServices;

	private Map<String, String> configurationParameters;

	public CMISConfigurationPresenter() {
		makeMenuBar();
	}

	@DefaultPosition
	public Position getPosition() {
		return CompassPosition.EAST;
	}

	@OnStartup
	public void onStartup(final PlaceRequest place) {
		this.place = place;
		
		configurationParameters = new HashMap<String, String>();
	}

	@WorkbenchPartTitle
	public String getTitle() {
		return constants.ConfigurationPanel();
	}

	@WorkbenchPartView
	public UberView<CMISConfigurationPresenter> getView() {
		return view;
	}

	private void changeStyleRow(String processDefName, String processDefVersion) {
		// processDefStyleEvent.fire( new ProcessDefStyleEvent( processDefName,
		// processDefVersion ) );
	}

	public void refreshConfigurationParameters() {
		dataServices.call(new RemoteCallback<Map<String,String>>() {
			@Override
			public void callback(Map<String,String> parameters) {
				view.getWSACLTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_ACL_SERVICE));
				view.getWSDiscoveryTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE));
				view.getWSMultifilingTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE));
				view.getWSNavigationTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE));
				view.getWSObjectTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_OBJECT_SERVICE));
				view.getWSPolicyTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_POLICY_SERVICE));
				view.getWSRelationshipTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE));
				view.getWSRepositoryTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE));
				view.getWSVersioningTextBox().setText(parameters.get(SessionParameter.WEBSERVICES_VERSIONING_SERVICE));
				view.getRepositoryIDTextBox().setText(parameters.get(SessionParameter.REPOSITORY_ID));
				view.getUserTextBox().setText(parameters.get(SessionParameter.USER));
				view.getPasswordTextBox().setText(parameters.get(SessionParameter.PASSWORD));
			}
		}, new ErrorCallback<Message>() {
			@Override
			public boolean error(Message message, Throwable throwable) {
				org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup
						.showMessage("Unexpected error encountered : "
								+ throwable.getMessage());
				return true;
			}
		}).getConfigurationParameters();
	}

	@OnOpen
	public void onOpen() {
		WorkbenchSplitLayoutPanel splitPanel = (WorkbenchSplitLayoutPanel) view
				.asWidget().getParent().getParent().getParent().getParent()
				.getParent().getParent().getParent().getParent().getParent()
				.getParent().getParent();
		splitPanel.setWidgetMinSize(splitPanel.getWidget(0), 500);
		refreshConfigurationParameters();
	}

	public void configureParameters() {
		
		HashMap<String,String> parameters = new HashMap<String, String>();
		parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, view.getWSACLTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE,view.getWSDiscoveryTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE,view.getWSMultifilingTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE,view.getWSNavigationTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE,view.getWSObjectTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE,view.getWSPolicyTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE,view.getWSRelationshipTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE,view.getWSRepositoryTextBox().getText());
		parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE,view.getWSVersioningTextBox().getText());
		parameters.put(SessionParameter.REPOSITORY_ID,view.getRepositoryIDTextBox().getText());
		parameters.put(SessionParameter.USER,view.getUserTextBox().getText());
		parameters.put(SessionParameter.PASSWORD,view.getPasswordTextBox().getText());
				
		dataServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Updated", NotificationType.SUCCESS);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
				errorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).setConfigurationParameters(parameters);
	}
	
	public void testConnection(){
		dataServices.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result){
                	view.displayNotification("Connection Successfull", NotificationType.SUCCESS);
                } else {
                	view.displayNotification("Connection Failed", NotificationType.ERROR);
                }
                
                	
            	
            	
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
				errorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).testConnection();
		
	}

	@WorkbenchMenu
	public Menus getMenus() {
		return menus;
	}

	private void makeMenuBar() {
		menus = MenuFactory.newTopLevelMenu(constants.Refresh())
				.respondsWith(new Command() {
					@Override
					public void execute() {
						refreshConfigurationParameters();
						view.displayNotification("Refresh complete.");
					}
				}).endMenu().build();

	}

	private List<MenuItem> getOptions() {
		return null;
	}

}
