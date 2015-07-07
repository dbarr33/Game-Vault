package com.apps.danielbarr.gamecollection.Activities;

import android.app.FragmentManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.DrawerListAdapter;
import com.apps.danielbarr.gamecollection.Old.DragAndDropList.DragListener;
import com.apps.danielbarr.gamecollection.Old.DragAndDropList.DragNDropAdapter;
import com.apps.danielbarr.gamecollection.Old.DragAndDropList.DropListener;
import com.apps.danielbarr.gamecollection.Old.DragAndDropList.RemoveListener;
import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Fragments.GameRecyclerListFragment;
import com.apps.danielbarr.gamecollection.Fragments.SearchFragment;
import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.Model.DrawerList;
import com.apps.danielbarr.gamecollection.Model.FirstInstall;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.FragmentController;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
import com.apps.danielbarr.gamecollection.Uitilites.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class Main extends ActionBarActivity implements DrawerListAdapter.OnStartDragListner, DrawerListAdapter.OnDrawerClickListener{

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBar actionBar;
    private GameRecyclerListFragment gameRecyclerListFragment;
    private DrawerListAdapter drawerListAdapter;
    private Realm realm;
    private FloatingActionButton floatingActionButton;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle args = new Bundle();
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.floating_action_selector)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fm = getFragmentManager();
                SearchFragment dialog = new SearchFragment();
                Bundle args = new Bundle();

                collapse(floatingActionButton);
                args.putString(dialog.EXTRA_PASS_PLATFORM, mTitle.toString());
                dialog.setArguments(args);
                dialog.show(fm, "TAG");
            }
        });

        // Initializing
        mDrawerTitle  = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView)findViewById(R.id.drawerList);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        realm = Realm.getInstance(getApplicationContext());
        RealmResults<FirstInstall> realmResults = realm.where(FirstInstall.class).findAll();

        if(realmResults.isEmpty()) {
            createDrawerList();
        }

        RealmResults<DrawerItem> drawerItems = realm.allObjects(DrawerItem.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerListAdapter = new DrawerListAdapter(drawerItems, this, this, this);
        mDrawerList.setLayoutManager(linearLayoutManager);
        mDrawerList.setAdapter(drawerListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(drawerListAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mDrawerList);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        gameRecyclerListFragment = new GameRecyclerListFragment();
        mTitle = drawerItems.get(0).getName();
        args.putString(GameRecyclerListFragment.GAME_PLATFORM, mTitle.toString());

        gameRecyclerListFragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        if(frgManager.findFragmentByTag(getResources().getString(R.string.fragment_game_list)) == null) {
            frgManager.beginTransaction().add(R.id.content_frame, gameRecyclerListFragment, getResources().getString(R.string.fragment_game_list))
                    .commit();
        }

        getSupportActionBar().setTitle(drawerItems.get(0).getName());
    }



    public void SelectItem(int position) {
        gameRecyclerListFragment.updateGameList(drawerListAdapter.getDrawerList().get(position).getName());
        setTitle(drawerListAdapter.getDrawerList().get(position).getName());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {

            FragmentController fragmentController = new FragmentController(getFragmentManager());

            if(getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_character)) != null) {

                fragmentController.showFramentCommand(getResources().getString(R.string.fragment_edit_game));
                EditGameFragment editGameFragment = (EditGameFragment)getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game));
                editGameFragment.mScrollView.setViewAlpha();

                if(editGameFragment.gamePosition > -1) {
                    ScreenSetupController.currentScreenEditGame(this, false);
                }
                else {
                    ScreenSetupController.currentScreenEditGame(this, true);
                }
                getFragmentManager().popBackStack();

            }
            else if(getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game)) != null) {
                ScreenSetupController.currentScreenGameList(this);
                ((EditGameFragment)getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game))).realm.close();
            }
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClick(int position) {
        if (((DrawerListAdapter)mDrawerList.getAdapter()).getDrawerList().get(position).getName() != null) {
            SelectItem(position);
        }
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(500);
        //v.startAnimation(a);

    }

    public void createDrawerList()
    {
        realm.beginTransaction();
        FirstInstall addPerson = realm.createObject(FirstInstall.class);
        addPerson.setFirstInstall(false);
        int count = 0;
        realm.commitTransaction();

        realm.beginTransaction();
        DrawerList list = realm.createObject(DrawerList.class);
        DrawerItem item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.ps4_drawer_title));
        item.setIconID(R.drawable.playstation_4_icon);
        item.setPosition(count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.ps3_drawer_title));
        item.setIconID(R.drawable.playstation_3_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.ps2_drawer_title));
        item.setIconID(R.drawable.playstation_2_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.ps1_drawer_title));
        item.setIconID(R.drawable.playstation_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.pc_drawer_title));
        item.setIconID(R.drawable.pc_icon);
        list.getItems().add(item);
        item.setPosition(++count);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.xboxone_drawer_title));
        item.setIconID(R.drawable.xboxone_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.xbox360_drawer_title));
        item.setIconID(R.drawable.xbox360_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.xbox_drawer_title));
        item.setIconID(R.drawable.xbox_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.wiiu_drawer_title));
        item.setIconID(R.drawable.wiiu_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.wii_drawer_title));
        item.setIconID(R.drawable.wii_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.gamecube_drawer_title));
        item.setIconID(R.drawable.gamecube_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.nintendo64_drawer_title));
        item.setIconID(R.drawable.nintendo64_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.supernintendo_drawer_title));
        item.setIconID(R.drawable.supernintendo_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.nintendo_drawer_title));
        item.setIconID(R.drawable.nintendo_icon);
        item.setPosition(++count);
        list.getItems().add(item);
        realm.commitTransaction();
    }

}
