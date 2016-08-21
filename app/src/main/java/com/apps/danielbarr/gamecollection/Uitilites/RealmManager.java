package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmDeveloper;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 8/23/15.
 */
public class RealmManager {

    private static RealmManager realmManager;
    private static int REALM_VERSION = 2;
    private Realm realm;
    private RealmConfiguration myConfig;

    private RealmManager() {
        myConfig = new RealmConfiguration.Builder(GameApplication.getActivity())
                .name("myrealm.realm")
                .schemaVersion(REALM_VERSION)
                .migration(realmMigration)
                .build();

        realm = Realm.getInstance(myConfig);

        RealmResults<RealmGame> games = getGamesByPlatform("");

        realm.beginTransaction();
        for(RealmGame temp: games) {
            temp.setPlatform("Similar Game");
        }
        realm.commitTransaction();
    }

    public Realm getRealm() {
        return realm;
    }

    public static RealmManager getInstance() {
        if (realmManager == null) {
            realmManager = new RealmManager();
        }

        return realmManager;
    }

    private RealmResults<RealmGame> getGamesByPlatform(String platform){
        return realm.where(RealmGame.class).equalTo(GameApplication.getResourceString(R.string.realm_game_platform), platform).findAllSorted(GameApplication.getResourceString(R.string.realm_game_date));
    }

    private RealmResults<RealmGame> getAllGames(){
        return realm.where(RealmGame.class).notEqualTo("platform", "Similar Game").findAllSorted(GameApplication.getResourceString(R.string.realm_game_date));
    }

    public void savePlatform(DrawerItem drawerItem) {
        realm.beginTransaction();
        realm.copyToRealm(drawerItem);
        realm.commitTransaction();
    }

    public void deletePlatform(DrawerItem drawerItem) {
        realm.beginTransaction();
        drawerItem.deleteFromRealm();
        realm.commitTransaction();
    }

    public void updatePlatform(DrawerItem drawerItem, String name) {
        realm.beginTransaction();
        drawerItem.setName(name);
        realm.commitTransaction();
    }

    public List<DrawerItem> getAllPlatforms() {
        return realm.allObjects(DrawerItem.class);
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
        RealmResults<RealmGame> tempRealmGames;

        if (platform.equalsIgnoreCase("All")) {
            tempRealmGames = getAllGames();
        }
        else {
            tempRealmGames = getGamesByPlatform(platform);
        }
        ArrayList<RealmGame> realmGames = new ArrayList<>();
        for (RealmGame realmGame : tempRealmGames) {
            realmGames.add(realmGame);
        }
        return realmGames;
    }

    public RealmGame getRealmGameByPosition(String platform, int position) {
        List<RealmGame> games = getGames(platform);
        try {
            if(games.size() > 0) {
                return games.get(position);
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

    public void removeGame(RealmGame realmGame) {
        realm.beginTransaction();
        realmGame.getDevelopers().deleteAllFromRealm();
        realmGame.getPublishers().deleteAllFromRealm();
        realmGame.getCharacters().deleteAllFromRealm();
        realmGame.getRealmGenre().deleteAllFromRealm();
        realmGame.getSimilarRealmGames().deleteAllFromRealm();
        realmGame.deleteFromRealm();

        realm.commitTransaction();
    }

    public RealmList<RealmPublisher> getAllPublishers() {
        RealmResults<RealmPublisher> publishers = realm.where(RealmPublisher.class).distinct("name");
        RealmList<RealmPublisher> realmPublishers = new RealmList<>();
        for(RealmPublisher temp : publishers){
            realmPublishers.add(temp);
        }
       return realmPublishers;
    }

    public RealmList<RealmDeveloper> getAllDevelopers() {
        RealmResults<RealmDeveloper> developers = realm.where(RealmDeveloper.class).distinct("name");
        RealmList<RealmDeveloper> realmDevelopers = new RealmList<>();
        for(RealmDeveloper temp : developers){
            realmDevelopers.add(temp);
        }
        return realmDevelopers;
    }

    public void closeRealm(){
        realm.close();
    }


    private RealmMigration realmMigration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            if(oldVersion == 1) {
                realm.getSchema().get("RealmCharacter")
                        .addField("imageURL", String.class)
                        .removeField("photosLoaded")
                        .setNullable("photo", true);
                realm.getSchema().create("RealmPublisher")
                        .addField("name", String.class)
                        .addIndex("name");
                realm.getSchema().create("RealmDeveloper")
                        .addField("name", String.class)
                        .addIndex("name");
                realm.getSchema().get("RealmGame")
                        .addRealmListField("publishers", realm.getSchema().get("RealmPublisher"))
                        .addRealmListField("developers", realm.getSchema().get("RealmDeveloper"))
                        .addField("gameID", String.class)
                        .removeField("hasImage")
                        .setNullable("photo", true)
                        .setNullable("description", true)
                        .setNullable("photoURL", true);
            }
        }
    };
}
