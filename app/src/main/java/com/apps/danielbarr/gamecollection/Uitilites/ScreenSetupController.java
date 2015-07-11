package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Fragments.GameRecyclerListFragment;
import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ScreenSetupController {

    public static void currentScreenEditGame(Activity activity, Boolean hideDelete) {
        activity.findViewById(R.id.toolbar).setVisibility(View.GONE);
        activity.findViewById(R.id.saveGameButton).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.floatingActionButton).setVisibility(View.GONE);

        if(hideDelete) {
            activity.findViewById(R.id.deleteGameButton).setVisibility(View.GONE);
        }
        else {
            activity.findViewById(R.id.deleteGameButton).setVisibility(View.VISIBLE);
        }

        DrawerLayout mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

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
        ShowFragmentCommand showFragmentCommand = new ShowFragmentCommand(activity, GameRecyclerListFragment.class.getName());
        showFragmentCommand.execute();
        GameRecyclerListFragment gameRecyclerListFragment = (GameRecyclerListFragment)activity.getFragmentManager()
                .findFragmentByTag(GameRecyclerListFragment.class.getName());
        gameRecyclerListFragment.notifyDataSetChanged();
    }
}
