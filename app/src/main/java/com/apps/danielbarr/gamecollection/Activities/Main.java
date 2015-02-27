package com.apps.danielbarr.gamecollection.Activities;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;
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
    DragNDropAdapter adapter;
    Realm realm;

    ArrayList<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle args = new Bundle();


        // Initializing
        dataList = new ArrayList<DrawerItem>();
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

        adapter = new DragNDropAdapter(getApplicationContext(), dataList);//new DragNDropAdapter(this,content)

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

        GameListFragment listFragment = new GameListFragment();
        args.putString(GameListFragment.ITEM_NAME, dataList.get(0).getName());

        listFragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().add(R.id.content_frame, listFragment)
                .commit();
        getSupportActionBar().setTitle(dataList.get(0).getName());
        mDrawerList.setItemChecked(0,true);
        mTitle = dataList.get(0).getName();
    }



    public void SelectItem(int possition) {

        Bundle args = new Bundle();

        GameListFragment listFragment = new GameListFragment();
        args.putString(GameListFragment.ITEM_NAME, dataList.get(possition).getName());

        listFragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, listFragment)
                .commit();

        mDrawerList.setItemChecked(possition, true);
        setTitle(dataList.get(possition).getName());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
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
