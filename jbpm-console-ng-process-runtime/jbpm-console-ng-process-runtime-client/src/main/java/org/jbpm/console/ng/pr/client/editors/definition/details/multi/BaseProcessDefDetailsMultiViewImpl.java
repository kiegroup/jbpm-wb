package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.pr.client.editors.definition.details.BaseProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class BaseProcessDefDetailsMultiViewImpl extends
		AbstractTabbedDetailsView<BaseProcessDefDetailsMultiPresenter>
		implements
		BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView , RequiresResize {
	
	private ScrollPanel spDetails = new ScrollPanel();
	@Override
	public void init(final BaseProcessDefDetailsMultiPresenter presenter) {
		super.init(presenter);
		createAndBindUi();
		spDetails.add(getSpecificProcessDefDetailPresenter().getWidget());
		((HTMLPanel) tabPanel.getWidget(0)).add(spDetails);
	}
	protected abstract void createAndBindUi() ;
	
	protected abstract BaseProcessDefDetailsPresenter getSpecificProcessDefDetailPresenter();
	
	@Override
	public void initTabs() {
		tabPanel.addTab("Definition Details",
				Constants.INSTANCE.Definition_Details());
	}

    @Override
    public Button getCloseButton() {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.Close() );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.closeDetails();
                    }
                } );
            }
        };
    }

    @Override
    public IsWidget getRefreshButton() {
        return new Button() {
            {
                setIcon( IconType.REFRESH );
                setTitle( Constants.INSTANCE.Refresh() );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.refresh();
                    }
                } );
            }
        };
    }

    @Override
    public IsWidget getNewInstanceButton() {
        return new Button() {{
            setSize( MINI );
            setIcon( IconType.PLAY );
            setText( Constants.INSTANCE.New_Instance() );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.createNewProcessInstance();
                }
            } );
        }};
    }

    @Override
    public void onResize() {
    	super.onResize(); 
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                tabPanel.setHeight(getSpecificOffsetHeight() - 30 + "px");
                spDetails.setHeight(getSpecificOffsetHeight() - 30 + "px");
            }
        });
    }
	protected abstract int getSpecificOffsetHeight();
}
