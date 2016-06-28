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
import com.apps.danielbarr.gamecollection.Fragments.FilterFragment;
import com.apps.danielbarr.gamecollection.Fragments.GameListFragment;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.Model.SortType;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AddFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.HideFragmentCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private ArrayList<RealmGame> realmGames;
    private OnItemClickListener onClickListener;
    private ArrayList<RealmGame> filteredList;
    private String platform;
    private FilterState currentFilterState;

    public GameListAdapter(ArrayList<RealmGame> realmGames, final String console) {
        this.realmGames = realmGames;
        this.filteredList = realmGames;
        this.platform = console;
        currentFilterState = new FilterState();
        currentFilterState.setSortType(SortType.DATE);
        currentFilterState.setSelectedPublisher(FilterFragment.NO_SELECTION);

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
    }

    public void addGame(int position, RealmGame game) {
        realmGames.add(position, game);
    }

    private void filterListAlpha() {
        Collections.sort(filteredList, new Comparator<RealmGame>() {
            @Override
            public int compare(RealmGame lhs, RealmGame rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        notifyDataSetChanged();
    }

    public void applyFilter(FilterState filterState) {
        if (filterState.getSelectedPublisher() != null){
            if(!filterState.getSelectedPublisher().matches(currentFilterState.getSelectedPublisher())) {
                currentFilterState.setSelectedPublisher(filterState.getSelectedPublisher());
                filterByPublisher(filterState.getSelectedPublisher());
            }
        }

        if(filterState.getSortType() != currentFilterState.getSortType()) {
            currentFilterState.setSortType(filterState.getSortType());
            if (filterState.getSortType() == SortType.ALPHA) {
                filterListAlpha();
            } else {
                filterBySaveDate();
            }
        }
    }

    private void filterBySaveDate() {
        Collections.sort(filteredList, new Comparator<RealmGame>() {
            @Override
            public int compare(RealmGame lhs, RealmGame rhs) {
                return Long.compare(lhs.getDate(), rhs.getDate());
            }
        });
        notifyDataSetChanged();
    }

    private void filterByPublisher(String publisherName) {
        if(!publisherName.matches(FilterFragment.NO_SELECTION)) {
            filteredList = new ArrayList<>();
            for (RealmGame realmGame : realmGames) {
                for (RealmPublisher realmPublisher : realmGame.getPublishers()) {
                    if (realmPublisher.getName().trim().matches(publisherName.trim())) {
                        filteredList.add(realmGame);
                        break;
                    }
                }
            }
        }
        else {
            filteredList = realmGames;
        }
        notifyDataSetChanged();
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_game_list_item, viewGroup, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder gameViewHolder, final int i) {

        gameViewHolder.name.setText(filteredList.get(i).getName());
        gameViewHolder.userRating.setRating(filteredList.get(i).getUserRating());
        if(filteredList.get(i).isHasImage()) {
            gameViewHolder.gameImage.setImageBitmap(BitmapFactory.decodeByteArray(filteredList.get(i).getPhoto(), 0, filteredList.get(i).getPhoto().length));
        }
        else {
            gameViewHolder.gameImage.setImageBitmap(BitmapFactory.decodeResource(GameApplication.getActivity().getResources(), R.drawable.box_art));
        }
        gameViewHolder.completionPercentage.setText(String.valueOf(filteredList.get(i).getCompletionPercentage()) + "%");

        String description;
        if(filteredList.get(i).getDescription() != null) {
            if (filteredList.get(i).getDescription().length() > 500) {
                description = filteredList.get(i).getDescription().substring(0, 500);
            } else {
                description = filteredList.get(i).getDescription();
            }
            gameViewHolder.description.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
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
                        String gameName = filteredList.get(getAdapterPosition()).getName();
                        if(gameName.matches(realmGames.get(getAdapterPosition()).getName())) {
                            onClickListener.onItemClick(v, getAdapterPosition());
                        }
                        else {
                            for(int i = 0 ; i < realmGames.size(); i++) {
                                RealmGame tempGame = realmGames.get(i);
                                if(gameName.matches(tempGame.getName())){
                                    onClickListener.onItemClick(v, i);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onClickListener = mItemClickListener;
    }
}
