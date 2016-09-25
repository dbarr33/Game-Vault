package com.apps.danielbarr.gamecollection.Activities;

/**
 * Created by danielbarr on 1/17/15.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.apps.danielbarr.gamecollection.R;

public abstract class SingleFragmentActivity extends ActionBarActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_game_list);

        FragmentManager fm =  getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.gameListContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.gameListContainer, fragment).commit();
        }
    }
}
