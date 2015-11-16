package org.jbpm.console.ng.gc.client.list.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Created by Cristiano Nicolai.
 */
public class RefreshSelectorMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private AbstractListPresenter presenter;

    private final DropDownMenu menuDropDownMenu = new DropDownMenu();
    private final Button menuButton = GWT.create(Button.class);;

    public RefreshSelectorMenuBuilder( final AbstractListPresenter presenter ) {
        this.presenter = presenter;
        setupMenuButton();
        setupMenuDropDown();
    }

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                final ButtonGroup buttonGroup = new ButtonGroup();
                buttonGroup.add( menuButton );
                buttonGroup.add( menuDropDownMenu );
                return buttonGroup;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void setEnabled( boolean enabled ) {

            }

            @Override
            public String getSignatureId() {
                return "org.jbpm.console.ng.gc.client.list.base.RefreshSelectorMenuBuilder#refresh";
            }

        };
    }

    protected void setupMenuButton() {
        menuButton.setDataToggle( Toggle.DROPDOWN );
        menuButton.setIcon( IconType.COG );
        menuButton.setTitle( Constants.INSTANCE.AutoRefresh() );
        menuButton.setSize( ButtonSize.SMALL );
    }

    protected void setupMenuDropDown() {
        menuDropDownMenu.setPull( Pull.RIGHT );
    }

    protected AnchorListItem createTimeSelector( int time,
                                                 String name,
                                                 int configuredSeconds,
                                                 final AnchorListItem refreshDisableButton ) {
        final AnchorListItem oneMinuteRadioButton = new AnchorListItem( name );
        oneMinuteRadioButton.setIconFixedWidth( true );
        final int selectedRefreshTime = time;
        if ( configuredSeconds == selectedRefreshTime ) {
            oneMinuteRadioButton.setIcon( IconType.CHECK );
        }

        oneMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                oneMinuteRadioButton.setIcon( IconType.CHECK );
                presenter.updateRefreshInterval( true, selectedRefreshTime );
                refreshDisableButton.setActive( false );
                refreshDisableButton.setEnabled( true );
                refreshDisableButton.setText( Constants.INSTANCE.Disable_autorefresh() );

            }
        } );

        return oneMinuteRadioButton;
    }

    public void loadOptions( int configuredSeconds ) {
        final AnchorListItem resetButton = new AnchorListItem( Constants.INSTANCE.Disable_autorefresh() );

        if ( configuredSeconds > 10 ) {
            presenter.updateRefreshInterval( true, configuredSeconds );
            resetButton.setEnabled( true );
        } else {
            presenter.updateRefreshInterval( false, 0 );
            resetButton.setEnabled( false );
            resetButton.setText( Constants.INSTANCE.Autorefresh_Disabled() );
        }

        final AnchorListItem oneMinuteRadioButton = createTimeSelector( 60, "1 " + Constants.INSTANCE.Minute(), configuredSeconds, resetButton );
        final AnchorListItem fiveMinuteRadioButton = createTimeSelector( 300, "5 " + Constants.INSTANCE.Minutes(), configuredSeconds, resetButton );
        final AnchorListItem tenMinuteRadioButton = createTimeSelector( 600, "10 " + Constants.INSTANCE.Minutes(), configuredSeconds, resetButton );

        menuDropDownMenu.add( oneMinuteRadioButton );
        menuDropDownMenu.add( fiveMinuteRadioButton );
        menuDropDownMenu.add( tenMinuteRadioButton );

        oneMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                fiveMinuteRadioButton.setIcon( null );
                tenMinuteRadioButton.setIcon( null );
            }
        } );

        fiveMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                oneMinuteRadioButton.setIcon( null );
                tenMinuteRadioButton.setIcon( null );
            }
        } );

        tenMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                oneMinuteRadioButton.setIcon( null );
                fiveMinuteRadioButton.setIcon( null );
            }
        } );

        resetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.updateRefreshInterval( false, 0 );
                resetButton.setActive( false );
                resetButton.setEnabled( false );
                resetButton.setText( Constants.INSTANCE.Autorefresh_Disabled() );
                oneMinuteRadioButton.setIcon( null );
                fiveMinuteRadioButton.setIcon( null );
                tenMinuteRadioButton.setIcon( null );
            }
        } );

        menuDropDownMenu.add( new Divider() );
        menuDropDownMenu.add( resetButton );
    }

}
