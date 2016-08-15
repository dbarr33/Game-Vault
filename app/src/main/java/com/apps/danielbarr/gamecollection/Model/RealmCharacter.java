package com.apps.danielbarr.gamecollection.Model;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.NameInterface;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RealmCharacter extends RealmObject implements Serializable, NameInterface {

    private int ID;
    @Required
    private String name;
    @Required
    private byte[] photo;
    @Required
    private String description;
    private boolean photosLoaded;
    private String imageURL;

    public RealmCharacter(){

    }

    public RealmCharacter(RealmCharacter realmCharacter){
        this.setDescription(realmCharacter.getDescription());
        this.setID(realmCharacter.getID());
        this.setName(realmCharacter.getName());
        this.setPhoto(realmCharacter.getPhoto());
        this.setPhotosLoaded(realmCharacter.isPhotosLoaded());
        this.setImageURL(realmCharacter.getImageURL());
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
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

}
