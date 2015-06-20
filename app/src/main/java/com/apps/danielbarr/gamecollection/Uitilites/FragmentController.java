package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Fragment;
import android.app.FragmentManager;

import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class FragmentController {

    private FragmentManager fragmentManager;

    public FragmentController(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void replaceFragmentCommand(Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragment, tag);
    }

    public void showFramentCommand(String fragmentName) {
        fragmentManager.beginTransaction().show(fragmentManager .findFragmentByTag(fragmentName)).commit();
    }

    public void hideFramentCommand(String fragmentName) {
        fragmentManager.beginTransaction().hide(fragmentManager .findFragmentByTag(fragmentName)).commit();
    }
}
