package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.CharacterResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */
public class BuildGameCharacter {

    private Activity activity;
    private GameCharacters gameCharacterses;
    private GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter;
    private int position;
    private int count = 0;


    public BuildGameCharacter(Activity activity, com.apps.danielbarr.gamecollection.Model.GiantBomb.Character characters, GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter, int position) {
        this.activity = activity;
        this.gameCharactersRecyclerAdapter = gameCharactersRecyclerAdapter;
        this.position = position;
        gameCharacterses = new GameCharacters();
        gameCharacterses.setName(characters.getName());
        gameCharacterses.setID(characters.getId());
        getCharacterInfo(characters.getId());
    }

    public BuildGameCharacter(Activity activity, GameCharacters characters, GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter, int position) {
        this.activity = activity;
        this.gameCharactersRecyclerAdapter = gameCharactersRecyclerAdapter;
        this.position = position;
        gameCharacterses = new GameCharacters();
        gameCharacterses.setName(characters.getName());
        gameCharacterses.setID(characters.getID());
        getCharacterInfo(characters.getID());
    }

    public void getCharacterInfo(int id) {
        GiantBombRestClient.get().getCharacterGiantBomb(id, "2b5563f0a5655a6ef2e0a4d0556d2958cced098d", "json",
                new Callback<CharacterResponse>() {
                    @Override
                    public void success(CharacterResponse characterResponse, Response response) {
                        if(characterResponse.getResults().getImage() != null) {
                            new DownloadAsyncTask(false).execute(characterResponse.getResults().getImage().getThumb_url());
                            new DownloadAsyncTask(true).execute(characterResponse.getResults().getImage().getSuper_url());
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
        return Html.fromHtml(html).toString();
    }


    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        WeakReference isLargePhoto;

       public DownloadAsyncTask(Boolean isLargePhoto) {
           this.isLargePhoto = new WeakReference(isLargePhoto);

       }
        @Override
        protected Bitmap doInBackground(String... params) {

            String URL = params[0];
            Bitmap bitmap = null;
            try {
                java.net.URL imageURL = new java.net.URL(URL);

                BufferedInputStream bis = new BufferedInputStream(imageURL.openStream(), 10240);
                bitmap = BitmapFactory.decodeStream(bis);
                bis.close();

            } catch (MalformedURLException e) {
                Log.e("error", "Downloading Image Failed");

            }
            catch (IOException e)
            {
                Log.e("error", "Downloading Image Failed");
            }

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            count++;
            if (result != null) {
                if ((boolean) isLargePhoto.get()) {
                    gameCharacterses.setLargePhoto(PictureUtils.convertBitmapToByteArray(result));
                }
                else {
                    gameCharacterses.setPhoto(PictureUtils.convertBitmapToByteArray(result));
                }

                if(count == 2) {
                    gameCharactersRecyclerAdapter.setCharactersAtPosition(position, gameCharacterses);
                }
            }
        }
    }
}
