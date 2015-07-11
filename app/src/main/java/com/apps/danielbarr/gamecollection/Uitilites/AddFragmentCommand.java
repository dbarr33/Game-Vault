package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.Fragment;

import com.apps.danielbarr.gamecollection.R;

/**
 * Created by danielbarr on 7/10/15.
 */
public class AddFragmentCommand {

    private Fragment fragment;
    private Activity activity;

    public AddFragmentCommand(Fragment fragment, Activity activity){
        this.fragment = fragment;
        this.activity = activity;
    }

    public void execute(){
        activity.getFragmentManager().beginTransaction().add(R.id.content_frame,fragment, fragment.getClass().getName()).addToBackStack(null).commit();

    }
}
