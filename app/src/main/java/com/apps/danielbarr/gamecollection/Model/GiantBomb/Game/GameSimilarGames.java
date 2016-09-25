package com.apps.danielbarr.gamecollection.Model.GiantBomb.Game;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.BaseObject;

import java.io.Serializable;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameSimilarGames extends BaseObject implements Serializable {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
