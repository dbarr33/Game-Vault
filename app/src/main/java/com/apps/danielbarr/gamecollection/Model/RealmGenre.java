package com.apps.danielbarr.gamecollection.Model;

import io.realm.RealmObject;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RealmGenre extends RealmObject {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
