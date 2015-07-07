package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.util.Log;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */
public class BuildGameCharacter {

    public static void getCharacterInfo(int id, String name, final Callback<RealmCharacter> callback, final Activity activity ) {
        final ImageDownloadManager<Integer> imageDownloadManager = new ImageDownloadManager();
        final RealmCharacter realmCharacter = new RealmCharacter();
        realmCharacter.setID(id);
        realmCharacter.setName(name);
        imageDownloadManager.setListener(new ImageDownloader.Listener<Integer>() {
            @Override
            public void onThumbNailDownloaded(Integer position, Bitmap thumbnail) {
                imageDownloadManager.imageDownloader.clearQueue();
                imageDownloadManager.imageDownloader.quit();
                int px = PictureUtils.dpTOPX(120, activity);

                if (thumbnail != null) {
                    Bitmap bmp = PictureUtils.scaleDown(thumbnail, px, true);
                    realmCharacter.setPhoto(PictureUtils.convertBitmapToByteArray(bmp));
                }
                callback.success(realmCharacter, null);
            }
        });

        ApiHandler apiHandler = new ApiHandler(activity);
        apiHandler.getCharacterGiantBomb(realmCharacter.getID(), new Callback<CharacterResponse>() {
            @Override
            public void success(CharacterResponse characterResponse, Response response) {
                if (characterResponse.getResults().getImage() != null) {
                    imageDownloadManager.queueThumbnail(0, characterResponse.getResults().getImage().getThumb_url());
                }

                if (characterResponse.getResults().getDescription() == null) {
                    realmCharacter.setDescription("");
                } else {
                    realmCharacter.setDescription(HTMLUtil.stripHtml(characterResponse.getResults().getDescription()));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
