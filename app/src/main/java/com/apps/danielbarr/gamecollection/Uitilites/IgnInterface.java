package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.IgnResponse;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * @author Daniel Barr (Fuzz)
 */
public interface IgnInterface {

    @Headers({"X-Mashape-Key: LyqQ22KvW7mshYfhVGtywEd0ZsQkp1VMTxKjsnUpApz3AnNC0K",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: application/json"})
    @GET("/get.php")
    void getIgn(@Query("count") int count,
                       @Query("game") String gameTitle,
                       retrofit.Callback <ArrayList<IgnResponse>>callback);
}

