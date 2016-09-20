package com.apps.danielbarr.gamecollection.Model;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.NameInterface;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RealmPublisher extends RealmObject implements NameInterface {

    @Index
    private String name;
    private String consoleName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsoleName() {
        return consoleName;
    }

    public void setConsoleName(String consoleName) {
        this.consoleName = consoleName;
    }
}
