package com.apps.danielbarr.gamecollection.Uitilites;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Daniel Barr (Fuzz)
 */
public class IgnRestClient {

    private static IgnInterface REST_CLIENT;
    private static String ROOT = "https://videogamesrating.p.mashape.com";

    static {
        setupRestClient();
    }

    private IgnRestClient() {}

    public static IgnInterface get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);


        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(IgnInterface.class);
    }
}

