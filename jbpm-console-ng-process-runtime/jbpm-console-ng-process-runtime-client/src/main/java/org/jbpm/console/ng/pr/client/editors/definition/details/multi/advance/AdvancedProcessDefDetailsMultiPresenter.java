package org.jbpm.console.ng.pr.client.editors.definition.details.multi.advance;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiPresenter;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "Advanced Process Details Multi", preferredWidth = 500)
public class AdvancedProcessDefDetailsMultiPresenter extends
        BaseProcessDefDetailsMultiPresenter {

    public interface AdvancedProcessDefDetailsMultiView extends
            BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

        IsWidget getOptionsButton();
    }

    @Inject
    public AdvancedProcessDefDetailsMultiView view;

    @DefaultPosition
    public Position getPosition() {
        return super.getDefaultPosition();
    }

    @WorkbenchPartView
    public UberView<BaseProcessDefDetailsMultiPresenter> getView() {
        return view;
    }

    @Override
    protected void setDefaultTab() {
        view.getTabPanel().selectTab( 0 );
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {

                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            @Override
                            public IsWidget build() {
                                return view.getNewInstanceButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {

                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            @Override
                            public IsWidget build() {
                                return view.getOptionsButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {

                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            @Override
                            public IsWidget build() {
                                return view.getRefreshButton();
                            }
                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {

                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            @Override
                            public IsWidget build() {
                                return view.getCloseButton();
                            }
                        };
                    }
                } ).endMenu().build();
    }
}
