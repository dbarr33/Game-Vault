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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.DragAndDropList.DragListener;
import com.apps.danielbarr.gamecollection.DragAndDropList.DragNDropAdapter;
import com.apps.danielbarr.gamecollection.DragAndDropList.DragNDropListView;
import com.apps.danielbarr.gamecollection.DragAndDropList.DropListener;
import com.apps.danielbarr.gamecollection.DragAndDropList.RemoveListener;
import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Fragments.GameRecyclerListFragment;
import com.apps.danielbarr.gamecollection.Fragments.SearchFragment;
import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.Model.DrawerList;
import com.apps.danielbarr.gamecollection.Model.FirstInstall;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class Main extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBar actionBar;
    private GameRecyclerListFragment gameRecyclerListFragment;
    private DragNDropAdapter adapter;
    private Realm realm;
    private FloatingActionButton floatingActionButton;

    ArrayList<DrawerItem> dataList;

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
        dataList = new ArrayList<>();
        mDrawerTitle  = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (DragNDropListView) findViewById(R.id.dropAndDragDrawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        realm = Realm.getInstance(getApplicationContext());
        RealmResults<FirstInstall> realmResults = realm.where(FirstInstall.class).findAll();

        if(realmResults.isEmpty())
        {
            createDrawerList();
        }

        RealmResults<DrawerItem> drawerItems = realm.allObjects(DrawerItem.class);

        for(int i =0; i < drawerItems.size(); i++)
        {
            dataList.add(drawerItems.get(i));
        }

        adapter = new DragNDropAdapter(getApplicationContext(), dataList);

        mDrawerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (mDrawerList instanceof DragNDropListView) {
            ((DragNDropListView) mDrawerList).setDropListener(mDropListener);
            ((DragNDropListView) mDrawerList).setRemoveListener(mRemoveListener);
            ((DragNDropListView) mDrawerList).setDragListener(mDragListener);
        }
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        gameRecyclerListFragment = new GameRecyclerListFragment();
        mTitle = dataList.get(0).getName();
        args.putString(GameRecyclerListFragment.GAME_PLATFORM, mTitle.toString());

        gameRecyclerListFragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        if(frgManager.findFragmentByTag(getResources().getString(R.string.fragment_game_list)) == null) {
            frgManager.beginTransaction().add(R.id.content_frame, gameRecyclerListFragment, getResources().getString(R.string.fragment_game_list))
                    .commit();
        }

        getSupportActionBar().setTitle(dataList.get(0).getName());
        mDrawerList.setItemChecked(0,true);
    }



    public void SelectItem(int position) {

        gameRecyclerListFragment.updateGameList(dataList.get(position).getName());

        mDrawerList.setItemChecked(position, true);
        setTitle(dataList.get(position).getName());
        mDrawerLayout.closeDrawer(mDrawerList);

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

    public void restoreMainScreen(boolean isEditGame) {
        Toolbar mainTool = (Toolbar)findViewById(R.id.toolbar);
        Toolbar editTool = (Toolbar)findViewById(R.id.editToolbar);
        setSupportActionBar(mainTool);
        mainTool.setVisibility(View.VISIBLE);
        editTool.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.VISIBLE);
        findViewById(R.id.deleteGameButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.saveGameButton).setVisibility(View.GONE);

        if(isEditGame) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getFragmentManager().beginTransaction().hide(getFragmentManager()
                    .findFragmentByTag(getResources().getString(R.string.fragment_edit_game))).commit();
        }else {
            getFragmentManager().beginTransaction().hide(getFragmentManager()
                    .findFragmentByTag("John")).commit();
        }
        getFragmentManager().beginTransaction().show(getFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.fragment_game_list))).commit();
        ((GameRecyclerListFragment)getFragmentManager()
                .findFragmentByTag(getResources().getString(R.string.fragment_game_list))).notifyDataSetChanged(mTitle.toString());

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if(getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_character)) != null) {
               getFragmentManager().beginTransaction().show(getFragmentManager().
                       findFragmentByTag(getResources().getString(R.string.fragment_edit_game))).commit();

                EditGameFragment editGameFragment = (EditGameFragment)getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game));
                if(editGameFragment.gamePosition > -1) {
                    findViewById(R.id.deleteGameButton).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.saveGameButton).setVisibility(View.VISIBLE);
                ((EditGameFragment)getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game))).mScrollView.setViewAlpha();
            }
            else if(getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game)) != null) {
                restoreMainScreen(true);
                ((EditGameFragment)getFragmentManager().findFragmentByTag(getResources().getString(R.string.fragment_edit_game))).realm.close();
            }
            else if(getFragmentManager().findFragmentByTag("John") != null) {
                restoreMainScreen(false);
            }

            getFragmentManager().popBackStack();

        }else {
            super.onBackPressed();
        }
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (dataList.get(position).getName() != null) {
                SelectItem(position);
            }

        }
    }

    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int from, int to) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    ListAdapter adapter = mDrawerList.getAdapter();
                    if (adapter instanceof DragNDropAdapter) {

                        realm.beginTransaction();

                        RealmResults<DrawerItem> results = realm.allObjects(DrawerItem.class);

                        String tempName  = results.get(from).getName();
                        int tempIcon = results.get(from).getIconID();

                        if(to - from >= 0) {
                            for (int i = from; i < to ; i++) {
                                results.get(i).setName(results.get(i + 1).getName());
                                results.get(i).setIconID(results.get(i + 1).getIconID());
                            }
                        }
                        else {
                            for (int i = from; i > to ; i--)
                            {
                                results.get(i).setName(results.get(i - 1).getName());
                                results.get(i).setIconID(results.get(i-1).getIconID());                            }
                        }
                        results.get(to).setName(tempName);
                        results.get(to).setIconID(tempIcon);

                        realm.commitTransaction();
                        ((DragNDropAdapter) adapter).notifyDataSetChanged();
                    }
                }
            };

    private RemoveListener mRemoveListener =
            new RemoveListener() {
                public void onRemove(int which) {
                    ListAdapter adapter = mDrawerList.getAdapter();
                    if (adapter instanceof DragNDropAdapter) {
                        ((DragNDropAdapter)adapter).onRemove(which);
                    }
                }
            };

    private DragListener mDragListener =
            new DragListener() {

                int backgroundColor = 0xe0103010;
                int defaultBackground = 0;

                public void onDrag(int x, int y, ListView listView) {

                }

                public void onStartDrag(View itemView) {

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    itemView.setVisibility(View.INVISIBLE);
                    defaultBackground = itemView.getDrawingCacheBackgroundColor();
                    itemView.setBackgroundColor(backgroundColor);
                    TextView textView = (TextView)itemView.findViewById(R.id.drawer_item_platformName);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    ImageView iv = (ImageView)itemView.findViewById(R.id.drawer_item_dragIcon);
                    if (iv != null) iv.setVisibility(View.INVISIBLE);
                }

                public void onStopDrag(View itemView) {
                    mDrawerList.setSelector(R.drawable.drawer_states);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        itemView.setBackground(getResources().getDrawable(R.drawable.drawer_states));
                    }
                    else
                    {
                        itemView.setDrawingCacheBackgroundColor(defaultBackground);
                    }
                    itemView.setVisibility(View.VISIBLE);
                    TextView textView = (TextView)itemView.findViewById(R.id.drawer_item_platformName);
                    textView.setTextColor(getResources().getColorStateList(R.color.drawer_text_states));
                    ImageView iv = (ImageView)itemView.findViewById(R.id.drawer_item_dragIcon);
                    if (iv != null) iv.setVisibility(View.VISIBLE);
                }
            };

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
