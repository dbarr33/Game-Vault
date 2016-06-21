package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Adapter.GameCharactersRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.Model.RealmGame;
import com.apps.danielbarr.gamecollection.Model.RealmGenre;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.HTMLUtil;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.SnackbarBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;
import com.apps.danielbarr.gamecollection.presenter.EditGamePresenter;
import com.apps.danielbarr.gamecollection.presenter.EditGameView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by danielbarr on 1/19/15.
 */
public class EditGameFragment extends Fragment implements EditGameView{

    public static final String EXTRA_GAME = "com.apps.danielbarr.gamecollection.game";
    public static final String EXTRA_PLATFORM = "com.apps.danielbarr.gamecollection.platform";
    public static final String EXTRA_Giant_Bomb_Response = "com.apps.danielbarr.gamecollection.giantBombResponse";

    private ImageButton deleteGameButton;
    private ImageButton saveGameButton;
    private ImageView gameImageView;
    private ImageView blurredGameImage;
    private TextView gameName;
    private TextView completionPercentage;
    private RatingBar userRatingBar;
    private Spinner platformSpinner;
    private RecyclerView relevantGamesRecyclerView;
    private RecyclerView gameDescriptionRecyclerView;
    private RecyclerView gameGenresRecyclerView;
    private RecyclerView charactersRecyclerView;
    private RecyclerView publisherRecyclerview;
    private ProgressBar gameImageProgressBar;
    private LinearLayout characterLayout;
    private String currentPlatform;
    private GiantBombSearch searchResults;


    public SynchronizedScrollView mScrollView;
    public int gamePosition;
    public EditGamePresenter editGamePresenter;

