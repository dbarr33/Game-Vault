package com.apps.danielbarr.gamecollection.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Adapter.RelevantGameRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.Model.RealmGenre;
import com.apps.danielbarr.gamecollection.Model.RecyclerObject;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.GiantBombRestClient;
import com.apps.danielbarr.gamecollection.Uitilites.ImageDownloader;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
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

    private Button saveGameButton;
    private Button backToTopButton;
    private ImageButton deleteGameButton;
    private ImageButton saveGameButton2;
    private ImageView gameImageView;
    private ImageView bluredGameImage;
    private TextView gameName;
    private TextView completionPercentage;
    private TextView topViewGameName;
    private RatingBar userRatingBar;
    public Realm realm;
    private Spinner platformSpinner;
    private RecyclerView relevantGamesRecyclerView;
    private RecyclerView gameDescriptionRecyclearView;
    private RecyclerView gameGenresRecyclerView;

    public int gamePosition;
    private String currentPlatform;
    private GiantBombSearch searchResults;

    private RecyclerView charactersRecyclerView;
    private LinearLayout recyclerLayout;
    private ProgressBar gameImageProgressBar;
    public SynchronizedScrollView mScrollView;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_game, parent, false);
        saveGameButton = (Button) v.findViewById(R.id.saveGame);
        deleteGameButton = (ImageButton)getActivity().findViewById(R.id.deleteGameButton);
        saveGameButton2 = (ImageButton)getActivity().findViewById(R.id.saveGameButton);
        gameImageView = (ImageView) v.findViewById(R.id.edit_game_photo);
        bluredGameImage = (ImageView)v.findViewById(R.id.blurredGameImage);
        gameName = (TextView) v.findViewById(R.id.edit_name_textField);
        platformSpinner = (Spinner) v.findViewById(R.id.edit_platform_spinner);
        userRatingBar = (RatingBar) v.findViewById(R.id.userRatingStars);
        completionPercentage = (TextView) v.findViewById(R.id.edit_completion_percentage_textField);
        charactersRecyclerView = (RecyclerView)v.findViewById(R.id.edit_characterRecyclerView);
        recyclerLayout = (LinearLayout)v.findViewById(R.id.recyclerviewLayout);
        gameImageProgressBar = (ProgressBar)v.findViewById(R.id.gameImageProgressBar);
        topViewGameName = (TextView)v.findViewById(R.id.topViewGameName);
        relevantGamesRecyclerView = (RecyclerView)v.findViewById(R.id.relevantGamesRecyclearView);
        gameDescriptionRecyclearView = (RecyclerView)v.findViewById(R.id.gameDescriptionRecyclearView);
        gameGenresRecyclerView = (RecyclerView)v.findViewById(R.id.gameGenresRecyclearView);
        backToTopButton = (Button)v.findViewById(R.id.backToTheTopButton);

        DrawerLayout mDrawerLayout = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        ((Main)getActivity()).setSupportActionBar(toolbar);
        getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.GONE);

        mScrollView = (SynchronizedScrollView)v.findViewById(R.id.scroll);
        mScrollView.setToolbar(toolbar);

        mScrollView.setAnchorView(v.findViewById(R.id.topView));
        mScrollView.setSynchronizedView(v.findViewById(R.id.sync));
        mScrollView.setToTheTopButton(backToTopButton);


        if(gamePosition == -1) {
            gamePosition = -2;
            deleteGameButton.setVisibility(View.GONE);
            if (InternetUtils.isNetworkAvailable(getActivity())) {
                final Dialog mDialog = ProgressDialog.show(getActivity(), "Loading", "Wait while loading...");

                GiantBombRestClient.get().getGameGiantBomb(searchResults.getId(), GiantBombRestClient.key , GiantBombRestClient.json,
                        new Callback<GameResponse>() {
                    @Override
                    public void success(GameResponse gameResponse, Response response) {

                        if(searchResults.getImage() != null) {
                                ImageDownloader<Integer> thread;

                                thread = new ImageDownloader<>(new Handler());
                                thread.setListener(new ImageDownloader.Listener<Integer>() {
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
                                            bluredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
                                            bluredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);

                                        }
                                    }});
                                thread.start();
                                thread.getLooper();
                            gameImageProgressBar.setVisibility(View.VISIBLE);

                            try {
                                thread.queueThumbnail(0, searchResults.getImage().getSuper_url());
                            }
                            catch (NullPointerException error) {
                                thread.queueThumbnail(0, searchResults.getImage().getThumb_url());
                            }
                        }else{
                            gameImageView.setImageDrawable(getResources().getDrawable(R.drawable.box_art));
                            bluredGameImage.setImageDrawable(getResources().getDrawable(R.drawable.box_art));
                            gameImageProgressBar.setVisibility(View.GONE);
                        }

                        if(gameResponse.getResults().getGameGenres() != null) {
                            ArrayList<String> genreList = StringArrayListBuilder.createArryList("Genres", ((ArrayList) gameResponse.getResults().getGameGenres()));
                            RelevantGameRecyclerAdapter genreRecycylerAdapter = new RelevantGameRecyclerAdapter(genreList, getActivity(),
                                    gameGenresRecyclerView);
                            gameGenresRecyclerView.setAdapter(genreRecycylerAdapter);
                        }
                        else {
                            gameGenresRecyclerView.setVisibility(View.GONE);
                        }

                        if(gameResponse.getResults().getSimilar_games() != null) {
                            ArrayList<String> similarGamesList = StringArrayListBuilder.createArryList("Similar Games", ((ArrayList) gameResponse.getResults().similar_games));
                            RelevantGameRecyclerAdapter similareGamesRecycylerAdapter = new RelevantGameRecyclerAdapter(similarGamesList, getActivity(),
                                    relevantGamesRecyclerView);
                            relevantGamesRecyclerView.setAdapter(similareGamesRecycylerAdapter);
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

                        mDialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mDialog.dismiss();

                        if (!InternetUtils.isNetworkAvailable(getActivity())) {
                            AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                            dialog.show();
                        }
                        Log.e("Giant", error.getMessage());
                    }

                });
            }
            else {
                AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                dialog.show();
            }

            populateFromSearch(searchResults);
        }
        else if( gamePosition != - 2){
            RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
            populateFields(storedRealmGames.get(gamePosition));
            saveGameButton.setText(getString(R.string.update_game));
            deleteGameButton.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        charactersRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        relevantGamesRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameDescriptionRecyclearView.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameGenresRecyclerView.setLayoutManager(linearLayoutManager);

        addItemsOnSpinner();

        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gamePosition != -1 && gamePosition != -2) {

                    upDateGame();
                    ((Main)getActivity()).restoreMainScreen(true);
                    Snackbar.make(v, "Updated Game", Snackbar.LENGTH_SHORT)
                            .setAction("TESTING", null)
                            .show();
                } else {
                    for(int i = 0; i < 1;i++){
                    saveGame();
                    ((Main)getActivity()).restoreMainScreen(true);
                        Snackbar.make(v, "Saved Game", Snackbar.LENGTH_SHORT)
                                .setActionTextColor(getResources().getColor(android.R.color.white))
                            .show();
                }
                realm.close();
            }
        }});

        saveGameButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gamePosition != -1 && gamePosition != -2) {

                    upDateGame();
                    ((Main)getActivity()).restoreMainScreen(true);
                    Snackbar.make(v, "Updated Game", Snackbar.LENGTH_SHORT)
                            .setAction("TESTING", null)
                            .show();
                } else {
                    for (int i = 0; i < 1; i++) {
                        saveGame();
                        ((Main) getActivity()).restoreMainScreen(true);
                        Snackbar.make(v, "Saved Game", Snackbar.LENGTH_SHORT)
                                .setActionTextColor(getResources().getColor(android.R.color.white))
                                .show();
                    }
                    realm.close();
                }
            }
        });

        backToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollView.smoothScrollTo(0,0);
                backToTopButton.setVisibility(View.GONE);
            }
        });

        deleteGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                RealmResults<RealmGame> storedRealmGames = realm.where(RealmGame.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
                storedRealmGames.get(gamePosition).setDeleted(true);
                realm.commitTransaction();
                ((Main)getActivity()).restoreMainScreen(true);
                Snackbar.make(v, "Delete Game", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(getResources().getColor(android.R.color.white))
                        .show();
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

        if(gameDescriptionRecyclearView.getVisibility() == View.VISIBLE) {
            realmGame.setDescription(((RelevantGameRecyclerAdapter) gameDescriptionRecyclearView.getAdapter()).getGameList().get(1));
        }

        if(gameImageView.getDrawable() != null) {
             byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
             realmGame.setPhoto(bytes);
        }
        else {
            realmGame.setPhotoURL(searchResults.image.getSuper_url());
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<RealmCharacter> gameCharacterses = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getGameCharacterses();
            for (int i = 0; i < gameCharacterses.size(); i++) {
                RealmCharacter realmCharacter = saveRealm.createObject(RealmCharacter.class);
                realmCharacter.setName(gameCharacterses.get(i).getName());
                realmCharacter.setID(gameCharacterses.get(i).getID());

                if(gameCharacterses.get(i).getPhoto() != null) {
                    realmCharacter.setPhoto(gameCharacterses.get(i).getPhoto());
                    realmCharacter.setDescription(gameCharacterses.get(i).getDescription());
                    realmCharacter.setPhotosLoaded(true);
                }
                else {
                    realmCharacter.setPhotosLoaded(false);
                }

                realmGame.getCharacterses().add(realmCharacter);
            }
        }

        if(gameGenresRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> genreTypes = ((RelevantGameRecyclerAdapter) gameGenresRecyclerView.getAdapter()).getGameList();
            for (int i = 1; i < genreTypes.size(); i++) {
                RealmGenre realmGenre = saveRealm.createObject(RealmGenre.class);
                realmGenre.setName(genreTypes.get(i));
                realmGame.getRealmGenre().add(realmGenre);
            }
        }

        if(relevantGamesRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> similarGameNames = ((RelevantGameRecyclerAdapter) relevantGamesRecyclerView.getAdapter()).getGameList();
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
            ArrayList<RecyclerObject> editCharacters = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getRecyclerObjects();
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

    public void populateFields(RealmGame realmGame) {
        gameImageProgressBar.setVisibility(View.GONE);

        gameName.setText(realmGame.getName());
        topViewGameName.setText(realmGame.getName());
        userRatingBar.setRating(realmGame.getUserRating());
        completionPercentage.setText(Float.toString(realmGame.getCompletionPercentage()));

         if(!realmGame.getDescription().matches("")) {
            ArrayList descriptionList = new ArrayList<>();
            descriptionList.add("Description");
            descriptionList.add(realmGame.getDescription());

            RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                    gameDescriptionRecyclearView);
            gameDescriptionRecyclearView.setAdapter(gameDescriptionRecyclerAdapter);
        }
        else {
             gameDescriptionRecyclearView.setVisibility(View.GONE);
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

            RelevantGameRecyclerAdapter genreRecyclerAdapter = new RelevantGameRecyclerAdapter(genres, getActivity(),
                    gameGenresRecyclerView);
            gameGenresRecyclerView .setAdapter(genreRecyclerAdapter);
        }

        if(realmGame.getSimilarRealmGames().size() > 0) {
            RealmList<RealmGame> similarRealmGames = realmGame.getSimilarRealmGames();
            ArrayList<String> games = new ArrayList<>();
            games.add("Similar Games");
            for(int i =0;i < similarRealmGames.size(); i ++) {
                games.add(similarRealmGames.get(i).getName());
            }
            RelevantGameRecyclerAdapter relevantGameRecyclerAdapter = new RelevantGameRecyclerAdapter(games, getActivity(),
                    relevantGamesRecyclerView);
            relevantGamesRecyclerView .setAdapter(relevantGameRecyclerAdapter);
            relevantGamesRecyclerView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));
        }
        else {
            relevantGamesRecyclerView.setVisibility(View.GONE);
        }

        Bitmap bmp = BitmapFactory.decodeByteArray(realmGame.getPhoto(), 0, realmGame.getPhoto().length);
        if(bmp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(realmGame.getPhoto(), 0, realmGame.getPhoto().length);
            bluredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap,getActivity().getApplicationContext()));
            bluredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);
            gameImageView.setImageBitmap(bmp);

        }
        else {
            ImageDownloader<Integer> thread;

            thread = new ImageDownloader<>(new Handler());
            thread.setListener(new ImageDownloader.Listener<Integer>() {
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
                        bluredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
                        bluredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);

                    }
                }});
            thread.start();
            thread.getLooper();
            thread.queueThumbnail(0, realmGame.getPhotoURL());
            gameImageProgressBar.setVisibility(View.VISIBLE);
        }

        mScrollView.setToolbarTitle(realmGame.getName());

    }

    public void populateFromSearch(GiantBombSearch giantBombSearch) {
        gameName.setText(giantBombSearch.getName());
        topViewGameName.setText(giantBombSearch.getName());
        completionPercentage.setText(Float.toString(0));

        if(giantBombSearch.getDescription() != null) {
            ArrayList<String> descriptionList = new ArrayList<>();
            descriptionList.add("Description");
            descriptionList.add(stripHtml(giantBombSearch.getDescription()));

            RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                    gameDescriptionRecyclearView);
            gameDescriptionRecyclearView.setAdapter(gameDescriptionRecyclerAdapter);
            mScrollView.setToolbarTitle(giantBombSearch.getName());
        }
        else {
            gameDescriptionRecyclearView.setVisibility(View.GONE);
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

    public String stripHtml(String html) {
        if(html != null) {
            String temp = Html.fromHtml(html).toString();
            if(temp.length() > 4000) {
                return temp.substring(0, 4000);
            }
            else {
                return temp;
            }
        }
        else {
            return "";
        }
    }
}
