package com.apps.danielbarr.gamecollection.Uitilites;

import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.io.IOException;
import java.util.List;

/**
 * Created by danielbarr on 1/25/15.
 */
public class CustomSearch {
    public static final String API_KEY = "AIzaSyCkgJDcUUFEBr0c82-bCx1ep55eOplgm40";
    public static final String PARAM_NUM = "1";
    public static final String PARAM_FILE_TYPE = "png";
    public static final String PARAM_CX = "001226080981610463359:5wnewnz0yao";

    public String fetchPhoto(String query) {

        try {
            HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            };

        com.google.api.client.json.JsonFactory jsonFactory = new JacksonFactory();
        HttpTransport httpTransport = new NetHttpTransport();

        Customsearch customsearch = new Customsearch.Builder(httpTransport, jsonFactory, httpRequestInitializer)
                .setApplicationName("Game Collection")
                .build();

        Customsearch.Cse.List list = customsearch.cse().list(query);
        list.setKey(API_KEY);
        list.setCx(PARAM_CX);
        list.setNum(Long.parseLong(PARAM_NUM));
        list.setFileType(PARAM_FILE_TYPE);
        list.setSearchType("image");
        list.setImgType("photo");
        Search results = list.execute();
        List<Result> items = results.getItems();

        String newurl = items.get(0).getLink();
        Log.e("Custom search", newurl);
        return items.get(0).getLink();
    } catch (IOException e) {
        e.printStackTrace();
    }


        return null;
    }
}
