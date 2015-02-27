package com.apps.danielbarr.gamecollection.Model;

import io.realm.RealmObject;

/**
 * Created by danielbarr on 1/17/15.
 */

public class Game extends RealmObject{

    private String name;
    private String platform;
    private String description;
    private String genre;
    private float completionPercentage;
    private float userRating;
    private float ignRating;
    private byte[] photo;

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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
}
