package com.apps.danielbarr.gamecollection.Model;

import android.text.TextUtils;
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
    private String description;
    private String imageURL;
    private byte[] photo;

    public RealmCharacter(){
        this.description = "";
    }

    public RealmCharacter(RealmCharacter realmCharacter){
        if(!TextUtils.isEmpty(realmCharacter.getDescription())) {
            this.setDescription(realmCharacter.getDescription());
        } else {
            this.description = "";
        }
        this.setID(realmCharacter.getID());
        this.setName(realmCharacter.getName());
        this.setImageURL(realmCharacter.getImageURL());
        this.photo = (realmCharacter.getPhoto());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(!TextUtils.isEmpty(description)) {
            this.description = description;
        }
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
