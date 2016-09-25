package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.SearchResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author Daniel Barr (Fuzz)
 */
public interface GiantBombInterface {

    @Headers({
            "User-Agent: Game Vault Android App"
    })
    @GET("/search")
    void getSearchGiantBomb(
            @Query("api_key") String apiKey,
            @Query("format") String format,
            @Query("query") String gameName,
            @Query("resources") String gameResource,
            @Query("limit") int limit,
            Callback<SearchResponse> results);

    @Headers({
            "User-Agent: Game Vault Android App"
    })
    @GET("/game/{path}")
    void getGameGiantBomb(
            @Path("path") int gameId,
            @Query("api_key") String apiKey,
            @Query("format") String format,
            Callback<GameResponse> results);

    @Headers({
            "User-Agent: Game Vault Android App"
    })
    @GET("/character/{path}")
    void getCharacterGiantBomb(
            @Path("path") int gameId,
            @Query("api_key") String apiKey,
            @Query("format") String format,
            Callback<CharacterResponse> results);
}
