package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by danielbarr on 7/6/15.
 */
public class GameApplication extends Application {

    private static String GOOGLE_ANALYTICS_ID = "UA-76408911-1";
    public static boolean TURN_ON_ANALYTICS  = false;

    public Tracker mTracker;

    private Tracker setUpGoogleAnalyticsTracking() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        String trackingId;
            analytics.setLocalDispatchPeriod(30);
            trackingId = GOOGLE_ANALYTICS_ID;

        Tracker tracker = analytics.newTracker(trackingId);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(false);
        return tracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mTracker = setUpGoogleAnalyticsTracking();
    }

    private static Activity activity;

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        GameApplication.activity = activity;
    }

    public static String getResourceString(int id){
        return activity.getString(id);
    }
}
