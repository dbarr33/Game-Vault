package com.apps.danielbarr.gamecollection.Uitilites;

import android.content.Context;

/**
 * Created by danielbarr on 7/6/15.
 */
public class GameApplication {
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

}
