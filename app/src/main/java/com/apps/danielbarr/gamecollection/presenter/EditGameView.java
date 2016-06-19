package com.apps.danielbarr.gamecollection.presenter;

import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;

import java.util.ArrayList;

/**
 * Created by danielbarr on 8/23/15.
 */
public interface EditGameView {
    public void showProgressBar();
    public void hideProgressBar();
    public void setupGameImages(String imageURL);
    public void configureGeneRecyclerView(ArrayList<String> strings);
    public void configureSimilarGamesRecyclerView(ArrayList<String> strings);
    public void configureCharacterRecyclerView(GameCharactersRecyclerAdapter adapter);
    public void populateFields(RealmGame realmGame);
    public void showConfirmationMessage(String message);
}
