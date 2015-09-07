package com.apps.danielbarr.gamecollection.Adapter;

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
import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AddFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.HideFragmentCommand;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private ArrayList<RealmGame> realmGames;
    private OnItemClickListener onClickListener;
    private int maxSize;
    private String platform;

    public GameListAdapter(ArrayList<RealmGame> realmGames, final String console) {
        this.realmGames = realmGames;
        this.platform = console;

        maxSize = realmGames.size();

        SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AddFragmentCommand addFragmentCommand = new AddFragmentCommand(EditGameFragment.newInstance(platform, position), GameApplication.getActivity());
                addFragmentCommand.execute();
                HideFragmentCommand hideFragmentCommand = new HideFragmentCommand(GameApplication.getActivity(), GameListFragment.class.getName());
                hideFragmentCommand.execute();
            }
        });
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void removeGame(int position) {
        realmGames.remove(position);
        maxSize = realmGames.size();
    }

    public void addGame(int position, RealmGame game) {
        realmGames.add(position, game);
        maxSize = realmGames.size();
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_game_list_item, viewGroup, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder gameViewHolder, final int i) {

        gameViewHolder.name.setText(realmGames.get(i).getName());
        gameViewHolder.userRating.setRating(realmGames.get(i).getUserRating());
        if(realmGames.get(i).isHasImage()) {
            gameViewHolder.gameImage.setImageBitmap(BitmapFactory.decodeByteArray(realmGames.get(i).getPhoto(), 0, realmGames.get(i).getPhoto().length));
        }
        else {
            gameViewHolder.gameImage.setImageBitmap(BitmapFactory.decodeResource(GameApplication.getActivity().getResources(), R.drawable.box_art));
        }
        gameViewHolder.completionPercentage.setText(String.valueOf(realmGames.get(i).getCompletionPercentage()) + "%");

        String description;
        if(realmGames.get(i).getDescription().length() > 500) {
            description = realmGames.get(i).getDescription().substring(0, 500);
        }
        else {
            description = realmGames.get(i).getDescription();
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
                        onClickListener.onItemClick(v, getAdapterPosition());
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
