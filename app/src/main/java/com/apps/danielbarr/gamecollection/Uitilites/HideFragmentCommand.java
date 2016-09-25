package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;

/**
 * Created by danielbarr on 7/10/15.
 */
public class HideFragmentCommand {

    private String fragmentName;
    private Activity activity;

    public HideFragmentCommand(Activity activity, String fragmentName){
        this.fragmentName = fragmentName;
        this.activity = activity;
    }

    public void execute(){
        activity.getFragmentManager().beginTransaction().hide(activity.getFragmentManager().findFragmentByTag(fragmentName)).commit();

    }
}
