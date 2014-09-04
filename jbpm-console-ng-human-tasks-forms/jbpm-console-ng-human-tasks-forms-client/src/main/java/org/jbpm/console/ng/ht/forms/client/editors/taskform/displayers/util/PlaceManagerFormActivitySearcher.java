package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

/**
 * @author pefernan
 */
@Dependent
public class PlaceManagerFormActivitySearcher {

  @Inject
  private ActivityManager activityManager;

  private AbstractWorkbenchScreenActivity currentActivity;

  public IsWidget findFormActivityWidget(String name, Map<String, String> params) {
    DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(name + " Form", params);
    currentActivity = (AbstractWorkbenchScreenActivity) activityManager.getActivity(defaultPlaceRequest);
    if (currentActivity == null) {
      return null;
    }
    currentActivity.launch(defaultPlaceRequest, null);
    currentActivity.onStartup(defaultPlaceRequest);
    currentActivity.onOpen();
    return currentActivity.getWidget();
  }

  public void closeFormActivity() {
    if (currentActivity != null) {
      currentActivity.onClose();
    }
  }
}
