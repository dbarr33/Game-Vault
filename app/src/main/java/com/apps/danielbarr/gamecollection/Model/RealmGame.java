package com.apps.danielbarr.gamecollection.Model;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.NameInterface;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by danielbarr on 1/17/15.
 */

public class RealmGame extends RealmObject implements NameInterface{

    private String gameID;
    @Required
    private String name;
    @Required
    private String platform;
    private String description;
    private RealmList<RealmGenre> realmGenre;
    private String photoURL;
    private float completionPercentage;
    private float userRating;
    private byte[] photo;
    private RealmList<RealmCharacter> characters;
    private RealmList<RealmGame> similarRealmGames;
    private long date;
    private RealmList<RealmPublisher> publishers;
    private RealmList<RealmDeveloper> developers;

    public RealmGame(){
    }

    public RealmGame(RealmGame realmGame){
        this.characters = new RealmList<>();
        for(RealmCharacter tempCharacter: realmGame.getCharacters()){
            RealmCharacter copy = new RealmCharacter(tempCharacter);
            this.characters.add(copy);
        }

        if(realmGame.getDevelopers() != null) {
            this.developers = new RealmList<>();
            for (RealmDeveloper developer : realmGame.getDevelopers()) {
                RealmDeveloper copy = new RealmDeveloper(developer);
                this.developers.add(copy);
            }
        }

        if(realmGame.getPublishers() != null) {
            this.publishers = new RealmList<>();
            for (RealmPublisher publisher : realmGame.getPublishers()) {
                RealmPublisher copy = new RealmPublisher(publisher);
                this.publishers.add(copy);
            }
        }

        if(realmGame.getSimilarRealmGames() != null) {
            this.similarRealmGames = new RealmList<>();
            for (RealmGame tempGame: realmGame.getSimilarRealmGames()){
                RealmGame copy = new RealmGame();
                copy.setName(tempGame.getName());
                copy.setPlatform(tempGame.getPlatform());
                this.similarRealmGames.add(copy);
            }
        }

        if(realmGame.getPublishers() != null) {
            this.publishers = new RealmList<>();
            for (RealmPublisher realmPublisher: realmGame.getPublishers()){
                RealmPublisher copy = new RealmPublisher();
                copy.setName(realmPublisher.getName());
                this.publishers.add(copy);
            }
        }

        if(realmGame.getDevelopers() != null) {
            this.developers = new RealmList<>();
            for (RealmDeveloper realmDeveloper: realmGame.getDevelopers()){
                RealmDeveloper copy = new RealmDeveloper();
                copy.setName(realmDeveloper.getName());
                this.developers.add(copy);
            }
        }

        this.realmGenre = new RealmList<>();
        for(RealmGenre tempGenre: realmGame.getRealmGenre()){
            RealmGenre copy = new RealmGenre();
            copy.setName(tempGenre.getName());
            realmGenre.add(copy);
        }
        this.photo = realmGame.getPhoto();
        this.photoURL = realmGame.getPhotoURL();
        this.name = realmGame.getName();
        this.description = realmGame.getDescription();
        this.completionPercentage = realmGame.getCompletionPercentage();
        this.userRating = realmGame.getUserRating();
        this.platform = realmGame.getPlatform();
        this.date = realmGame.getDate();
        this.gameID = realmGame.getGameID();
    }

    public RealmList<RealmDeveloper> getDevelopers() {
        return developers;
    }

    public void setDevelopers(RealmList<RealmDeveloper> developers) {
        this.developers = developers;
    }

    public RealmList<RealmGame> getSimilarRealmGames() {
        return similarRealmGames;
    }

    public void setSimilarRealmGames(RealmList<RealmGame> similarRealmGames) {
        this.similarRealmGames = similarRealmGames;
    }

    public RealmList<RealmPublisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(RealmList<RealmPublisher> publishers) {
        this.publishers = publishers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public float getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(float completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public RealmList<RealmGenre> getRealmGenre() {
        return realmGenre;
    }

    public void setRealmGenre(RealmList<RealmGenre> realmGenre) {
        this.realmGenre = realmGenre;
    }

    public RealmList<RealmCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(RealmList<RealmCharacter> characters) {
        this.characters = characters;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}

