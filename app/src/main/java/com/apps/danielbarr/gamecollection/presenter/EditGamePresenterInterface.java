package com.apps.danielbarr.gamecollection.presenter;

import com.apps.danielbarr.gamecollection.Model.RealmGame;

/**
 * Created by danielbarr on 8/23/15.
 */
public interface EditGamePresenterInterface {
    public void configureScreen(boolean hideDelete);
    public void fetchDataFromAPI(int id, String url);
    public void loadDataFromRealm(String platform, int position);
    public void setupToolbar();
    public void saveGame(RealmGame realmGame);
    public void updateGame(RealmGame realmGame, String platform, int position);
    public void removeGame(String platform, int position);
}
