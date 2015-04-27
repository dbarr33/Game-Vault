package com.apps.danielbarr.gamecollection.Old;

import android.app.Fragment;

import com.apps.danielbarr.gamecollection.Activities.SingleFragmentActivity;

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
