package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Activities.EditGameActivity;
import com.apps.danielbarr.gamecollection.Model.Game;
import com.apps.danielbarr.gamecollection.Model.IgnResponse;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.IgnRestClient;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;

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
    private static final int REQUEST_CHOICE = 0;
    private ProgressDialog mDialog;
    private boolean isBackgrounded = false;

    private Button saveGameButton;
    private Button ignButton;
    private ImageView gameImageView;
    private TextView gameName;
    private TextView genre;
    private TextView ignTextView;
    private TextView description;
    private TextView descriptionCount;
    private TextView completionPercentage;
    private RatingBar userRatingBar;
    private Realm realm;
    private Spinner platformSpinner;

    private Game currentGame;
    private int gamePosition;
    private String currentPlatform;
    private TextView ignLabel;

    public EditGameFragment() {
        mDialog = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity().getApplicationContext());
        currentPlatform = getActivity().getIntent().getStringExtra(EXTRA_PLATFORM);
        gamePosition = getActivity().getIntent().getIntExtra(EXTRA_GAME, -1);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_game, parent, false);
        saveGameButton = (Button)v.findViewById(R.id.saveGame);
        ignButton = (Button)v.findViewById(R.id.edit_game_IgnInfoButton);
        gameImageView = (ImageView)v.findViewById(R.id.edit_game_photo);
        gameName = (TextView)v.findViewById(R.id.edit_name_textField);
        platformSpinner = (Spinner)v.findViewById(R.id.edit_platform_spinner);
        genre = (TextView)v.findViewById(R.id.edit_genre_textField);
        userRatingBar = (RatingBar)v.findViewById(R.id.userRatingStars);
        ignTextView = (TextView)v.findViewById(R.id.edit_ign_ratingTextview);
        description = (TextView)v.findViewById(R.id.edit_description_textField);
        descriptionCount = (TextView)v.findViewById(R.id.edit_description_textCount);
        ignLabel = (TextView)v.findViewById(R.id.edit_ign_label);
        completionPercentage = (TextView)v.findViewById(R.id.edit_completion_percentage_textField);

        addItemsOnSpinner();

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar2);
        final TextView titleBar = (TextView)toolbar.findViewById(R.id.customTitleBar);


        if (gamePosition != -1) {
            populateTextFields(gamePosition, currentPlatform);
            saveGameButton.setText(getString(R.string.update_game));
            titleBar.setText(getString(R.string.update_game));
        } else {
            titleBar.setText(getString(R.string.save_game));
        }

        if(ignTextView.getText().toString().trim().matches("") || ignTextView.getText().toString().trim().matches("0.0"))
        {
            ignLabel.setVisibility(View.GONE);
            ignTextView.setVisibility(View.GONE);
        }

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionCount.setText(String.valueOf(description.getText().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        saveGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gameName.getText().toString().toString().equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(), "The game name must not be empty", Toast.LENGTH_LONG).show();

                }
                else {
                    if (gamePosition != -1) {
                        upDateGame();
                    } else {
                        saveGame();
                    }
                    getActivity().finish();

                }
            }
        });


        ignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameName.getText().toString().toString().equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(), "The game name must not be empty", Toast.LENGTH_LONG).show();

                } else {

                    if (InternetUtils.isNetworkAvailable(getActivity())) {
                        mDialog = ProgressDialog.show(getActivity(), "Loading", "Wait while loading...");
                        IgnRestClient.get().getIgn(20, gameName.getText().toString(), new Callback<ArrayList<IgnResponse>>() {
                            @Override
                            public void success(ArrayList<IgnResponse> ignResults, retrofit.client.Response response) {
                                mDialog.dismiss();
                                android.app.FragmentManager fm = getActivity().getFragmentManager();
                                IgnGamesFragment dialog = IgnGamesFragment.newInstance(ignResults);
                                dialog.setTargetFragment(EditGameFragment.this, REQUEST_CHOICE);
                                if(!isBackgrounded) {
                                    dialog.show(fm, "TAG");
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("Ign", error.getResponse().getReason());

                            }
                        });
                    }
                    else {
                        AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                        dialog.show();
                    }
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
        game.setDescription(description.getText().toString());
        game.setGenre(genre.getText().toString());
        game.setUserRating(userRatingBar.getRating());
        if(!ignTextView.getText().toString().matches("")) {
            game.setIgnRating(Float.parseFloat(ignTextView.getText().toString()));
        }
        if(!completionPercentage.getText().toString().matches("")) {
            game.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }
        game.setDescription(description.getText().toString());

        if(gameImageView.getDrawable() != null)
        {
             byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable) gameImageView.getDrawable()).getBitmap());
             game.setPhoto(bytes);
        }

        realm.commitTransaction();
        realm.close();
    }

    public void upDateGame() {
        realm.beginTransaction();

        currentGame.setName(gameName.getText().toString());
        currentGame.setPlatform(platformSpinner.getSelectedItem().toString());
        currentGame.setGenre(genre.getText().toString());
        currentGame.setUserRating(userRatingBar.getRating());
        if(!completionPercentage.getText().toString().matches("")) {
            currentGame.setCompletionPercentage(Float.parseFloat(completionPercentage.getText().toString()));
        }
        if(!ignTextView.getText().toString().matches("")){
            currentGame.setIgnRating(Float.parseFloat(ignTextView.getText().toString()));
        }
        currentGame.setDescription(description.getText().toString());

        if(gameImageView.getDrawable() != null)
        {
            byte[] bytes = PictureUtils.convertBitmapToByteArray(((BitmapDrawable)gameImageView.getDrawable()).getBitmap());
            currentGame.setPhoto(bytes);
        }

        realm.commitTransaction();
        realm.close();

    }

    public void populateTextFields(int gamePosition, String name) {
        RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", name).findAll();
        currentGame = storedGames.get(gamePosition);

        gameName.setText(currentGame.getName());
        genre.setText(currentGame.getGenre());
        description.setText(currentGame.getDescription());
        descriptionCount.setText(String.valueOf(description.length()));
        userRatingBar.setRating(currentGame.getUserRating());
        ignTextView.setText(Float.toString(currentGame.getIgnRating()));
        completionPercentage.setText(Float.toString(currentGame.getCompletionPercentage()));

        if(!currentGame.getPhoto().toString().equals(""))
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(currentGame.getPhoto(), 0, currentGame.getPhoto().length);
            if(bmp != null) {
                gameImageView.setImageBitmap(bmp);
            }
        }
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
                RealmResults<Game> storedGames = realm.where(Game.class).equalTo("platform", currentPlatform).findAll();
                storedGames.remove(gamePosition);
                realm.commitTransaction();
                realm.close();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_CHOICE)
        {
            IgnResponse ignResponse = ((EditGameActivity)getActivity()).getIgnResponse();

            if(((EditGameActivity) getActivity()).getGameImage() != null)
            {
                gameImageView.setImageDrawable(((EditGameActivity) getActivity()).getGameImage().getDrawable());
            }
            description.setText(ignResponse.getShort_description());
            ignTextView.setText(ignResponse.getScore());
            gameName.setText(ignResponse.getTitle());

            ignTextView.setVisibility(View.VISIBLE);
            ignLabel.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        isBackgrounded = true;
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackgrounded = false;
    }
}
