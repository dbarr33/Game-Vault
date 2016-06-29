package com.apps.danielbarr.gamecollection.Model.GiantBomb.Game;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Developer;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Publisher;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombGame {
    public ArrayList<GameGenre> genres;
    public ArrayList<GameCharacter> characters;
    public ArrayList<GameSimilarGames> similar_games;
    public ArrayList<Publisher> publishers;
    public ArrayList<Developer> developers;

    public ArrayList<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(ArrayList<Developer> developers) {
        this.developers = developers;
    }

    public ArrayList<Publisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(ArrayList<Publisher> publishers) {
        this.publishers = publishers;
    }

    public ArrayList<GameGenre> getGameGenres() {
        return genres;
    }

    public void setGameGenres(ArrayList<GameGenre> gameGenres) {
        this.genres = gameGenres;
    }

    public ArrayList<GameCharacter> getGameCharacters() {
        return characters;
    }

    public void setGameCharacters(ArrayList<GameCharacter> gameCharacters) {
        this.characters = gameCharacters;
    }

    public ArrayList<GameSimilarGames> getSimilar_games() {
        return similar_games;
    }

    public void setSimilar_games(ArrayList<GameSimilarGames> similar_games) {
        this.similar_games = similar_games;
    }
}
