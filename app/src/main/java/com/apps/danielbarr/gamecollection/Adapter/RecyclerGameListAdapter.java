package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RecyclerGameListAdapter extends RecyclerView.Adapter<RecyclerGameListAdapter.GameViewHolder> {

    private ArrayList<Game> games;
    private OnItemClickListener onClickListener;
    private int maxSize;
    private String platform;
    ArrayList<ImageView> imageViews;

    public RecyclerGameListAdapter(ArrayList<Game> games, final Activity activity, final String console) {
        this.games = games;
        this.platform = console;

        maxSize = games.size();

        SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                EditGameFragment editGameFragment = EditGameFragment.newInstance(platform, position);
               // editGameFragment.setEnterTransition(new Slide());
               // editGameFragment.setSharedElementEnterTransition(new Slide());
                activity.getFragmentManager().beginTransaction()
                        .hide(activity.getFragmentManager().findFragmentByTag(activity.getResources().getString(R.string.fragment_game_list))).commit();

                activity.getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, editGameFragment, activity.getResources().getString(R.string.fragment_edit_game))
                        .addToBackStack(null).commit();
                       // .addSharedElement(activity.findViewById(R.id.recycler_gameList_gameName), "gameName")
                       // .addSharedElement(activity.findViewById(R.id.recycler_gameList_gameImage), "gameImage").commit();
            }
        });
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_game_list_item, viewGroup, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder gameViewHolder, final int i) {

        gameViewHolder.name.setText(games.get(i).getName());
        gameViewHolder.userRating.setRating(games.get(i).getUserRating());
        gameViewHolder.gameImage.setImageBitmap(BitmapFactory.decodeByteArray(games.get(i).getPhoto(), 0, games.get(i).getPhoto().length));
        gameViewHolder.completionPercentage.setText(String.valueOf(games.get(i).getCompletionPercentage()) + "%");

        String description;
        if(games.get(i).getDescription().length() > 500) {
            description = games.get(i).getDescription().substring(0, 500);
        }
        else {
            description = games.get(i).getDescription();
        }
        gameViewHolder.description.setText(description);

    }

    @Override
    public int getItemCount() {
        return maxSize;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder  {
        protected TextView name;
        protected CardView cardView;
        protected ImageView gameImage;
        protected TextView description;
        protected RatingBar userRating;
        protected TextView completionPercentage;

        public GameViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.recycler_gameList_gameName);
            cardView = (CardView)itemView.findViewById(R.id.recycler_gameList_cardView);
            gameImage = (ImageView)itemView.findViewById(R.id.recycler_gameList_gameImage);
            description = (TextView)itemView.findViewById(R.id.recycler_gameList_description);
            userRating = (RatingBar)itemView.findViewById(R.id.recycler_gameList_userRating);
            completionPercentage = (TextView)itemView.findViewById(R.id.completionPercentage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickListener != null) {
                        onClickListener.onItemClick(v, getPosition());
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onClickListener = mItemClickListener;
    }
}
