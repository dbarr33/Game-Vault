package com.apps.danielbarr.gamecollection.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Adapter.RelevantGameRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Genre;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GiantBombSearch;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.SimilarGames;
import com.apps.danielbarr.gamecollection.Model.RecyclerObject;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.GiantBombRestClient;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;

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
    private ImageView gameImageView;
    private ImageView bluredGameImage;
    private TextView gameName;
    private TextView completionPercentage;
    private TextView topViewGameName;
    private RatingBar userRatingBar;
    private Realm realm;
    private Spinner platformSpinner;
    private RecyclerView relevantGamesRecyclerView;
    private RecyclerView gameDescriptionRecyclearView;
    private RecyclerView gameGenresRecyclerView;

    private Game currentGame;
    private int gamePosition;
    private String currentPlatform;
    private GiantBombSearch searchResults;
    private Bitmap searchImage;
    private Dialog mDialog;


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
        searchImage = getArguments().getParcelable(EXTRA_SEARCH);


        setHasOptionsMenu(true);
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

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        ((Main)getActivity()).setSupportActionBar(toolbar);

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
                new DownloadAsyncTask(gameImageView).execute(searchResults.getImage().getSuper_url());
                GiantBombRestClient.get().getGameGiantBomb(searchResults.getId(), "2b5563f0a5655a6ef2e0a4d0556d2958cced098d", "json",
                        new Callback<GameResponse>() {
                            @Override
                            public void success(GameResponse gameResponse, retrofit.client.Response response) {

                                if(gameResponse.getResults().getGenres() != null) {
                                    ArrayList<Genre> genres = new ArrayList<Genre>();
                                    genres = gameResponse.getResults().getGenres();
                                    ArrayList<String> genreList = new ArrayList<String>();
                                    genreList.add("Genres");
                                    for(int i = 0; i < genres.size(); i++) {
                                        genreList.add(genres.get(i).getName());
                                    }
                                    RelevantGameRecyclerAdapter genreRecycylerAdapter = new RelevantGameRecyclerAdapter(genreList, getActivity(),
                                            gameGenresRecyclerView , genreList.size() * 120);
                                    gameGenresRecyclerView.setAdapter(genreRecycylerAdapter);
                                    gameGenresRecyclerView.setMinimumHeight(160);
                                }else {
                                    gameGenresRecyclerView.setVisibility(View.GONE);
                                }


                                if(gameResponse.getResults().getSimilar_games() != null) {
                                    ArrayList<SimilarGames> similarGameses = gameResponse.getResults().getSimilar_games();

                                    ArrayList<String> similarGameNames = new ArrayList<String>();
                                    similarGameNames.add("Similar Games");

                                    if(similarGameses.size() < 7) {
                                        for (int i = 0; i < similarGameses.size(); i++) {
                                            similarGameNames.add(similarGameses.get(i).getName());
                                        }
                                    }
                                    else {
                                        for (int i = 0; i < 7; i++) {
                                            similarGameNames.add(similarGameses.get(i).getName());
                                        }
                                    }
                                    final RelevantGameRecyclerAdapter relevantGameRecyclerAdapter = new RelevantGameRecyclerAdapter(similarGameNames, getActivity(),
                                            relevantGamesRecyclerView, 120 * similarGameNames.size());

                                    relevantGamesRecyclerView.setAdapter(relevantGameRecyclerAdapter);
                                    relevantGamesRecyclerView.setMinimumHeight(160);
                                }
                                else {
                                    relevantGamesRecyclerView.setVisibility(View.GONE);
                                }

                                ArrayList<com.apps.danielbarr.gamecollection.Model.GiantBomb.Character> characters = new ArrayList<com.apps.danielbarr.gamecollection.Model.GiantBomb.Character>();
                                characters = gameResponse.getResults().getCharacters();

                                if(characters != null) {

                                    GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter;

                                    if(characters.size() <= 10) {
                                        gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter(characters, getActivity());
                                    }
                                    else {
                                        gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter( new ArrayList<>(characters.subList(0, 10)), getActivity());

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
            } else {
                AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                dialog.show();
            }

            populateFromSearch(searchResults);
        }
        else if( gamePosition != - 2){
            realm = Realm.getInstance(getActivity().getApplicationContext());
            RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
            populateTextFields(storedGames.get(gamePosition));
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
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            upDateGame();
                            mDialog.dismiss();
                            ((Main)getActivity()).setShouldUpdateGameList(true);
                        }
                    });
                    mDialog = ProgressDialog.show(getActivity(), "Updating", "Wait while Updating...", true);
                    mDialog.show();
                    thread.start();
                    try {
                        thread.join();
                        ((Main)getActivity()).setUpToolbar();
                        getFragmentManager().popBackStack();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < 500;i++){
                                saveGame();
                            }
                            mDialog.dismiss();
                            ((Main)getActivity()).setShouldUpdateGameList(true);
                        }
                    });
                    mDialog = ProgressDialog.show(getActivity(), "Saving", "Wait while Saving...", true);
                    mDialog.show();
                    thread.start();
                    try {
                        thread.join();
                        ((Main)getActivity()).setUpToolbar();
                        getFragmentManager().popBackStack();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                realm = Realm.getInstance(getActivity().getApplicationContext());
                realm.beginTransaction();
                RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
                Game deleteGame = storedGames.get(gamePosition);
/*
                if(deleteGame.getCharacterses() != null) {
                   RealmList<GameCharacters> characters = deleteGame.getCharacterses();

                    for(int i = characters.size() - 1; i > -1; i --) {
                        characters.remove(i);
                    }
                    characters.clear();
                }
                deleteGame.setCharacterses(null);*/
                deleteGame.setDeleted(true);
                realm.commitTransaction();
                realm.close();
                ((Main)getActivity()).setShouldUpdateGameList(true);
                ((Main)getActivity()).setUpToolbar();
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }

    public void saveGame() {
        Realm saveGameRealm = Realm.getInstance(getActivity().getApplicationContext());
        saveGameRealm.beginTransaction();

        Game game = saveGameRealm.createObject(Game.class);
        game.setName(gameName.getText().toString());
        game.setPlatform(platformSpinner.getSelectedItem().toString());
        game.setUserRating(userRatingBar.getRating());
        game.setDeleted(false);

        if(!completionPercentage.getText().toString().matches("")) {
            game.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }
        game.setDescription(((RelevantGameRecyclerAdapter) gameDescriptionRecyclearView.getAdapter()).getGameList().get(1));

        if(gameImageView.getDrawable() != null) {
             byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
             game.setPhoto(bytes);
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<GameCharacters> gameCharacterses = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getGameCharacterses();
            for (int i = 0; i < gameCharacterses.size(); i++) {
                GameCharacters realmCharacter = saveGameRealm.createObject(GameCharacters.class);
                realmCharacter.setName(gameCharacterses.get(i).getName());
                realmCharacter.setID(gameCharacterses.get(i).getID());

                if(gameCharacterses.get(i).getPhoto() != null) {
                    realmCharacter.setPhoto(gameCharacterses.get(i).getPhoto());
                    realmCharacter.setDescription(gameCharacterses.get(i).getDescription());
                    realmCharacter.setEnemies(gameCharacterses.get(i).getEnemies());
                    realmCharacter.setPhotosLoaded(true);
                }
                else {
                    realmCharacter.setPhotosLoaded(false);
                }

                game.getCharacterses().add(realmCharacter);
            }
        }

        if(gameGenresRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> genreTypes = ((RelevantGameRecyclerAdapter) gameGenresRecyclerView.getAdapter()).getGameList();
            for (int i = 1; i < genreTypes.size(); i++) {
                com.apps.danielbarr.gamecollection.Model.Genre genre = saveGameRealm.createObject(com.apps.danielbarr.gamecollection.Model.Genre.class);
                genre.setName(genreTypes.get(i));
                game.getGenre().add(genre);
            }
        }

        if(relevantGamesRecyclerView.getVisibility() != View.GONE) {
            ArrayList<String> similarGameNames = ((RelevantGameRecyclerAdapter) relevantGamesRecyclerView.getAdapter()).getGameList();
            for (int i = 1; i < similarGameNames.size(); i++) {
                Game similarGame = saveGameRealm.createObject(Game.class);
                similarGame.setName(similarGameNames.get(i));
                game.getSimilarGames().add(similarGame);
            }
        }

        try {
            saveGameRealm.commitTransaction();
        }
        catch (io.realm.internal.OutOfMemoryError error) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"You are out of memory",Toast.LENGTH_SHORT);
                }
            });
        }
        saveGameRealm.close();
    }

    public void upDateGame() {

        Realm updateRealm = Realm.getInstance(getActivity().getApplicationContext());

        updateRealm.beginTransaction();
        RealmResults<Game> storedGames = updateRealm.where(Game.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
        Game upDateGame = storedGames.get(gamePosition);

        upDateGame.setPlatform(platformSpinner.getSelectedItem().toString());
        upDateGame.setUserRating(userRatingBar.getRating());
        if(!completionPercentage.getText().toString().matches("")) {
            upDateGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }

        if(gameImageView.getDrawable() != null) {
            byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
            upDateGame.setPhoto(bytes);
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<RecyclerObject> editCharacters = ((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getRecyclerObjects();
            for (int i = 0; i < editCharacters.size(); i++) {
                if(!upDateGame.getCharacterses().get(i).isPhotosLoaded() && editCharacters.get(i).isPhotosLoaded()) {
                    upDateGame.getCharacterses().get(i).setPhoto(editCharacters.get(i).getPhoto());
                    upDateGame.getCharacterses().get(i).setDescription(editCharacters.get(i).getDescription());

                    if(editCharacters.get(i).getEnemies() != null) {
                        upDateGame.getCharacterses().get(i).setEnemies(editCharacters.get(i).getEnemies());
                    }
                    upDateGame.getCharacterses().get(i).setPhotosLoaded(true);
                }
            }
        }

        try {
            updateRealm.commitTransaction();
        }
        catch (io.realm.internal.OutOfMemoryError error) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"You are out of memory",Toast.LENGTH_SHORT);
                }
            });
        }
        updateRealm.close();
    }

    public void populateTextFields(Game game) {
        gameImageProgressBar.setVisibility(View.GONE);
        currentGame = game;

        gameName.setText(currentGame.getName());
        topViewGameName.setText(currentGame.getName());
        userRatingBar.setRating(currentGame.getUserRating());
        completionPercentage.setText(Float.toString(currentGame.getCompletionPercentage()));


                ArrayList descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        descriptionList.add(currentGame.getDescription());
        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setText(currentGame.getDescription());

        RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                gameDescriptionRecyclearView, (98 * PictureUtils.dpTOPX(19, getActivity()) + PictureUtils.dpTOPX(40, getActivity())));
        gameDescriptionRecyclearView .setAdapter(gameDescriptionRecyclerAdapter);
        gameDescriptionRecyclearView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));

        if(currentGame.getCharacterses().size() > 0) {
            GameCharactersRecyclerAdapter gameCharactersRecyclerAdapter = new GameCharactersRecyclerAdapter(currentGame.getCharacterses(), getActivity());
            charactersRecyclerView.setAdapter(gameCharactersRecyclerAdapter);
            gameCharactersRecyclerAdapter.notifyDataSetChanged();
        }
        else {
            recyclerLayout.setVisibility(View.GONE);
        }

        if(currentGame.getGenre().size() > 0) {
            RealmList<com.apps.danielbarr.gamecollection.Model.Genre> savedGenres = currentGame.getGenre();
            ArrayList<String> genres = new ArrayList<>();
            genres.add("Genres");
            for(int i =0;i < savedGenres.size(); i ++) {
                genres.add(savedGenres.get(i).getName());
            }

            RelevantGameRecyclerAdapter genreRecyclerAdapter = new RelevantGameRecyclerAdapter(genres, getActivity(),
                    gameGenresRecyclerView, PictureUtils.dpTOPX(32, getActivity()) * genres.size() + PictureUtils.dpTOPX(40, getActivity()));
            gameGenresRecyclerView .setAdapter(genreRecyclerAdapter);
            gameGenresRecyclerView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));
        }

        if(currentGame.getSimilarGames().size() > 0) {
            RealmList<Game> similarGames = currentGame.getSimilarGames();
            ArrayList<String> games = new ArrayList<>();
            games.add("Similar Games");
            for(int i =0;i < similarGames.size(); i ++) {
                games.add(similarGames.get(i).getName());
            }
            RelevantGameRecyclerAdapter relevantGameRecyclerAdapter = new RelevantGameRecyclerAdapter(games, getActivity(),
                    relevantGamesRecyclerView, PictureUtils.dpTOPX(32, getActivity()) * games.size() + PictureUtils.dpTOPX(40, getActivity()));
            relevantGamesRecyclerView .setAdapter(relevantGameRecyclerAdapter);
            relevantGamesRecyclerView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));
        }
        else {
            relevantGamesRecyclerView.setVisibility(View.GONE);
        }

        Bitmap bmp = BitmapFactory.decodeByteArray(currentGame.getPhoto(), 0, currentGame.getPhoto().length);
        if(bmp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentGame.getPhoto(), 0, currentGame.getPhoto().length);
            bluredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap,getActivity().getApplicationContext()));
            bluredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);
            gameImageView.setImageBitmap(bmp);

        }
        else {
            new DownloadAsyncTask(gameImageView).execute(currentGame.getPhotoURL());
            gameImageProgressBar.setVisibility(View.VISIBLE);
        }

        mScrollView.setToolbarTitle(currentGame.getName());

    }

    public void populateFromSearch(GiantBombSearch giantBombSearch) {
        gameName.setText(giantBombSearch.getName());
        topViewGameName.setText(giantBombSearch.getName());

        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        descriptionList.add(stripHtml(giantBombSearch.getDescription()));
        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setText(stripHtml(giantBombSearch.getDescription()));
        textView.measure(0, 0);
        RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                gameDescriptionRecyclearView, textView.getLineCount() * PictureUtils.dpTOPX(45, getActivity()) + PictureUtils.dpTOPX(40, getActivity()));
        gameDescriptionRecyclearView .setAdapter(gameDescriptionRecyclerAdapter);
        gameDescriptionRecyclearView.setMinimumHeight(PictureUtils.dpTOPX(40, getActivity()));
        mScrollView.setToolbarTitle(giantBombSearch.getName());

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.global, menu);
        MenuItem deleteButton = menu.findItem(R.id.customDeleteButton);
        MenuItem addGame = menu.findItem(R.id.addGame);
        addGame.setVisible(false);


       // if (gamePosition == -1) {
            deleteButton.setVisible(false);
       // }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.customDeleteButton:
                realm.beginTransaction();
                RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", currentPlatform).equalTo("isDeleted", false).findAll();
                Log.e("Character name: ", storedGames.get(gamePosition).getCharacterses().get(0).getName());
                storedGames.get(gamePosition).setDeleted(true);
                realm.commitTransaction();
                realm.close();
                ((Main)getActivity()).getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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



    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference weakReference;

        public DownloadAsyncTask(ImageView imageView)
        {
            weakReference = new WeakReference(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String URL = params[0];
            Bitmap bitmap = null;
            try {
                java.net.URL imageURL = new URL(URL);

                BufferedInputStream bis = new BufferedInputStream(imageURL.openStream(), 10240);
                bitmap = BitmapFactory.decodeStream(bis);
                bis.close();

            } catch (MalformedURLException e) {
                Log.e("error", "Downloading Image Failed");

            }
            catch (IOException e)
            {
                Log.e("error", "Downloading Image Failed");
            }

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                gameImageProgressBar.setVisibility(View.GONE);
                int dp = 160;
                int px = PictureUtils.dpTOPX(dp, getActivity());
                Bitmap bitmap = Bitmap.createBitmap(result);
                Bitmap bmp = Bitmap.createBitmap(result);
                bmp = PictureUtils.scaleDown(bmp, px, true);
                bitmap = PictureUtils.scaleDown(bitmap,px,true);
                gameImageView.setImageBitmap(bmp);
                bluredGameImage.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
                bluredGameImage.setScaleType(ImageView.ScaleType.FIT_XY);

            }
        }
    }
}
