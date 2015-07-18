package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmGenre;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ApiHandler;
import com.apps.danielbarr.gamecollection.Uitilites.HTMLUtil;
import com.apps.danielbarr.gamecollection.Uitilites.ImageDownloadManager;
import com.apps.danielbarr.gamecollection.Uitilites.ImageDownloader;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
import com.apps.danielbarr.gamecollection.Uitilites.SnackbarBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.StringArrayListBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by danielbarr on 1/19/15.
 */
public class EditGameFragment extends Fragment {

    public static final String EXTRA_GAME = "com.apps.danielbarr.gamecollection.game";
    public static final String EXTRA_PLATFORM = "com.apps.danielbarr.gamecollection.platform";
    public static final String EXTRA_SEARCH = "com.apps.danielbarr.gamecollection.search";

    private ImageButton deleteGameButton;
    private ImageButton saveGameButton;
    private ImageView gameImageView;
    private ImageView blurredGameImage;
    private TextView gameName;
    private TextView completionPercentage;
    private RatingBar userRatingBar;
    public Realm realm;
    private Spinner platformSpinner;
    private RecyclerView relevantGamesRecyclerView;
    private RecyclerView gameDescriptionRecyclerView;
    private RecyclerView gameGenresRecyclerView;

    public int gamePosition;
    private String currentPlatform;
    private GiantBombSearch searchResults;

    private RecyclerView charactersRecyclerView;
    private LinearLayout recyclerLayout;
    private ProgressBar gameImageProgressBar;
    public SynchronizedScrollView mScrollView;
    private final ImageDownloadManager<Integer> imageDownloadManager = new ImageDownloadManager<Integer>();


    public static EditGameFragment newInstance(String platform, Bitmap image, GiantBombSearch giantBombSearch)
    {
        Bundle args = new Bundle();
        args.putString(EXTRA_PLATFORM, platform);
        args.putSerializable("GiantBombResponse", giantBombSearch);
        args.putParcelable(EditGameFragment.EXTRA_SEARCH, image);
        EditGameFragment fragment = new EditGameFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static EditGameFragment newInstance(String platform, int gamePosition)
    {
        Bundle args = new Bundle();
        args.putString(EXTRA_PLATFORM, platform);
        args.putSerializable(EXTRA_GAME, gamePosition);
        EditGameFragment fragment = new EditGameFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPlatform = getArguments().getString(EXTRA_PLATFORM);
        gamePosition = getArguments().getInt(EXTRA_GAME, -1);
        searchResults = (GiantBombSearch)getArguments().getSerializable("GiantBombResponse");
        realm = Realm.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_game, parent, false);
        deleteGameButton = (ImageButton)getActivity().findViewById(R.id.deleteGameButton);
        saveGameButton = (ImageButton)getActivity().findViewById(R.id.saveGameButton);
        gameImageView = (ImageView) v.findViewById(R.id.edit_game_photo);
        blurredGameImage = (ImageView)v.findViewById(R.id.blurredGameImage);
        gameName = (TextView) v.findViewById(R.id.edit_name_textField);
        platformSpinner = (Spinner) v.findViewById(R.id.edit_platform_spinner);
        userRatingBar = (RatingBar) v.findViewById(R.id.userRatingStars);
        completionPercentage = (TextView) v.findViewById(R.id.edit_completion_percentage_textField);
        charactersRecyclerView = (RecyclerView)v.findViewById(R.id.edit_characterRecyclerView);
        recyclerLayout = (LinearLayout)v.findViewById(R.id.recyclerviewLayout);
        gameImageProgressBar = (ProgressBar)v.findViewById(R.id.gameImageProgressBar);
        relevantGamesRecyclerView = (RecyclerView)v.findViewById(R.id.relevantGamesRecyclerView);
        gameDescriptionRecyclerView = (RecyclerView)v.findViewById(R.id.gameDescriptionRecyclerView);
        gameGenresRecyclerView = (RecyclerView)v.findViewById(R.id.gameGenresRecyclerView);

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);
        toolbar.setVisibility(View.VISIBLE);
        ((Main)getActivity()).setSupportActionBar(toolbar);

