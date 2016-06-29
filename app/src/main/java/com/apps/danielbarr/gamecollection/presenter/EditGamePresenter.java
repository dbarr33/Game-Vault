package com.apps.danielbarr.gamecollection.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Developer;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameGenre;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameSimilarGames;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Publisher;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmGenre;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ApiHandler;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.ListObjectBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;

import java.util.ArrayList;

import io.realm.RealmList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by danielbarr on 8/23/15.
 */
public class EditGamePresenter implements EditGamePresenterInterface {
    private EditGameView editGameView;

    public EditGamePresenter(EditGameView editGameView){
        this.editGameView = editGameView;
    }

    @Override
    public void configureScreen(boolean hideDelete) {
        Activity activity = GameApplication.getActivity();
        activity.findViewById(R.id.toolbar).setVisibility(View.GONE);
        activity.findViewById(R.id.saveGameButton).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.floatingActionButton).setVisibility(View.GONE);

        if(hideDelete) {
            activity.findViewById(R.id.deleteGameButton).setVisibility(View.GONE);
        }
        else {
            activity.findViewById(R.id.deleteGameButton).setVisibility(View.VISIBLE);
        }

        DrawerLayout mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void fetchDataFromAPI(int id, String url) {
        ApiHandler apiHandler = ApiHandler.getInstance();
        apiHandler.getGameGiantBomb(id, new Callback<GameResponse>() {
            @Override
            public void success(GameResponse gameResponse, Response response) {
                if (gameResponse.getResults().getGameGenres() != null) {
                    createGenreData(gameResponse.getResults().getGameGenres());
                }

                if (gameResponse.getResults().getSimilar_games() != null) {
                    createSimilarGameData(gameResponse.getResults().getSimilar_games());
                }

                if(gameResponse.getResults().getPublishers() != null) {
                    createPublisherGameData(gameResponse.getResults().getPublishers());
                }

                if(gameResponse.getResults().getDevelopers() != null) {
                    createDeveloperGameData(gameResponse.getResults().getDevelopers());
                }

                if (gameResponse.getResults().getGameCharacters() != null) {
                    if (gameResponse.getResults().getGameCharacters().size() <= 10) {
                        editGameView.configureCharacterRecyclerView(new GameCharactersRecyclerAdapter(gameResponse.getResults().getGameCharacters()));
                    } else {
                        editGameView.configureCharacterRecyclerView(new GameCharactersRecyclerAdapter(new ArrayList<>(gameResponse.getResults().getGameCharacters().subList(0, 10))));
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        if (url != null) {
            editGameView.setupGameImages(url);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(GameApplication.getActivity().getResources(), R.drawable.box_art);
            //editGameView.setupGameImages(bitmap, null);
            editGameView.hideProgressBar();
        }
    }

    @Override
    public void loadDataFromRealm(String platform, int position) {
        editGameView.populateFields(RealmManager.getInstance().getRealmGameByPosition(platform, position));
    }

    @Override
    public void setupToolbar() {
        Toolbar toolbar = (Toolbar)GameApplication.getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);
        toolbar.setVisibility(View.VISIBLE);
        ((Main)GameApplication.getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void saveGame(RealmGame realmGame) {
        RealmManager.getInstance().saveGame(realmGame);
        editGameView.showConfirmationMessage(GameApplication.getResourceString(R.string.snackbar_text_save));
        transitionBack();
    }

    @Override
    public void updateGame(RealmGame realmGame, String platform, int position) {
        RealmManager.getInstance().updateGame(realmGame, platform, position);
        editGameView.showConfirmationMessage(GameApplication.getResourceString(R.string.snackbar_text_update));
        transitionBack();
    }

    @Override
    public void removeGame(String platform, int position) {
        RealmGame realmGame = RealmManager.getInstance().getRealmGameByPosition(platform, position);
        ((Main)GameApplication.getActivity()).removeGame(position, realmGame);
        transitionBack();
    }

    private void transitionBack() {
        GameApplication.getActivity().onBackPressed();
    }

    public void createGenreData(ArrayList<GameGenre> genres) {
        editGameView.configureGeneRecyclerView(ListObjectBuilder.createArrayList(GameApplication.getResourceString(R.string.recycler_header_genres), (ArrayList) genres));
    }

    public void createGenreData(RealmList<RealmGenre> realmGenres) {
        editGameView.configureGeneRecyclerView(ListObjectBuilder.createArrayList(GameApplication.getResourceString(R.string.recycler_header_genres), (RealmList) realmGenres));
    }

    public void createSimilarGameData(ArrayList<GameSimilarGames> genres) {
        editGameView.configureSimilarGamesRecyclerView(ListObjectBuilder.createArrayList(GameApplication.getResourceString(R.string.recycler_header_similar_games), (ArrayList) genres));
    }

    public void createSimilarGameData(RealmList<RealmGame> realmGenres) {
        editGameView.configureSimilarGamesRecyclerView(ListObjectBuilder.createArrayList(GameApplication.getResourceString(R.string.recycler_header_similar_games), (RealmList) realmGenres));
    }

    public void createPublisherGameData(ArrayList<Publisher> realmPublisher) {
        editGameView.configurePublisherRecyclerView(ListObjectBuilder.createArrayList("Publishers", (ArrayList)realmPublisher));
    }

    public void createPublisherGameData(RealmList<RealmPublisher> realmPublisher) {
        editGameView.configurePublisherRecyclerView(ListObjectBuilder.createArrayList("Publishers", (RealmList)realmPublisher));
    }

    public void createDeveloperGameData(ArrayList<Developer> developers) {
        editGameView.configureDeveloperRecyclerView(ListObjectBuilder.createArrayList("Developers", (ArrayList)developers));
    }
}
