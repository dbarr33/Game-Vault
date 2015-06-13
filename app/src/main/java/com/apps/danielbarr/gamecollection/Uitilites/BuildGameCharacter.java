package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Html;
import android.util.Log;

import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */
public class BuildGameCharacter {

    private Activity activity;
    private RealmCharacter gameCharacterses;
    private GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter;
    private int position;
    private ImageDownloader<Integer> thread;


    public BuildGameCharacter(Activity activity, GameCharacter characters, GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter, int position) {
        this.activity = activity;
        this.gameCharactersRecyclerAdapter = gameCharactersRecyclerAdapter;
        this.position = position;
        gameCharacterses = new RealmCharacter();
        gameCharacterses.setName(characters.getName());
        gameCharacterses.setID(characters.getId());
        getCharacterInfo(characters.getId());
    }

    public BuildGameCharacter(Activity activity, RealmCharacter characters, GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter, int position) {
        this.activity = activity;
        this.gameCharactersRecyclerAdapter = gameCharactersRecyclerAdapter;
        this.position = position;
        gameCharacterses = new RealmCharacter();
        gameCharacterses.setName(characters.getName());
        gameCharacterses.setID(characters.getID());
        getCharacterInfo(characters.getID());
    }

    public void getCharacterInfo(int id) {
        thread = new ImageDownloader<>(new Handler());
        thread.setListener(new ImageDownloader.Listener<Integer>() {
            @Override
            public void onThumbNailDownloaded(Integer position, Bitmap thumbnail) {
                int dp = 120;
                int px = PictureUtils.dpTOPX(dp, activity);

                if(thumbnail != null) {
                    Bitmap bmp = PictureUtils.scaleDown(thumbnail, px, true);

                    gameCharacterses.setPhoto(PictureUtils.convertBitmapToByteArray(bmp));
                    gameCharactersRecyclerAdapter.setCharactersAtPosition(position, gameCharacterses);
                    gameCharactersRecyclerAdapter.notifyDataSetChanged();
                }
        }});
        thread.start();
        thread.getLooper();

        GiantBombRestClient.get().getCharacterGiantBomb(id, GiantBombRestClient.key, GiantBombRestClient.json,
                new Callback<CharacterResponse>() {
                    @Override
                    public void success(CharacterResponse characterResponse, Response response) {
                        if(characterResponse.getResults().getImage() != null) {
                            thread.queueThumbnail(position, characterResponse.getResults().getImage().getThumb_url());
                        }

                        if(characterResponse.getResults().getDescription() == null) {
                            gameCharacterses.setDescription("");
                        }
                        else {
                            gameCharacterses.setDescription(stripHtml(characterResponse.getResults().getDescription()));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (!InternetUtils.isNetworkAvailable(activity)) {
                            AlertDialog.Builder dialog = InternetUtils.buildDialog(activity);
                            dialog.show();
                        }
                        Log.e("Giant", error.getMessage());
                    }
                });

    }

    public String stripHtml(String html) {
        if(html != null) {
            String temp = Html.fromHtml(html).toString();
            if(temp.length() > 4000) {
                return temp.substring(0, 4000);
            }
            else {
                return temp;
            }
        }
        else {
            return "";
        }
    }
}
