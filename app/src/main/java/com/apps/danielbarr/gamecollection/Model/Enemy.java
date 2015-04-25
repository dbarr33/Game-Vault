package com.apps.danielbarr.gamecollection.Model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * @author Daniel Barr (Fuzz)
 */
public class Enemy extends RealmObject implements Serializable {

    private int ID;
    private String name;

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
}
