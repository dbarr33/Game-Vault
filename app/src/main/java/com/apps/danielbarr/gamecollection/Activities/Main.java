package com.apps.danielbarr.gamecollection.Activities;

import android.app.FragmentManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
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
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.DrawerListAdapter;
import com.apps.danielbarr.gamecollection.Fragments.CharacterFragment;
import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Fragments.FilterFragment;
import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;
import com.apps.danielbarr.gamecollection.Fragments.SearchFragment;
import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.Model.DrawerList;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.FirstInstall;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AddFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
import com.apps.danielbarr.gamecollection.Uitilites.ShowFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.SimpleItemTouchHelperCallback;

import io.realm.Realm;
import io.realm.RealmResults;

public class Main extends ActionBarActivity implements DrawerListAdapter.OnStartDragListner, DrawerListAdapter.OnDrawerClickListener{

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private GameListFragment gameListFragment;
    private DrawerListAdapter drawerListAdapter;
    private Realm realm;
    private FloatingActionButton floatingActionButton;
    private ItemTouchHelper mItemTouchHelper;
    private TextView toolbarTitle;
    private String selectedPlatform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        GameApplication.setActivity(this);
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

                args.putString(dialog.EXTRA_PASS_PLATFORM, selectedPlatform.toString());
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
        realm = RealmManager.getInstance().getRealm();
        RealmResults<FirstInstall> realmResults = realm.where(FirstInstall.class).findAll();

        if(realmResults.isEmpty()) {
            createDrawerList();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        drawerListAdapter = new DrawerListAdapter(this, this, this);
        mDrawerList.setLayoutManager(linearLayoutManager);
        mDrawerList.setAdapter(drawerListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(drawerListAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mDrawerList);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                setTitle(selectedPlatform);
                invalidateOptionsMenu(); // creates call to
            }

            public void onDrawerOpened(View drawerView) {
                setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        gameListFragment = new GameListFragment();
        selectedPlatform = drawerListAdapter.getDrawerList().get(0).getName();
        args.putString(GameListFragment.GAME_PLATFORM, selectedPlatform.toString());

        gameListFragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        if(frgManager.findFragmentByTag(GameListFragment.class.getName()) == null) {
            AddFragmentCommand addFragmentCommand = new AddFragmentCommand(gameListFragment, this);
            addFragmentCommand.execute();
        }

        getSupportActionBar().setTitle(selectedPlatform);
        getFragmentManager().beginTransaction().add(R.id.filterFragment, new FilterFragment(), null).commit();
        toolbarTitle = (TextView)findViewById(R.id.title);
        setTitle(selectedPlatform);
    }

    public void SelectItem(int position) {
        gameListFragment = (GameListFragment)getFragmentManager().findFragmentByTag(GameListFragment.class.getName());
        gameListFragment.setGameList(drawerListAdapter.getDrawerList().get(position).getName());
        setTitle(drawerListAdapter.getDrawerList().get(position).getName());
        selectedPlatform = drawerListAdapter.getDrawerList().get(position).getName();
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbarTitle.setText(title);
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

            if(getFragmentManager().findFragmentByTag(CharacterFragment.class.getName()) != null) {
                ShowFragmentCommand showFragmentCommand = new ShowFragmentCommand(this, EditGameFragment.class.getName());
                showFragmentCommand.execute();
                EditGameFragment editGameFragment = (EditGameFragment)getFragmentManager().findFragmentByTag(EditGameFragment.class.getName());
                editGameFragment.mScrollView.setViewAlpha();

                if(editGameFragment.gamePosition > -1) {
                    editGameFragment.editGamePresenter.configureScreen(false);
                }
                else {
                    editGameFragment.editGamePresenter.configureScreen(true);
                }
                getFragmentManager().popBackStack();

            }
            else if(getFragmentManager().findFragmentByTag(EditGameFragment.class.getName()) != null) {
                ScreenSetupController.currentScreenGameList(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        RealmManager.getInstance().closeRealm();
        super.onDestroy();
    }

    public void removeGame(int position, RealmGame realmGame){
        GameListFragment gameListFragment = (GameListFragment)getFragmentManager().findFragmentByTag(GameListFragment.class.getName());
        gameListFragment.removeGame(position, realmGame);
    }

    public void applyFilter(FilterState filterState){
        gameListFragment.applyFilter(filterState);
    }

    public void createDrawerList() {
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
        item = realm.createObject(DrawerItem.class);
        item.setName(getString(R.string.other_title));
        item.setIconID(R.drawable.joystick);
        item.setPosition(++count);
        list.getItems().add(item);
        realm.commitTransaction();
    }

}
