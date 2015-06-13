package com.apps.danielbarr.gamecollection.Old;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 1/26/15.
 */
public class GameListAdapter extends ArrayAdapter<RealmGame> {

    private Context context;
    private Realm realm;
    private ArrayList<RealmGame> realmGames;


    public GameListAdapter(Context context, ArrayList<RealmGame> realmGames, Context applicationContext) {
        super(context, 0, realmGames);
        this.context = context;
        this.realmGames = realmGames;
        realm = Realm.getInstance(applicationContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.game_list_item, null);
        }

        RealmGame realmGame = getItem(position);

        TextView gameNameTextView = (TextView) convertView.findViewById(R.id.list_item_gameName);
        ImageView gamePhotoImageView = (ImageView) convertView.findViewById(R.id.list_item_gamePhoto);
        RatingBar gameUserRatingBar = (RatingBar) convertView.findViewById(R.id.list_item_UserRating);
        TextView gameCompletionTextView = (TextView) convertView.findViewById(R.id.list_item_CompletionPercentage);
        TextView gameIgnRating = (TextView) convertView.findViewById(R.id.edit_game_ign_rating);
        TextView gameIgnDescription = (TextView) convertView.findViewById(R.id.edit_game_ign_description);

        gameNameTextView.setText(realmGame.getName());
        gameUserRatingBar.setRating(realmGame.getUserRating());
        gameCompletionTextView.setText("Completion: " + Float.toString(realmGame.getCompletionPercentage()) + "%");
        gameIgnDescription.setText(realmGame.getDescription());

        Bitmap bmp = BitmapFactory.decodeByteArray(realmGame.getPhoto(), 0, realmGame.getPhoto().length);
        gamePhotoImageView.setImageBitmap(bmp);


        if(gameIgnRating.getText().toString().trim().matches("") || gameIgnRating.getText().toString().trim().matches("0.0"))
        {
            gameIgnRating.setVisibility(View.GONE);
            convertView.findViewById(R.id.list_item_ign_label).setVisibility(View.GONE);
        }
        else
        {
            gameIgnRating.setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.list_item_ign_label).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public void updateGameList(String platform) {
        RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", platform).equalTo("isDeleted", false) .findAll();
        realmGames.clear();

        if (!storedRealmGames.isEmpty()) {
            for (int i = 0; i < storedRealmGames.size(); i++) {
                realmGames.add(storedRealmGames.get(i));
            }
        }

        this.notifyDataSetChanged();
    }
}
