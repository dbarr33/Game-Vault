package com.apps.danielbarr.gamecollection.Activities;

import android.app.Fragment;

import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;

/**
 * Created by danielbarr on 1/17/15.
 */
public class GameListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment()
    {
        return new GameListFragment();
    }


}
