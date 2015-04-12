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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.CustomRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GameResponse;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Genre;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GiantBombSearch;
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
    private ImageView gameImageView;
    private TextView gameName;
    private TextView genre;
    private TextView description;
    private TextView completionPercentage;
    private TextView topViewGameName;
    private RatingBar userRatingBar;
    private Realm realm;
    private Spinner platformSpinner;

    private Game currentGame;
    private int gamePosition;
    private String currentPlatform;
    private TextView expandTextView;
    private String longDescription;
    private GiantBombSearch searchResults;
    private Bitmap searchImage;

    private RecyclerView charactersRecyclerView;
    private LinearLayout recyclerLayout;
    private ProgressBar gameImageProgressBar;

    private ImageView savedGameImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity().getApplicationContext());
        currentPlatform = getActivity().getIntent().getStringExtra(EXTRA_PLATFORM);
        gamePosition = getActivity().getIntent().getIntExtra(EXTRA_GAME, -1);
        searchResults = (GiantBombSearch)getActivity().getIntent().getSerializableExtra("GiantBombResponse");
        searchImage = (Bitmap)getActivity().getIntent().getParcelableExtra(EXTRA_SEARCH);

        setHasOptionsMenu(true);
        //setRetainInstance(true);
    }

    private View.OnClickListener expandOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            description.setMaxLines(Integer.MAX_VALUE);
            String temp = longDescription;
            description.setText(temp);
            expandTextView.setOnClickListener(collapseOnClick);
            expandTextView.setText("Collapse");
        }
    };

    private View.OnClickListener collapseOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            description.setMaxLines(7);
            expandTextView.setOnClickListener(expandOnClick);
            expandTextView.setText("See More");
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putByteArray("GameImage", PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap()));
        outState.putSerializable("Recycler", ((CustomRecyclerAdapter) charactersRecyclerView.getAdapter()).getGameCharacterses());

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            byte[] bytes =  savedInstanceState.getByteArray("GameImage");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            gameImageView.setImageBitmap(bmp);
            ArrayList<GameCharacters> gameCharacterses = (ArrayList)savedInstanceState.getSerializable("Recycler");
            CustomRecyclerAdapter customRecyclerAdapter = new CustomRecyclerAdapter(getActivity(), gameCharacterses);
            charactersRecyclerView.setAdapter(customRecyclerAdapter);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_game, parent, false);
        saveGameButton = (Button) v.findViewById(R.id.saveGame);
        gameImageView = (ImageView) v.findViewById(R.id.edit_game_photo);
        gameName = (TextView) v.findViewById(R.id.edit_name_textField);
        platformSpinner = (Spinner) v.findViewById(R.id.edit_platform_spinner);
        genre = (TextView) v.findViewById(R.id.edit_genre_textField);
        userRatingBar = (RatingBar) v.findViewById(R.id.userRatingStars);
        description = (TextView) v.findViewById(R.id.edit_description_textField);
        completionPercentage = (TextView) v.findViewById(R.id.edit_completion_percentage_textField);
        expandTextView = (TextView) v.findViewById(R.id.expandText);
        charactersRecyclerView = (RecyclerView)v.findViewById(R.id.edit_characterRecyclerView);
        recyclerLayout = (LinearLayout)v.findViewById(R.id.recyclerviewLayout);
        gameImageProgressBar = (ProgressBar)v.findViewById(R.id.gameImageProgressBar);
        topViewGameName = (TextView)v.findViewById(R.id.topViewGameName);


        SynchronizedScrollView mScrollView = (SynchronizedScrollView)v.findViewById(R.id.scroll);

        mScrollView.setAnchorView(v.findViewById(R.id.topView));
        mScrollView.setSynchronizedView(v.findViewById(R.id.sync));
        int temp = gamePosition;

        if(gamePosition == -1) {
            gamePosition = -2;
            if (InternetUtils.isNetworkAvailable(getActivity())) {
                final Dialog mDialog = ProgressDialog.show(getActivity(), "Loading", "Wait while loading...");
                new DownloadAsyncTask(gameImageView).execute(searchResults.getImage().getSuper_url());
                GiantBombRestClient.get().getGameGiantBomb(searchResults.getId(), "2b5563f0a5655a6ef2e0a4d0556d2958cced098d", "json",
                        new Callback<GameResponse>() {
                            @Override
                            public void success(GameResponse gameResponse, retrofit.client.Response response) {
                                mDialog.dismiss();
                                ArrayList<Genre> genres = new ArrayList<Genre>();
                                genres = gameResponse.getResults().getGenres();
                                StringBuffer temp = new StringBuffer();
                                for (int i = 0; i < genres.size(); i++) {
                                    if (i < genres.size() - 1) {
                                        temp.append(genres.get(i).getName() + ", ");
                                    } else {
                                        temp.append(genres.get(i).getName());
                                    }
                                }
                                genre.setText(temp);

                                ArrayList<com.apps.danielbarr.gamecollection.Model.GiantBomb.Character> characters = new ArrayList<com.apps.danielbarr.gamecollection.Model.GiantBomb.Character>();
                                characters = gameResponse.getResults().getCharacters();
                                temp = new StringBuffer();

                                if(characters != null) {
                                    for (int i = 0; i < characters.size(); i++) {
                                        if (i < characters.size() - 1) {
                                            temp.append(characters.get(i).getName() + ", ");
                                        } else {
                                            temp.append(characters.get(i).getName());
                                        }
                                    }
                                    CustomRecyclerAdapter customRecyclerAdapter;

                                    if(characters.size() <= 30) {
                                        customRecyclerAdapter = new CustomRecyclerAdapter(characters, getActivity());
                                    }
                                    else {
                                        customRecyclerAdapter = new CustomRecyclerAdapter( new ArrayList<>(characters.subList(0, 30)), getActivity());

                                    }
                                    charactersRecyclerView.setAdapter(customRecyclerAdapter);
                                }
                                else {
                                    recyclerLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                if (!InternetUtils.isNetworkAvailable(getActivity())) {
                                    AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                                    dialog.show();
                                }
                                mDialog.dismiss();
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
            populateTextFields(gamePosition, currentPlatform);
            saveGameButton.setText(getString(R.string.update_game));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        charactersRecyclerView.setLayoutManager(linearLayoutManager);

        addItemsOnSpinner();

        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gamePosition != -1 && gamePosition != -2) {
                    upDateGame();
                } else {
                    saveGame();
                }
                getActivity().finish();
            }
        });

        expandTextView.setOnClickListener(expandOnClick);

        description.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (description.getLineCount() <= 7) {
                    expandTextView.setVisibility(View.GONE);
                } else {
                    expandTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        return v;
    }

    public void saveGame() {
        realm.beginTransaction();

        Game game = realm.createObject(Game.class);
        game.setName(gameName.getText().toString());
        game.setPlatform(platformSpinner.getSelectedItem().toString().toString());
        game.setDescription(longDescription);
        game.setGenre(genre.getText().toString());
        game.setUserRating(userRatingBar.getRating());

        if(!completionPercentage.getText().toString().matches("")) {
            game.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }
        game.setDescription(description.getText().toString());

        if(gameImageView.getDrawable() != null) {
             byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
             game.setLargePhoto(bytes);
             game.setPhoto(PictureUtils.convertBitmapToByteArray(searchImage));
        }
        else {
            byte[] bytes = PictureUtils.convertBitmapToByteArray(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.box_art));
            game.setLargePhoto(bytes);
            game.setPhoto(PictureUtils.convertBitmapToByteArray(searchImage));
        }

        if(recyclerLayout.getVisibility() != View.GONE) {
            ArrayList<GameCharacters> gameCharacterses = ((CustomRecyclerAdapter) charactersRecyclerView.getAdapter()).getGameCharacterses();
            for (int i = 0; i < gameCharacterses.size(); i++) {
                GameCharacters realmCharacter = realm.createObject(GameCharacters.class);
                realmCharacter.setName(gameCharacterses.get(i).getName());
                realmCharacter.setID(gameCharacterses.get(i).getID());

                if(gameCharacterses.get(i).getPhoto() != null) {
                    realmCharacter.setPhoto(gameCharacterses.get(i).getPhoto());
                    realmCharacter.setLargePhoto(gameCharacterses.get(i).getLargePhoto());
                    realmCharacter.setDescription(gameCharacterses.get(i).getDescription());
                }
                else {
                    byte[] bytes = PictureUtils.convertBitmapToByteArray(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.box_art));
                    realmCharacter.setPhoto(bytes);
                    realmCharacter.setLargePhoto(bytes);
                }
                game.getCharacterses().add(realmCharacter);
            }
        }

        realm.commitTransaction();
        realm.close();
    }

    public void upDateGame() {
        realm.beginTransaction();

        currentGame.setPlatform(platformSpinner.getSelectedItem().toString());
        currentGame.setGenre(genre.getText().toString());
        currentGame.setUserRating(userRatingBar.getRating());
        if(!completionPercentage.getText().toString().matches("")) {
            currentGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }

        realm.commitTransaction();
        realm.close();
    }

    public void populateTextFields(int gamePosition, String name) {
        gameImageProgressBar.setVisibility(View.GONE);

        RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", name).equalTo("isDeleted", false).findAll();
        currentGame = storedGames.get(gamePosition);

        gameName.setText(currentGame.getName());
        topViewGameName.setText(currentGame.getName());
        genre.setText(currentGame.getGenre());
        longDescription = currentGame.getDescription();
        description.setText(currentGame.getDescription());
        userRatingBar.setRating(currentGame.getUserRating());
        completionPercentage.setText(Float.toString(currentGame.getCompletionPercentage()));

        if(currentGame.getCharacterses().size() > 0) {
            CustomRecyclerAdapter customRecyclerAdapter = new CustomRecyclerAdapter(currentGame.getCharacterses(), getActivity());
            charactersRecyclerView.setAdapter(customRecyclerAdapter);
            customRecyclerAdapter.notifyDataSetChanged();
        }
        else {
            recyclerLayout.setVisibility(View.GONE);
        }

        if(!currentGame.getPhoto().toString().equals(""))
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(currentGame.getLargePhoto(), 0, currentGame.getLargePhoto().length);
            if(bmp != null) {
                gameImageView.setImageBitmap(bmp);
            }
        }
    }

    public void populateFromSearch(GiantBombSearch giantBombSearch) {
        gameName.setText(giantBombSearch.getName());
        topViewGameName.setText(giantBombSearch.getName());
        description.setText(stripHtml(giantBombSearch.getDescription()));
        longDescription = stripHtml(giantBombSearch.getDescription());
    }

    public void addItemsOnSpinner() {

        List<String> list = new ArrayList<String>();
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
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
        MenuItem menuItem = menu.findItem(R.id.customDeleteButton);

        if (gamePosition == -1) {
            menuItem.setVisible(false);
        }
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
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
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
                ((ImageView)weakReference.get()).setImageBitmap(result);
                gameImageProgressBar.setVisibility(View.GONE);
            }
        }
    }

}