    public static EditGameFragment newInstance(String platform, GiantBombSearch giantBombSearch) {
        Bundle args = new Bundle();
        args.putString(EXTRA_PLATFORM, platform);
        args.putSerializable(EXTRA_Giant_Bomb_Response, giantBombSearch);
        EditGameFragment fragment = new EditGameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditGameFragment newInstance(String platform, int gamePosition) {
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
        searchResults = (GiantBombSearch)getArguments().getSerializable(EXTRA_Giant_Bomb_Response);
        editGamePresenter = new EditGamePresenter(this);
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
        characterLayout = (LinearLayout)v.findViewById(R.id.chacacterLayout);
        gameImageProgressBar = (ProgressBar)v.findViewById(R.id.gameImageProgressBar);
        relevantGamesRecyclerView = (RecyclerView)v.findViewById(R.id.relevantGamesRecyclerView);
        gameDescriptionRecyclerView = (RecyclerView)v.findViewById(R.id.gameDescriptionRecyclerView);
        publisherRecyclerview = (RecyclerView)v.findViewById(R.id.publisher);
        gameGenresRecyclerView = (RecyclerView)v.findViewById(R.id.gameGenresRecyclerView);
        mScrollView = (SynchronizedScrollView)v.findViewById(R.id.scroll);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        configurePlatformSpinner();
        editGamePresenter.setupToolbar();
        mScrollView.init(getActivity(), view);

        if(gamePosition == -1) {
            gamePosition = -2;
            editGamePresenter.configureScreen(true);
            if(searchResults.getImage() != null) {
                if(searchResults.getImage().getSuper_url() != null) {
                    editGamePresenter.fetchDataFromAPI(searchResults.getId(), searchResults.getImage().getSuper_url());
                }
                else {
                    editGamePresenter.fetchDataFromAPI(searchResults.getId(), searchResults.getImage().getIcon_url());
                }
            }
            else {
                editGamePresenter.fetchDataFromAPI(searchResults.getId(), null);
            }
            populateFromSearch(searchResults);
        }
        else if(gamePosition != - 2){
            editGamePresenter.configureScreen(false);
            editGamePresenter.loadDataFromRealm(currentPlatform, gamePosition);

            deleteGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editGamePresenter.removeGame(currentPlatform, gamePosition);
                }
            });
        }
        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gamePosition < 0) {
                    editGamePresenter.saveGame(createGame());
                } else {
                    editGamePresenter.updateGame(createGame(), currentPlatform, gamePosition);
                }
            }
        });
    }

    @Override
    public void populateFields(final RealmGame realmGame) {
        gameName.setText(realmGame.getName());
        userRatingBar.setRating(realmGame.getUserRating());
        completionPercentage.setText(Float.toString(realmGame.getCompletionPercentage()));

        if(realmGame.getDescription() != null){
            configureDescriptionRecyclerView(realmGame.getDescription());
        }
        if(realmGame.getCharacters().size() > 0) {
            configureCharacterRecyclerView(new GameCharactersRecyclerAdapter(realmGame.getCharacters()));
        }
        if(realmGame.getRealmGenre().size() > 0) {
            editGamePresenter.createGenreData(realmGame.getRealmGenre());
        }
        if(realmGame.getSimilarRealmGames().size() > 0) {
            editGamePresenter.createSimilarGameData(realmGame.getSimilarRealmGames());
        }
        if(realmGame.getPublishers().size() > 0) {
            editGamePresenter.createPublisherGameData(realmGame.getPublishers());
        }

        if(realmGame.getPhoto() != null) {
            setupSavedImages(realmGame.getPhoto());
        }
        else if(!realmGame.isHasImage()){
            //TODO fix default image displayed
            Bitmap bitmap = BitmapFactory.decodeResource(GameApplication.getActivity().getResources(), R.drawable.box_art);
            //setupSavedImages(bitmap);
        }
        else {
            setupGameImages(realmGame.getPhotoURL());
        }

        mScrollView.setToolbarTitle(realmGame.getName());
    }

    @Override
    public void showConfirmationMessage(String message) {
        new SnackbarBuilder(getView(), message).show();
    }

    @Override
    public void hideProgressBar() {
        gameImageProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        gameImageProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupGameImages(String imageURL) {
        Glide.with(this)
                .load(imageURL)
                .asBitmap()
                .into(gameImageView);
        Glide.with(this)
                .load(imageURL)
                .asBitmap()
                .transform(new BlurTransformation(getActivity().getApplicationContext()))
                .into(blurredGameImage);
    }

    public void setupSavedImages(byte[] bitmap) {
        Glide.with(this)
                .load(bitmap)
                .asBitmap()
                .into(gameImageView);
        Glide.with(this)
                .load(bitmap)
                .asBitmap()
                .transform(new BlurTransformation(getActivity().getApplicationContext()))
                .into(blurredGameImage);
    }

    @Override
    public void configureGeneRecyclerView(ArrayList<String> strings) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameGenresRecyclerView.setLayoutManager(linearLayoutManager);
        ExpandableRecyclerAdapter genreRecyclerAdapter = new ExpandableRecyclerAdapter(strings,
                gameGenresRecyclerView, false);
        gameGenresRecyclerView.setAdapter(genreRecyclerAdapter);
        gameGenresRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void configureSimilarGamesRecyclerView(ArrayList<String> strings) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        relevantGamesRecyclerView.setLayoutManager(linearLayoutManager);
        ExpandableRecyclerAdapter similarGamesAdapter = new ExpandableRecyclerAdapter(strings,
                relevantGamesRecyclerView, false);
        relevantGamesRecyclerView.setAdapter(similarGamesAdapter);
        relevantGamesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void configurePublisherRecyclerView(ArrayList<String> strings) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        publisherRecyclerview.setLayoutManager(linearLayoutManager);
        ExpandableRecyclerAdapter publisherAdapter = new ExpandableRecyclerAdapter(strings,
                publisherRecyclerview, false);
        publisherRecyclerview.setAdapter(publisherAdapter);
        publisherRecyclerview.setVisibility(View.VISIBLE);
    }

    @Override
    public void configureCharacterRecyclerView(GameCharactersRecyclerAdapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        charactersRecyclerView.setLayoutManager(linearLayoutManager);
        charactersRecyclerView.setAdapter(adapter);
        characterLayout.setVisibility(View.VISIBLE);
    }

    public void configureDescriptionRecyclerView(String description) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        descriptionList.add(HTMLUtil.stripHtml(description));

        ExpandableRecyclerAdapter gameDescriptionRecyclerAdapter = new ExpandableRecyclerAdapter(descriptionList,
                gameDescriptionRecyclerView, true);
        gameDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
        gameDescriptionRecyclerView.setVisibility(View.VISIBLE);
    }

    private RealmGame createGame() {

        RealmGame realmGame = new RealmGame();
        Calendar c = Calendar.getInstance();
        long date = c.get(Calendar.YEAR) * 10000000000L + c.get(Calendar.MONTH) * 100000000 + c.get(Calendar.DAY_OF_MONTH) * 1000000 + c.get(Calendar.HOUR_OF_DAY) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
        realmGame.setDate(date);
        realmGame.setName(gameName.getText().toString());
        realmGame.setPlatform(platformSpinner.getSelectedItem().toString());
        realmGame.setUserRating(userRatingBar.getRating());

        if(!completionPercentage.getText().toString().matches("") && !completionPercentage.getText().toString().matches(".")) {
            realmGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        } else {
            realmGame.setCompletionPercentage(0);
        }

        if(gameDescriptionRecyclerView.getVisibility() == View.VISIBLE) {
            realmGame.setDescription(((ExpandableRecyclerAdapter) gameDescriptionRecyclerView.getAdapter()).getList().get(1));
        }
        if(characterLayout.getVisibility() == View.VISIBLE) {
            realmGame.setCharacters(((GameCharactersRecyclerAdapter) charactersRecyclerView.getAdapter()).getRecyclerObjects());
        }

        if(gameGenresRecyclerView.getVisibility() == View.VISIBLE) {
            RealmList<RealmGenre> realmGenres = new RealmList<>();
            ArrayList<String> genreTypes = ((ExpandableRecyclerAdapter)gameGenresRecyclerView.getAdapter()).getList();
            for (int i = 1; i < genreTypes.size(); i++) {
                RealmGenre realmGenre = new RealmGenre();
                realmGenre.setName(genreTypes.get(i));
                realmGenres.add(realmGenre);
            }
            realmGame.setRealmGenre(realmGenres);
        }
        if(relevantGamesRecyclerView.getVisibility() == View.VISIBLE) {
            RealmList<RealmGame> realmGames = new RealmList<>();
            ArrayList<String> similarGameNames = ((ExpandableRecyclerAdapter)relevantGamesRecyclerView.getAdapter()).getList();
            for (int i = 1; i < similarGameNames.size(); i++) {
                RealmGame similarRealmGame = new RealmGame();
                similarRealmGame.setName(similarGameNames.get(i));
                realmGames.add(similarRealmGame);
            }
            realmGame.setSimilarRealmGames(realmGames);
        }

        if(publisherRecyclerview.getVisibility() == View.VISIBLE) {
            RealmList<RealmPublisher> realmPublishers = new RealmList<>();
            ArrayList<String> publisherNames = ((ExpandableRecyclerAdapter)publisherRecyclerview.getAdapter()).getList();
            for (int i = 1; i < publisherNames.size(); i++) {
                RealmPublisher publisher = new RealmPublisher();
                publisher.setName(publisherNames.get(i));
                realmPublishers.add(publisher);
            }
            realmGame.setPublishers(realmPublishers);
        }

        if(blurredGameImage.getDrawable() != null) {
            byte[] bytes;

            if(gameImageView.getDrawable() instanceof GlideBitmapDrawable) {
                bytes = PictureUtils.convertBitmapToByteArray(((GlideBitmapDrawable) gameImageView.getDrawable()).getBitmap());
            }
            else {
                bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
            }
            realmGame.setPhoto(bytes);
            realmGame.setHasImage(true);
        }
        else if(gameImageView.getDrawable() != null){
            realmGame.setHasImage(false);
        }
        else {
            realmGame.setPhotoURL(searchResults.image.getSuper_url());
            realmGame.setHasImage(true);
        }

        return realmGame;
    }

    private void populateFromSearch(GiantBombSearch giantBombSearch) {
        gameName.setText(giantBombSearch.getName());
        completionPercentage.setText(Float.toString(0));

        if(giantBombSearch.getDescription() != null) {
            configureDescriptionRecyclerView(giantBombSearch.getDescription());
        }

        mScrollView.setToolbarTitle(giantBombSearch.getName());
    }

    private void configurePlatformSpinner() {

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
        list.add(getString(R.string.other_title));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(dataAdapter);
        int position = dataAdapter.getPosition(currentPlatform);
        platformSpinner.setSelection(position);
    }
}
