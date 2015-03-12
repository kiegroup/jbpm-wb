package org.jbpm.console.ng.gc.client.experimental.viewmode;

import static org.jbpm.console.ng.ga.model.ContextualSwtichMode.ADVANCED_MODE;
import static org.jbpm.console.ng.ga.model.ContextualSwtichMode.BASIC_MODE;

import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.ga.model.ContextualSwtichMode;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public abstract class AbastractMultipeViewModePerspective {

	@Inject
	private PerspectiveManager perspectiveManager;

	@Inject
	private ContextualSwtichMode contextualSwtichMode;

	@Inject
	protected PlaceManager placeManager;

	private Constants constants = GWT.create(Constants.class);

	protected Menus getMenus() {
		String switchInformation = constants.Switch(constants.Basic());
		if (contextualSwtichMode.getModeName().equals(BASIC_MODE)) {
			switchInformation = constants.Switch(constants.Advanced());
		}
		return MenuFactory.newTopLevelMenu(constants.View_Mode()).menus()
				.menu(switchInformation).respondsWith(new Command() {
					@Override
					public void execute() {
						switchMode();
					}

				}).endMenu().endMenus().endMenu().build();
	}

	private void switchMode() {
		String information = constants.SwitchModeOfPerspectives(constants
				.Basic());
		if (contextualSwtichMode.getModeName().equals(BASIC_MODE)) {
			information = constants.SwitchModeOfPerspectives(constants
					.Advanced());
		}

		if (Window.confirm(information)) {
			final PerspectiveActivity currentPerspective = perspectiveManager
					.getCurrentPerspective();
			perspectiveManager.removePerspectiveStates(new Command() {
				@Override
				public void execute() {
					if (currentPerspective != null) {
						String modeName = contextualSwtichMode.getModeName();
						if (modeName.equals(BASIC_MODE)) {
							modeName = ADVANCED_MODE;
							contextualSwtichMode.setModeName(modeName);
						} else {
							modeName = BASIC_MODE;
							contextualSwtichMode.setModeName(modeName);
						}
						final PlaceRequest pr = new ForcedPlaceRequest(
								currentPerspective.getIdentifier(),
								currentPerspective.getPlace().getParameters());
						placeManager.goTo(pr);
					}
				}
			});
		}
	}
}
