package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.RecyclerGameListAdapter;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Daniel Barr (Fuzz)
 */

public class GameRecyclerListFragment extends Fragment {

    public static final String GAME_PLATFORM = "gamePlatform";

    private RecyclerView gameListRecycler;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recycleview_game_list, container, false);
        String platform = getArguments().getString(GAME_PLATFORM);

        gameListRecycler = (RecyclerView)v.findViewById(R.id.recycler_gameList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        realm = Realm.getInstance(getActivity().getApplicationContext());

        RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", platform).equalTo("isDeleted", false).findAll();

        ArrayList<Game> gameList = new ArrayList<>();
        for(int i = 0; i < storedGames.size(); i++) {
            gameList.add((Game)storedGames.get(i));
        }

        RecyclerGameListAdapter recyclerGameListAdapter = new RecyclerGameListAdapter(gameList, getActivity(), platform);
        gameListRecycler.setLayoutManager(linearLayoutManager);
        gameListRecycler.setAdapter(recyclerGameListAdapter);

        return v;
    }

    public void updateGameList(String platform) {
        RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", platform).equalTo("isDeleted", false) .findAll();

        ArrayList<Game> gameList = new ArrayList<>();
        for(int i = 0; i < storedGames.size(); i++) {
            gameList.add((Game)storedGames.get(i));
        }
        Toast.makeText(getActivity(), String.valueOf(storedGames.size()), Toast.LENGTH_LONG).show();

        RecyclerGameListAdapter recyclerGameListAdapter = new RecyclerGameListAdapter(gameList, getActivity(), platform);
        gameListRecycler.setAdapter(recyclerGameListAdapter);

    }

    public void notifiyDataSetChanged() {
        gameListRecycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if(! ((Main)getActivity()).getSupportActionBar().isShowing()) {
            ((Main)getActivity()).getSupportActionBar().show();
        }
        super.onResume();
    }
}