        mScrollView = (SynchronizedScrollView)v.findViewById(R.id.scroll);
        mScrollView.init(getActivity(), v);

        if(gamePosition == -1) {
            gamePosition = -2;
            ScreenSetupController.currentScreenEditGame(getActivity(), true);
            ApiHandler apiHandler = new ApiHandler(getActivity());
            apiHandler.getGameGiantBomb(searchResults.getId(), new Callback<GameResponse>(){
                @Override
                public void success(GameResponse gameResponse, Response response) {
                    if(searchResults.getImage() != null) {
                        imageDownloadManager.setListener(new ImageDownloader.Listener<Integer>() {
                            @Override
                            public void onThumbNailDownloaded(Integer position, Bitmap thumbnail) {
                            if (thumbnail != null) {
                                gameImageProgressBar.setVisibility(View.GONE);
                                int px = PictureUtils.dpTOPX(160, getActivity());
                                Bitmap bitmap = Bitmap.createBitmap(thumbnail);
                                Bitmap bmp = Bitmap.createBitmap(thumbnail);
                                bmp = PictureUtils.scaleDown(bmp, px, true);
                                bitmap = PictureUtils.scaleDown(bitmap, px, true);
                                gameImageView.setImageBitmap(bmp);
                                blurredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
                                blurredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                            }
                        });
                        gameImageProgressBar.setVisibility(View.VISIBLE);

                        if (searchResults.getImage().getSuper_url() != null) {
                            imageDownloadManager.queueThumbnail(0, searchResults.getImage().getSuper_url());
                        } else if (searchResults.getImage().getThumb_url() != null) {
                            imageDownloadManager.queueThumbnail(0, searchResults.getImage().getThumb_url());
                        }
                    }
                    else{
                        gameImageView.setImageDrawable(getResources().getDrawable(R.drawable.box_art));
                        blurredGameImage.setImageDrawable(getResources().getDrawable(R.drawable.box_art));
                        gameImageProgressBar.setVisibility(View.GONE);
                    }

                    if(gameResponse.getResults().getGameGenres() != null) {
                        ArrayList<String> genreList = StringArrayListBuilder.createArryList("Genres", ((ArrayList) gameResponse.getResults().getGameGenres()));
                        ExpandableRecyclerAdapter genreRecyclerAdapter = new ExpandableRecyclerAdapter(genreList, getActivity(),
                                gameGenresRecyclerView, false);
                        gameGenresRecyclerView.setAdapter(genreRecyclerAdapter);
                    }
                    else {
                        gameGenresRecyclerView.setVisibility(View.GONE);
                    }

                    if(gameResponse.getResults().getSimilar_games() != null) {
                        ArrayList<String> similarGamesList = StringArrayListBuilder.createArryList("Similar Games", ((ArrayList) gameResponse.getResults().similar_games));
                        ExpandableRecyclerAdapter similarGamesRecyclerAdapter = new ExpandableRecyclerAdapter(similarGamesList, getActivity(),
                                relevantGamesRecyclerView, false);
                        relevantGamesRecyclerView.setAdapter(similarGamesRecyclerAdapter);
                    }
                    else {
                        relevantGamesRecyclerView.setVisibility(View.GONE);
                    }

                    if(gameResponse.getResults().getGameCharacters() != null) {
                        GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter;

                        if(gameResponse.getResults().getGameCharacters().size() <= 10) {
                            gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter(gameResponse.getResults().getGameCharacters(), getActivity());
                        }
                        else {
                            gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter( new ArrayList<>(gameResponse.getResults().getGameCharacters().subList(0, 10)), getActivity());
                        }
                        charactersRecyclerView.setAdapter(gameCharactersRecyclerAdapter);
                    }
                    else {
                        recyclerLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });

            populateFromSearch(searchResults);
        }
        else if( gamePosition != - 2){
            RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
            populateFields(storedRealmGames.get(gamePosition));
            ScreenSetupController.currentScreenEditGame(getActivity(), false);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        charactersRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        relevantGamesRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameGenresRecyclerView.setLayoutManager(linearLayoutManager);

        addItemsOnSpinner();

        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gamePosition != -1 && gamePosition != -2) {
                    upDateGame();
                    ScreenSetupController.currentScreenGameList(getActivity());
                    SnackbarBuilder snackbarBuilder = new SnackbarBuilder(v, "Updated Game");
                    snackbarBuilder.show();
                } else {

                    saveGame();
                    saveGame();
                    saveGame();
                    saveGame();
                    saveGame();
                    saveGame();
                    saveGame();
                    saveGame();

                    ScreenSetupController.currentScreenGameList(getActivity());
                    SnackbarBuilder snackbarBuilder = new SnackbarBuilder(v, "Saved Game");
                    snackbarBuilder.show();
                    realm.close();
                }
            }});

            deleteGameButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                realm.beginTransaction();
                RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
                storedRealmGames.get(gamePosition).setDeleted(true);
                realm.commitTransaction();
                ScreenSetupController.currentScreenGameList(getActivity());
                SnackbarBuilder snackbarBuilder = new SnackbarBuilder(v, "Deleted Game");
                snackbarBuilder.show();
            }
        });

        return v;
    }

    public void saveGame() {

        Realm saveRealm = Realm.getInstance(getActivity().getApplicationContext());
        saveRealm.beginTransaction();

        RealmGame realmGame = saveRealm.createObject(RealmGame.class);
        realmGame.setName(gameName.getText().toString());
        realmGame.setPlatform(platformSpinner.getSelectedItem().toString());
        realmGame.setUserRating(userRatingBar.getRating());
        realmGame.setDeleted(false);

        if(!completionPercentage.getText().toString().matches("")) {
            realmGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        } else {
            realmGame.setCompletionPercentage(0);
        }

        if(gameDescriptionRecyclerView.getVisibility() == View.VISIBLE) {
            realmGame.setDescription(((ExpandableRecyclerAdapter) gameDescriptionRecyclerView.getAdapter()).getList().get(1));
        }

        if(gameImageView.getDrawable() != null) {
             byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
             realmGame.setPhoto(bytes);
        }
        else {
            realmGame.setPhotoURL(searchResults.image.getSuper_url());
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<RealmCharacter> gameCharacteres = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getRecyclerObjects();

            for (int i = 0; i < gameCharacteres.size(); i++) {
                RealmCharacter realmCharacter = saveRealm.createObject(RealmCharacter.class);
                realmCharacter.setName(gameCharacteres.get(i).getName());
                realmCharacter.setID(gameCharacteres.get(i).getID());

                if(gameCharacteres.get(i).getPhoto() != null) {
                    realmCharacter.setPhoto(gameCharacteres.get(i).getPhoto());
                    realmCharacter.setDescription(gameCharacteres.get(i).getDescription());
                    realmCharacter.setPhotosLoaded(true);
                }
                else {
                    realmCharacter.setPhotosLoaded(false);
                }

                realmGame.getCharacterses().add(realmCharacter);
            }
        }

        if(gameGenresRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> genreTypes = ((ExpandableRecyclerAdapter) gameGenresRecyclerView.getAdapter()).getList();
            for (int i = 1; i < genreTypes.size(); i++) {
                RealmGenre realmGenre = saveRealm.createObject(RealmGenre.class);
                realmGenre.setName(genreTypes.get(i));
                realmGame.getRealmGenre().add(realmGenre);
            }
        }

        if(relevantGamesRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> similarGameNames = ((ExpandableRecyclerAdapter) relevantGamesRecyclerView.getAdapter()).getList();
            for (int i = 1; i < similarGameNames.size(); i++) {
                RealmGame similarRealmGame = saveRealm.createObject(RealmGame.class);
                similarRealmGame.setName(similarGameNames.get(i));
                realmGame.getSimilarRealmGames().add(similarRealmGame);
            }
        }

        saveRealm.commitTransaction();
        saveRealm.close();
    }

    public void upDateGame() {
        Realm upDateRealm = Realm.getInstance(getActivity().getApplicationContext());
        upDateRealm.beginTransaction();
        RealmResults<RealmGame> storedRealmGames = upDateRealm.where(RealmGame.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
        RealmGame upDateRealmGame = storedRealmGames.get(gamePosition);

        upDateRealmGame.setPlatform(platformSpinner.getSelectedItem().toString());
        upDateRealmGame.setUserRating(userRatingBar.getRating());
        if(!completionPercentage.getText().toString().matches("")) {
            upDateRealmGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }
        else {
            upDateRealmGame.setCompletionPercentage(0);
        }

        if(gameImageView.getDrawable() != null) {
            byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
            upDateRealmGame.setPhoto(bytes);
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<RealmCharacter> editCharacters = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getRecyclerObjects();
            for (int i = 0; i < editCharacters.size(); i++) {
                if(!upDateRealmGame.getCharacterses().get(i).isPhotosLoaded() && editCharacters.get(i).isPhotosLoaded()) {
                    upDateRealmGame.getCharacterses().get(i).setPhoto(editCharacters.get(i).getPhoto());
                    upDateRealmGame.getCharacterses().get(i).setDescription(editCharacters.get(i).getDescription());
                    upDateRealmGame.getCharacterses().get(i).setPhotosLoaded(true);
                }
            }
        }
        upDateRealm.commitTransaction();
        upDateRealm.close();
    }

    public void populateFields(final RealmGame realmGame) {

        gameImageProgressBar.setVisibility(View.GONE);

        gameName.setText(realmGame.getName());
        userRatingBar.setRating(realmGame.getUserRating());
        completionPercentage.setText(Float.toString(realmGame.getCompletionPercentage()));

         if(!realmGame.getDescription().matches("")) {
            ArrayList descriptionList = new ArrayList<>();
            descriptionList.add("Description");
            descriptionList.add(realmGame.getDescription());

            ExpandableRecyclerAdapter gameDescriptionRecyclerAdapter = new ExpandableRecyclerAdapter(descriptionList, getActivity(),
                    gameDescriptionRecyclerView, true);
             gameDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
        }
        else {
             gameDescriptionRecyclerView.setVisibility(View.GONE);
         }

        if(realmGame.getCharacterses().size() > 0) {
            GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter(realmGame.getCharacterses(), getActivity());
            charactersRecyclerView.setAdapter(gameCharactersRecyclerAdapter);
            gameCharactersRecyclerAdapter.notifyDataSetChanged();
        }
        else {
            recyclerLayout.setVisibility(View.GONE);
        }

        if(realmGame.getRealmGenre().size() > 0) {
            RealmList<RealmGenre> savedRealmGenres = realmGame.getRealmGenre();
            ArrayList<String> genres = new ArrayList<>();
            genres.add("Genres");
            for(int i =0;i < savedRealmGenres.size(); i ++) {
                genres.add(savedRealmGenres.get(i).getName());
            }

            ExpandableRecyclerAdapter genreRecyclerAdapter = new ExpandableRecyclerAdapter(genres, getActivity(),
                    gameGenresRecyclerView, false);
            gameGenresRecyclerView .setAdapter(genreRecyclerAdapter);
        }

        if(realmGame.getSimilarRealmGames().size() > 0) {
            RealmList<RealmGame> similarRealmGames = realmGame.getSimilarRealmGames();
            ArrayList<String> games = new ArrayList<>();
            games.add("Similar Games");
            for(int i =0;i < similarRealmGames.size(); i ++) {
                games.add(similarRealmGames.get(i).getName());
            }
            ExpandableRecyclerAdapter relevantGameRecyclerAdapter = new ExpandableRecyclerAdapter(games, getActivity(),
                    relevantGamesRecyclerView, false);
            relevantGamesRecyclerView .setAdapter(relevantGameRecyclerAdapter);
            relevantGamesRecyclerView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));
        }
        else {
            relevantGamesRecyclerView.setVisibility(View.GONE);
        }

        Bitmap bmp = BitmapFactory.decodeByteArray(realmGame.getPhoto(), 0, realmGame.getPhoto().length);
        if(bmp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(realmGame.getPhoto(), 0, realmGame.getPhoto().length);
            blurredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap,getActivity().getApplicationContext()));
            blurredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);
            gameImageView.setImageBitmap(bmp);

        }
        else {
            imageDownloadManager.setListener(new ImageDownloader.Listener<Integer>() {
                @Override
                public void onThumbNailDownloaded(Integer position, Bitmap thumbnail) {
                    if (thumbnail != null) {
                        gameImageProgressBar.setVisibility(View.GONE);
                        int dp = 160;
                        int px = PictureUtils.dpTOPX(dp, getActivity());
                        Bitmap bitmap = Bitmap.createBitmap(thumbnail);
                        Bitmap bmp = Bitmap.createBitmap(thumbnail);
                        bmp = PictureUtils.scaleDown(bmp, px, true);
                        bitmap = PictureUtils.scaleDown(bitmap,px,true);
                        gameImageView.setImageBitmap(bmp);
                        blurredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
                        blurredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);

                    }
                }});

            imageDownloadManager.queueThumbnail(0, realmGame.getPhotoURL());
            gameImageProgressBar.setVisibility(View.VISIBLE);
        }

        mScrollView.setToolbarTitle(realmGame.getName());

    }

    public void populateFromSearch(GiantBombSearch giantBombSearch) {
        gameName.setText(giantBombSearch.getName());
        completionPercentage.setText(Float.toString(0));

        if(giantBombSearch.getDescription() != null) {
            ArrayList<String> descriptionList = new ArrayList<>();
            descriptionList.add("Description");
            descriptionList.add(HTMLUtil.stripHtml(giantBombSearch.getDescription()));

            ExpandableRecyclerAdapter gameDescriptionRecyclerAdapter = new ExpandableRecyclerAdapter(descriptionList, getActivity(),
                    gameDescriptionRecyclerView, true);
            gameDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
            mScrollView.setToolbarTitle(giantBombSearch.getName());
        }
        else {
            gameDescriptionRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(imageDownloadManager != null) {
            imageDownloadManager.imageDownloader.clearQueue();
            imageDownloadManager.imageDownloader.quit();
        }
    }

    public void addItemsOnSpinner() {

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.ps4_drawer_title));
        list.add(getString(R.string.ps3_drawer_title));
        list.add(getString(R.string.ps2_drawer_title));
        list.add(getString(R.string.ps1_drawer_title));
        list.add(getString(R.string.pc_drawer_title));
        list.add(getString(R.string.xboxone_drawer_title));
        list.add(getString(R.string.xbox360_drawer_title));
        list.add(getString(R.string.xbox_drawer_title));
        list.add(getString(R.string.wiiu_drawer_title));
        list.add(getString(R.string.wii_drawer_title));
        list.add(getString(R.string.gamecube_drawer_title));
        list.add(getString(R.string.nintendo64_drawer_title));
        list.add(getString(R.string.supernintendo_drawer_title));
        list.add(getString(R.string.nintendo_drawer_title));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(),
        android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(dataAdapter);
        int position = dataAdapter.getPosition(currentPlatform);
        platformSpinner.setSelection(position);
    }
}
