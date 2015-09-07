package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;

/**
 * Created by danielbarr on 7/6/15.
 */
public class GameApplication {
    private static Activity activity;

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        GameApplication.activity = activity;
    }

    public static String getString(int id){
        return activity.getString(id);
    }
}
