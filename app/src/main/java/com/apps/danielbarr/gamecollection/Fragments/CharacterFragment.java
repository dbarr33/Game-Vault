package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.RelevantGameRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class CharacterFragment extends Fragment {

    private ImageView characterImageView;
    private ImageView blurredCharacterImageView;
    private TextView characterName;
    private RecyclerView characterDescriptionRecyclerView;
    private RecyclerView characterEnemiesRecyclerView;
    private GameCharacters gameCharacters;
    private SynchronizedScrollView mScrollView;
    private Button backToTopButton;

    public static final String EXTRA_GIANTCHARACTER = "com.apps.danielbarr.gamecollection.character";


    public static CharacterFragment newInstance(GameCharacters gameCharacters) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_GIANTCHARACTER, gameCharacters);

        CharacterFragment fragment = new CharacterFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_character, parent, false);
        characterImageView = (ImageView) v.findViewById(R.id.character_ImageView);
        blurredCharacterImageView = (ImageView)v.findViewById(R.id.blurredCharacterImage);
        characterName = (TextView) v.findViewById(R.id.character_name_textField);
        characterDescriptionRecyclerView = (RecyclerView)v.findViewById(R.id.characterDescriptionsRecyclearView);
        characterEnemiesRecyclerView = (RecyclerView)v.findViewById(R.id.characterEnemiesRecyclearView);
        mScrollView = (SynchronizedScrollView)v.findViewById(R.id.mScrollView);
        backToTopButton = (Button)v.findViewById(R.id.backToTheTopButton);

        gameCharacters = (GameCharacters) getArguments().getSerializable((EXTRA_GIANTCHARACTER));
        getActivity().findViewById(R.id.deleteGameButton).setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);

        mScrollView.setToolbar(toolbar);
        mScrollView.setAnchorView(v.findViewById(R.id.topView));
        mScrollView.setSynchronizedView(v.findViewById(R.id.sync));
        mScrollView.setToTheTopButton(backToTopButton);
        mScrollView.setToolbarTitle(gameCharacters.getName());

        Bitmap bmp = BitmapFactory.decodeByteArray(gameCharacters.getPhoto(), 0, gameCharacters.getPhoto().length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(gameCharacters.getPhoto(), 0, gameCharacters.getPhoto().length);
        if (bmp != null) {
            characterImageView.setImageBitmap(bmp);
            blurredCharacterImageView.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
            blurredCharacterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        characterName.setText(gameCharacters.getName());

        setHasOptionsMenu(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        if(!gameCharacters.getDescription().matches("")) {
            descriptionList.add(gameCharacters.getDescription());
            TextView textView = new TextView(getActivity());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(gameCharacters.getDescription());
            textView.measure(0, 0);

            characterDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
            RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                    characterDescriptionRecyclerView);
            characterDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
        }else {
            characterDescriptionRecyclerView.setVisibility(View.GONE);
        }
        characterEnemiesRecyclerView.setVisibility(View.GONE);

    /*    linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ArrayList<String> enemiesList = new ArrayList<>();
        enemiesList.add("Enemies");


        if( gameCharacters.getEnemies().size() > 0) {
            for (int i = 0; i < gameCharacters.getEnemies().size(); i++) {
                enemiesList.add(gameCharacters.getEnemies().get(i).getName());
            }
        }else {
        }

        RelevantGameRecyclerAdapter gameEnemiesRecyclerAdapter = new RelevantGameRecyclerAdapter(enemiesList, getActivity(),
                characterEnemiesRecyclerView, enemiesList.size() * 118);
        characterEnemiesRecyclerView.setLayoutManager(linearLayoutManager);
        characterEnemiesRecyclerView.setAdapter(gameEnemiesRecyclerAdapter);
        characterEnemiesRecyclerView.setMinimumHeight(160);

*/
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.global, menu);
        menu.findItem(R.id.addGame).setVisible(false);
        menu.findItem(R.id.customDeleteButton).setVisible(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                    getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
