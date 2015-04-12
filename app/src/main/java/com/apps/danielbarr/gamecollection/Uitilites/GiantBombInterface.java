package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.SearchResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author Daniel Barr (Fuzz)
 */
public interface GiantBombInterface {

    @GET("/search")
    void getSearchGiantBomb(
            @Query("api_key") String apiKey,
            @Query("format") String format,
            @Query("query") String gameName,
            @Query("resources") String gameResource,
            @Query("limit") int limit,
            Callback<SearchResponse> results);

    @GET("/game/{path}")
    void getGameGiantBomb(
            @Path("path") int gameId,
            @Query("api_key") String apiKey,
            @Query("format") String format,
            Callback<GameResponse> results);

    @GET("/character/{path}")
    void getCharacterGiantBomb(
            @Path("path") int gameId,
            @Query("api_key") String apiKey,
            @Query("format") String format,
            Callback<CharacterResponse> results);
}
