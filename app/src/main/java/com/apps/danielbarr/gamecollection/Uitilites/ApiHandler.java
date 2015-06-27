package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.SearchResponse;

import retrofit.Callback;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by danielbarr on 6/27/15.
 */
public class ApiHandler implements GiantBombInterface {
    @Override
    public void getSearchGiantBomb(@Query("api_key") String apiKey, @Query("format") String format, @Query("query") String gameName, @Query("resources") String gameResource, @Query("limit") int limit, Callback<SearchResponse> results) {

    }

    @Override
    public void getGameGiantBomb(@Path("path") int gameId, @Query("api_key") String apiKey, @Query("format") String format, Callback<GameResponse> results) {

    }

    @Override
    public void getCharacterGiantBomb(@Path("path") int gameId, @Query("api_key") String apiKey, @Query("format") String format, Callback<CharacterResponse> results) {

    }
}
