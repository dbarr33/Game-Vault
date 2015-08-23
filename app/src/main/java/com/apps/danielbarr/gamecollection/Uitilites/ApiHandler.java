package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.util.Log;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.SearchResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by danielbarr on 6/27/15.
 */
public class ApiHandler   {

    private Activity activity;
    private Dialog mDialog;

    public ApiHandler(Activity activity){
        this.activity = activity;
    }
    public void getSearchGiantBomb(String gameName,  final Callback<SearchResponse> results) {

        if(InternetUtils.isNetworkAvailable(activity)) {
            mDialog = ProgressDialog.show(this.activity, "Loading", "Wait while loading...");
            GiantBombRestClient.get().getSearchGiantBomb(GiantBombRestClient.key, GiantBombRestClient.json,
                    gameName, "game", 30, new Callback<SearchResponse>() {
                        @Override
                        public void success(SearchResponse searchResponse, Response response) {
                            mDialog.dismiss();
                            results.success(searchResponse, response);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            callFailed(error);
                            results.failure(error);
                        }
                    });
        }
        else {
            InternetUtils.showDialog(activity);
        }
    }

    public void getGameGiantBomb(int gameId, final Callback<GameResponse> results) {
        if(InternetUtils.isNetworkAvailable(activity)) {
            mDialog = ProgressDialog.show(this.activity, "Loading", "Wait while loading...");

            GiantBombRestClient.get().getGameGiantBomb(gameId, GiantBombRestClient.key, GiantBombRestClient.json,
                    new Callback<GameResponse>() {
                        @Override
                        public void success(GameResponse gameResponse, Response response) {
                            mDialog.dismiss();
                            results.success(gameResponse, response);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            callFailed(error);
                            results.failure(error);
                        }
                    });
        }
        else{
            InternetUtils.showDialog(activity);
        }
    }

    public void getCharacterGiantBomb(int gameId, final Callback<CharacterResponse> results) {
        if(InternetUtils.isNetworkAvailable(activity)) {
            GiantBombRestClient.get().getCharacterGiantBomb(gameId, GiantBombRestClient.key, GiantBombRestClient.json,
                new Callback<CharacterResponse>() {
                    @Override
                    public void success(CharacterResponse characterResponse, Response response) {
                        results.success(characterResponse, response);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        callFailed(error);
                        results.failure(error);
                    }
                });
        }
        else {
            InternetUtils.showDialog(activity);
        }
    }

    private void callFailed(RetrofitError error){
        mDialog.dismiss();
        if (!InternetUtils.isNetworkAvailable(activity)) {
            InternetUtils.showDialog(activity);
        }
        Log.e("Giant", error.getMessage() + " occurred");
    }

    public void dismissDialog() {
        if(mDialog != null) {
            if(mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }
}
