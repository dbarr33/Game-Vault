package com.apps.danielbarr.gamecollection.Model.GiantBomb.Search;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SearchResponse {

    ArrayList<GiantBombSearch> results;

    public ArrayList<GiantBombSearch> getResults() {
        return results;
    }

    public void setResults(ArrayList<GiantBombSearch> results) {
        this.results = results;
    }
}
