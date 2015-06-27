package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.RecyclerGameListAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
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
    private String platform;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recycleview_game_list, container, false);
        platform = getArguments().getString(GAME_PLATFORM);

        gameListRecycler = (RecyclerView)v.findViewById(R.id.recycler_gameList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        realm = Realm.getInstance(getActivity().getApplicationContext());

        RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", platform).equalTo("isDeleted", false).findAll();

        ArrayList<RealmGame> realmGameList = new ArrayList<>();
        for(int i = 0; i < storedRealmGames.size(); i++) {
            realmGameList.add(storedRealmGames.get(i));
        }

        RecyclerGameListAdapter recyclerGameListAdapter = new RecyclerGameListAdapter(realmGameList, getActivity(), platform);
        gameListRecycler.setLayoutManager(linearLayoutManager);
        gameListRecycler.setAdapter(recyclerGameListAdapter);

        return v;
    }

    public void updateGameList(String newPlatform) {
        if(realm == null) {
            realm = Realm.getInstance(getActivity());
        }

        RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", newPlatform).equalTo("isDeleted", false).findAll();

        platform = newPlatform;
        
        ArrayList<RealmGame> realmGameList = new ArrayList<>();
        for(int i = 0; i < storedRealmGames.size(); i++) {
            realmGameList.add(storedRealmGames.get(i));
        }
        RecyclerGameListAdapter recyclerGameListAdapter = new RecyclerGameListAdapter(realmGameList, getActivity(), newPlatform);
        gameListRecycler.setAdapter(recyclerGameListAdapter);
    }

    public void notifyDataSetChanged() {
        updateGameList(platform);
    }

    @Override
    public void onResume() {
        if(! ((Main)getActivity()).getSupportActionBar().isShowing()) {
            ((Main)getActivity()).getSupportActionBar().show();
        }
        super.onResume();
    }
}
