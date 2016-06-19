package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameCharacterBuilder {

    public static void getCharacterInfo(int id, String name, final Callback<RealmCharacter> callback, final Activity activity ) {
        final RealmCharacter realmCharacter = new RealmCharacter();
        realmCharacter.setID(id);
        realmCharacter.setName(name);

        ApiHandler apiHandler = ApiHandler.getInstance();
        apiHandler.getCharacterGiantBomb(realmCharacter.getID(), new Callback<CharacterResponse>() {
            @Override
            public void success(CharacterResponse characterResponse, Response response) {
                if (characterResponse.getResults().getImage() != null) {
                    realmCharacter.setImageURL(characterResponse.getResults().getImage().getThumb_url());
                }
                else {
                    byte[] picture =  PictureUtils
                            .convertBitmapToByteArray(((BitmapDrawable) activity.getResources().getDrawable(R.drawable.default_character)).getBitmap());
                    realmCharacter.setPhoto(picture);
                    callback.success(realmCharacter, response);
                }

                if (characterResponse.getResults().getDescription() == null) {
                    realmCharacter.setDescription("");
                } else {
                    realmCharacter.setDescription(HTMLUtil.stripHtml(characterResponse.getResults().getDescription()));
                }
                callback.success(realmCharacter, response);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
