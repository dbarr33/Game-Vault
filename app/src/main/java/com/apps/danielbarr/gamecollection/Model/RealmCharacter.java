package com.apps.danielbarr.gamecollection.Model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RealmCharacter extends RealmObject implements Serializable {

    private int ID;
    private String name;
    private byte[] photo;
    private String description;
   // private RealmList<Enemy> enemies;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPhotosLoaded() {
        return photosLoaded;
    }

    public void setPhotosLoaded(boolean photosLoaded) {
        this.photosLoaded = photosLoaded;
    }

 //   public RealmList<Enemy> getEnemies() {
 //       return enemies;
 //   }

//    public void setEnemies(RealmList<Enemy> enemies) {
//        this.enemies = enemies;
//    }
//    public void setEnemies(ArrayList<Enemy> enemies) {
//
//        if(this.enemies != null) {
//            this.enemies.clear();
//        }
//        for(int i = 0; i < enemies.size(); i++) {
//            this.enemies.add(enemies.get(i));
//        }
//    }
}
