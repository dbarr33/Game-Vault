package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 8/23/15.
 */
public class RealmManager {

    private static RealmManager realmManager;
    private Realm realm;

    private RealmManager() {
        RealmConfiguration myConfig = new RealmConfiguration.Builder(GameApplication.getActivity())
                .name("myrealm.realm")
                .schemaVersion(1)
                .build();
        realm = Realm.getInstance(myConfig);
    }

    public static RealmManager getInstance() {
        if (realmManager == null) {
            realmManager = new RealmManager();
        }

        return realmManager;
    }

    private RealmResults<RealmGame> getAllGames(String platform){
        return realm.where(RealmGame.class).equalTo(GameApplication.getString(R.string.realm_game_platform), platform).findAllSorted(GameApplication.getString(R.string.realm_game_date));
    }

    public void saveGame(RealmGame realmGame) {
        realm.beginTransaction();
        realm.copyToRealm(realmGame);
        realm.commitTransaction();
    }

    public void updateGame(RealmGame realmGame, String platform, int position) {
        realm.beginTransaction();
        RealmGame oldGame = getRealmGameByPosition(platform, position);
        oldGame.setPlatform(realmGame.getPlatform());
        if(realmGame.getPhoto() != null) {
           oldGame.setPhoto(realmGame.getPhoto());
        }
        if(realmGame.getCharacters() != null) {
            oldGame.getCharacters().removeAll(oldGame.getCharacters());
            oldGame.getCharacters().clear();
            for (RealmCharacter realmCharacter : realmGame.getCharacters()) {
                oldGame.getCharacters().add(realm.copyToRealm(realmCharacter));
            }
        }
        oldGame.setUserRating(realmGame.getUserRating());
        oldGame.setCompletionPercentage(realmGame.getCompletionPercentage());
        realm.commitTransaction();
    }

    public ArrayList<RealmGame> getGames(String platform) {
        RealmResults<RealmGame> tempRealmGames = getAllGames(platform);
        ArrayList<RealmGame> realmGames = new ArrayList<>();
        for (RealmGame realmGame : tempRealmGames) {
            realmGames.add(realmGame);
        }
        return realmGames;
    }

    public RealmGame getRealmGameByPosition(String platform, int position) {
        List<RealmGame> games = getAllGames(platform);
        try {
            if(games.size() > 0) {
                return getAllGames(platform).get(position);
            }
            else {
                throw new IOException();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeGame(String platform, int position) {
        realm.beginTransaction();
        getAllGames(platform).get(position).removeFromRealm();
        realm.commitTransaction();
    }

    public void removeGame(RealmGame realmGame) {
        realm.beginTransaction();
        realmGame.removeFromRealm();
        realm.commitTransaction();
    }

    public void closeRealm(){
        realm.close();
    }
}
