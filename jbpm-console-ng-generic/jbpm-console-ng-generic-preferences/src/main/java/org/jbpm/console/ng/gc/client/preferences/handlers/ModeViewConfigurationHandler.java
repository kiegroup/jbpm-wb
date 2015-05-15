package org.jbpm.console.ng.gc.client.preferences.handlers;

import static org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView.ADVANCED_MODE;
import static org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView.BASIC_MODE;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.preferences.i18n.Constants;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationHandler;
import org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferences;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;

import com.google.gwt.core.shared.GWT;

@ApplicationScoped
public class ModeViewConfigurationHandler extends WorkbenchConfigurationHandler {

    private final static String ALL_PERSPECTIVES_HADNLER_ID = "all_perspectives";

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private ContextualView contextualView;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    ConfigurationComboBoxItemWidget allPrespectivesModeViewItem;

    private Map<String, String> viewModeMap = new HashMap<String, String>();

    @Override
    protected void initHandler() {
        allPrespectivesModeViewItem.getExtensionItemLabel().setText( constants.View_Mode() );
        allPrespectivesModeViewItem.clear();
        allPrespectivesModeViewItem.getExtensionItem().addItem( Pair.newPair( viewModeMap.get( BASIC_MODE ),
                BASIC_MODE ) );
        allPrespectivesModeViewItem.getExtensionItem().addItem( Pair.newPair( viewModeMap.get( ADVANCED_MODE ),
                ADVANCED_MODE ) );

        super.getExtensions().add( Pair.newPair( ALL_PERSPECTIVES_HADNLER_ID, allPrespectivesModeViewItem ) );
    }

    public ModeViewConfigurationHandler() {
        viewModeMap.put( ADVANCED_MODE, constants.Advanced() );
        viewModeMap.put( BASIC_MODE, constants.Basic() );
    }

    @Override
    public String getDescription() {
        return constants.View_Mode_Selector();
    }

    @Override
    public void configurationSetting( boolean isInit ) {
        boolean refreshPerspectiveFlag = true;
        if ( allPrespectivesModeViewItem.getSelectedItem().getK2() != null ) {
            if ( allPrespectivesModeViewItem.getSelectedItem().getK2().equals( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ) ) ) {
                refreshPerspectiveFlag = false;
            }
        }
        switchMode( refreshPerspectiveFlag );
    }

    @Override
    protected void setDefaultConfigurationValues( final UserWorkbenchPreferences response ) {
        allPrespectivesModeViewItem.getExtensionItem().setSelectItemByText( response.getViewMode( ContextualView.ALL_PERSPECTIVES ) );
    }

    private void switchMode( boolean refreshPerspectiveFlag ) {
        if ( refreshPerspectiveFlag ) {
            String modeName = contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES );
            if ( modeName.equals( BASIC_MODE ) ) {
                contextualView.setViewMode( ContextualView.ALL_PERSPECTIVES, ADVANCED_MODE );
            } else {
                contextualView.setViewMode( ContextualView.ALL_PERSPECTIVES, BASIC_MODE );
            }
            refreshPerspective();
        }
    }

    private void refreshPerspective() {
        final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
        perspectiveManager.removePerspectiveStates( new org.uberfire.mvp.Command() {

            @Override
            public void execute() {
                if ( currentPerspective != null ) {
                    final PlaceRequest pr = new ForcedPlaceRequest( currentPerspective.getIdentifier(),
                            currentPerspective.getPlace().getParameters() );
                    placeManager.goTo( pr );
                }
            }
        } );
    }

    @Override
    protected UserWorkbenchPreferences getSelectedUserWorkbenchPreferences() {
        UserWorkbenchPreferences preference = super.getPreference();
        if ( preference != null ) {
            preference.setViewMode( ContextualView.ALL_PERSPECTIVES, allPrespectivesModeViewItem.getSelectedItem().getK2() );
            return preference;
        }
        preference = new UserWorkbenchPreferences( "default" );
        preference.setViewMode( ContextualView.ALL_PERSPECTIVES, allPrespectivesModeViewItem.getSelectedItem().getK2() );
        return preference;
    }
}