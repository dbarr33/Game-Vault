package com.apps.danielbarr.gamecollection.Uitilites;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombRestClient {
    private static GiantBombInterface REST_CLIENT;
    private static String ROOT = "http://www.giantbomb.com/api";

    static {
        setupRestClient();
    }

    private GiantBombRestClient() {}

    public static GiantBombInterface get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);


        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(GiantBombInterface.class);
    }
}

