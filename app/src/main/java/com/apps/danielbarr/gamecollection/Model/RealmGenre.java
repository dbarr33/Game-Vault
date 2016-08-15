package com.apps.danielbarr.gamecollection.Model;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.NameInterface;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RealmGenre extends RealmObject implements NameInterface {
    @Required
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
