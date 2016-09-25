package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Daniel Barr (Fuzz)
 */

public class AnalyticsTracker {
  private Tracker mTracker;

  public AnalyticsTracker(Activity activity) {

    GameApplication application = (GameApplication) activity.getApplication();
    mTracker = application.mTracker;
  }

  public void sendEvent(String screenName, String action) {
    sendEvent(screenName, action, "");
  }

  public void sendEvent(String screenName, String action, String category) {
    sendEvent(screenName, action, category, "");
  }

  public void sendEvent(String screenName, String action, String category, String label) {
    mTracker.setScreenName(screenName);
    mTracker.send(new HitBuilders.EventBuilder().setAction(action)
        .setCategory(category)
        .setLabel(label)
        .build());
  }
}
