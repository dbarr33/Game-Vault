package com.apps.danielbarr.gamecollection.presenter;

import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;

import java.util.ArrayList;

/**
 * Created by danielbarr on 8/23/15.
 */
public interface EditGameView {
    void showProgressBar();
    void hideProgressBar();
    void setupGameImages(String imageURL);
    void configureGeneRecyclerView(ArrayList<String> strings);
    void configureSimilarGamesRecyclerView(ArrayList<String> strings);
    void configureCharacterRecyclerView(GameCharactersRecyclerAdapter adapter);
    void populateFields(RealmGame realmGame);
    void showConfirmationMessage(String message);
    void configurePublisherRecyclerView(ArrayList<String> strings);
}
