package com.apps.danielbarr.gamecollection.Model.GiantBomb;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombGame {
    public ArrayList<Genre> genres;
    public ArrayList<Character> characters;
    public ArrayList<SimilarGames> similar_games;

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

    public ArrayList<SimilarGames> getSimilar_games() {
        return similar_games;
    }

    public void setSimilar_games(ArrayList<SimilarGames> similar_games) {
        this.similar_games = similar_games;
    }
}
