package com.apps.danielbarr.gamecollection.Model.GiantBomb;

import java.io.Serializable;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SimilarGames implements Serializable {

    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
