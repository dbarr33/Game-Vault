package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character.CharacterResponse;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameCharacterBuilder {

    public static void getCharacterInfo(int id, String name, final Callback<RealmCharacter> callback) {
        final RealmCharacter realmCharacter = new RealmCharacter();
        realmCharacter.setID(id);
        realmCharacter.setName(name);

        ApiHandler apiHandler = ApiHandler.getInstance();
        apiHandler.getCharacterGiantBomb(realmCharacter.getID(), new Callback<CharacterResponse>() {
            @Override
            public void success(CharacterResponse characterResponse, Response response) {
                if (characterResponse.getResults().getImage() != null) {
                    realmCharacter.setImageURL(characterResponse.getResults().getImage().getSuper_url());
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
