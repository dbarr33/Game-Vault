package com.apps.danielbarr.gamecollection.Model;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RecyclerObject {

    private int ID;
    private String name;
    private byte[] photo;
    private byte[] largePhoto;
    private String description;
    private ArrayList<Enemy> enemies;
    private boolean photosLoaded;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getLargePhoto() {
        return largePhoto;
    }

    public void setLargePhoto(byte[] largePhoto) {
        this.largePhoto = largePhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setEnemies(RealmList<Enemy> enemies) {

        this.enemies = new ArrayList<>();

        for(int i = 0; i < enemies.size(); i++) {
            this.enemies.add(enemies.get(i));
        }
    }

    public boolean isPhotosLoaded() {
        return photosLoaded;
    }

    public void setPhotosLoaded(boolean photosLoaded) {
        this.photosLoaded = photosLoaded;
    }
}
