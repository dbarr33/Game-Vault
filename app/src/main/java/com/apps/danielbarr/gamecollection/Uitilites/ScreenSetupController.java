package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;
import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ScreenSetupController {

    public static void currentScreenCharacter(Activity activity) {
        activity.findViewById(R.id.deleteGameButton).setVisibility(View.GONE);
        activity.findViewById(R.id.saveGameButton).setVisibility(View.GONE);
    }

    public static void currentScreenGameList(Activity activity){
        Toolbar mainTool = (Toolbar)activity.findViewById(R.id.toolbar);
        mainTool.setVisibility(View.VISIBLE);
        ((Main)activity).setSupportActionBar(mainTool);

        activity.findViewById(R.id.editToolbar).setVisibility(View.GONE);
        activity.findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.deleteGameButton).setVisibility(View.INVISIBLE);
        activity.findViewById(R.id.saveGameButton).setVisibility(View.GONE);

        DrawerLayout mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        activity.getFragmentManager().popBackStack();
        ShowFragmentCommand showFragmentCommand = new ShowFragmentCommand(activity, GameListFragment.class.getName());
        showFragmentCommand.execute();
        GameListFragment gameListFragment = (GameListFragment)activity.getFragmentManager()
                .findFragmentByTag(GameListFragment.class.getName());
        gameListFragment.notifyDataSetChanged();
    }
}
