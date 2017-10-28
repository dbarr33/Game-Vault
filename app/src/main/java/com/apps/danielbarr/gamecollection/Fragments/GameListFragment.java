package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.GameListAdapter;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AnalyticsTracker;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;
import com.apps.danielbarr.gamecollection.Uitilites.SnackbarBuilder;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */

public class GameListFragment extends Fragment {

    public static final String GAME_PLATFORM = "gamePlatform";

    private RecyclerView gameListRecycler;
    private LinearLayout emptyView;
    private TextView emptyTextview;

    private GameListAdapter gameListAdapter;
    private String platform;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        platform = getArguments().getString(GAME_PLATFORM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycleview_game_list, container, false);
        gameListRecycler = (RecyclerView) v.findViewById(R.id.recycler_gameList);
        emptyView = (LinearLayout) v.findViewById(R.id.emptyView);
        emptyTextview = (TextView)v.findViewById(R.id.emptyTextView);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setGameList(platform);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameListRecycler.setLayoutManager(linearLayoutManager);
        setUpSwipeToDismiss();
    }

    public void setGameList(String newPlatform) {
        platform = newPlatform;
        ArrayList<RealmGame> storedRealmGames = RealmManager.getInstance().getGames(platform);
        gameListAdapter = new GameListAdapter(storedRealmGames, platform, new GameListAdapter.FilterableAdapter() {
            @Override
            public void noFilterResults() {
                displayEmptyView("No filtered results");
            }

            @Override
            public void hasResults() {
                hideEmptyView();
            }
        });
        gameListRecycler.setAdapter(gameListAdapter);
        if (storedRealmGames.size() > 0) {
            hideEmptyView();
        } else {
            displayEmptyView(getString(R.string.empty_text));
        }
    }

    public void displayEmptyView(String text) {
        emptyView.setVisibility(View.VISIBLE);
        emptyTextview.setText(text);
    }

    public void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }

    public void notifyDataSetChanged() {
        setGameList(platform);
    }

    @Override
    public void onResume() {
        if (!((Main) getActivity()).getSupportActionBar().isShowing()) {
            ((Main) getActivity()).getSupportActionBar().show();
        }
        super.onResume();
    }

    private void setUpSwipeToDismiss() {
        // init swipe to dismiss logic
        final ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter
                int position = viewHolder.getAdapterPosition();
                RealmGame realmGame = RealmManager.getInstance().getRealmGameByPosition(platform, position);
                removeGame(position, realmGame);
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(gameListRecycler);
    }

    public void removeGame(final int position, RealmGame realmGame) {
        new AnalyticsTracker(getActivity()).sendEvent("Game List", "Game Deleted",
            realmGame.getName());
        final RealmGame temp = new RealmGame(realmGame);

        gameListAdapter.removeGame(position);
        gameListAdapter.notifyItemRemoved(position);
        if (gameListAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        RealmManager.getInstance().removeGame(realmGame);

        SnackbarBuilder snackbarBuilder = new SnackbarBuilder(getView(), GameApplication.getResourceString(R.string.snackbar_text_deleted));
        snackbarBuilder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AnalyticsTracker(getActivity()).sendEvent("Game List", "Undo Game Deleted",
                    temp.getName());
                RealmManager.getInstance().saveGame(temp);
                gameListAdapter.addGame(position, temp);
                gameListAdapter.notifyItemInserted(position);
                gameListRecycler.scrollToPosition(position);
                emptyView.setVisibility(View.GONE);
            }
        });
        snackbarBuilder.show();
    }

    public void applyFilter(FilterState filterState) {
        if(gameListAdapter != null) {
            gameListAdapter.applyFilter(filterState);
        }
    }
}
