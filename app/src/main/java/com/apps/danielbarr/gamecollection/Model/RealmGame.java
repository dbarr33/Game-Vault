package com.apps.danielbarr.gamecollection.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by danielbarr on 1/17/15.
 */

public class RealmGame extends RealmObject{

    private String name;
    private String platform;
    private String description;
    private RealmList<RealmGenre> realmGenre;
    private String photoURL;
    private float completionPercentage;
    private float userRating;
    private float ignRating;
    private byte[] photo;
    private RealmList<RealmCharacter> characterses;
    private boolean isDeleted = false;
    private RealmList<RealmGame> similarRealmGames;

    public RealmList<RealmGame> getSimilarRealmGames() {
        return similarRealmGames;
    }

    public void setSimilarRealmGames(RealmList<RealmGame> similarRealmGames) {
        this.similarRealmGames = similarRealmGames;
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

    public float getIgnRating() {
        return ignRating;
    }

    public void setIgnRating(float mataCriticRating) {
        this.ignRating = mataCriticRating;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public RealmList<RealmCharacter> getCharacterses() {
        return characterses;
    }

    public void setCharacterses(RealmList<RealmCharacter> characterses) {
        this.characterses = characterses;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    //public RealmList<RealmGame> getSimilarGames() {
      //  return similarRealmGames;
    //}

    //public void setSimilarGames(RealmList<RealmGame> similarRealmGames) {
    //    this.similarRealmGames = similarRealmGames;
   // }
}

