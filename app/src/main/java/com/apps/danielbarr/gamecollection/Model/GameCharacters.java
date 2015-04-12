package com.apps.danielbarr.gamecollection.Model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameCharacters extends RealmObject implements Serializable {

    private int ID;
    private String name;
    private byte[] photo;
    private byte[] largePhoto;
    private String description;

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
}
