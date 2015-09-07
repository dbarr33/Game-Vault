package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 8/23/15.
 */
public class RealmManager {

    private static RealmManager realmManager;
    private Realm realm;

    private RealmManager() {
        realm = Realm.getInstance(GameApplication.getActivity());

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
        return getAllGames(platform).get(position);
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
