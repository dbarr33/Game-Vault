package com.apps.danielbarr.gamecollection.Fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apps.danielbarr.gamecollection.Activities.EditGameActivity;
import com.apps.danielbarr.gamecollection.Adapter.GameListAdapter;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

import io.realm.Realm;


/**
 * Created by danielbarr on 1/17/15.
 */
public class GameListFragment extends ListFragment {

    private GameListAdapter gameListAdapter;
    private ArrayList<Game> games;
    private Realm realm;

    public static final String ITEM_NAME = "itemName";

    private String name = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        games = new ArrayList<>();

        gameListAdapter = new GameListAdapter(getActivity(),games, getActivity().getApplicationContext());
        setListAdapter(gameListAdapter);
        gameListAdapter.notifyDataSetChanged();
        realm = Realm.getInstance(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        name = getArguments().getString(ITEM_NAME);
        View v = inflater.inflate(R.layout.empty_list, container, false);

        gameListAdapter.updateGameList(getArguments().getString(ITEM_NAME));
        super.onCreateView(inflater, container, savedInstanceState);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addGame: {
                android.app.FragmentManager fm = getActivity().getFragmentManager();
                SearchFragment dialog = new SearchFragment();
                dialog.setTargetFragment(GameListFragment.this, 0);
                Bundle args = new Bundle();
                args.putString(dialog.EXTRA_PASS_PLATFORM, name);
                dialog.setArguments(args);
                dialog.show(fm, "TAG");
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), EditGameActivity.class);
        i.putExtra(EditGameFragment.EXTRA_GAME, position);
        i.putExtra(EditGameFragment.EXTRA_PLATFORM, name);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        gameListAdapter.updateGameList(getArguments().getString(ITEM_NAME));
    }
}

