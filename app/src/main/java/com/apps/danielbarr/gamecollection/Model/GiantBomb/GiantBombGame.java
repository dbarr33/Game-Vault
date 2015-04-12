package com.apps.danielbarr.gamecollection.Model.GiantBomb;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombGame {
    public ArrayList<Genre> genres;
    public ArrayList<Character> characters;

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }
}
