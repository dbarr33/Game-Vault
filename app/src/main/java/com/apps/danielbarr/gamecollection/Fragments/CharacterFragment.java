package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.RelevantGameRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class CharacterFragment extends Fragment {

    private ImageView characterImageView;
    private TextView characterName;
    private RecyclerView characterDescriptionRecyclerView;
    private RecyclerView characterEnemiesRecyclerView;
    private GameCharacters gameCharacters;

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
        characterName = (TextView) v.findViewById(R.id.character_name_textField);
        characterDescriptionRecyclerView = (RecyclerView)v.findViewById(R.id.characterDescriptionsRecyclearView);
        characterEnemiesRecyclerView = (RecyclerView)v.findViewById(R.id.characterEnemiesRecyclearView);

        gameCharacters = (GameCharacters) getArguments().getSerializable((EXTRA_GIANTCHARACTER));

        Bitmap bmp = BitmapFactory.decodeByteArray(gameCharacters.getPhoto(), 0, gameCharacters.getPhoto().length);
        if (bmp != null) {
            characterImageView.setImageBitmap(bmp);
        }
        characterName.setText(gameCharacters.getName());

        setHasOptionsMenu(true);

        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        descriptionList.add(gameCharacters.getDescription());
        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setText(gameCharacters.getDescription());
        textView.measure(0, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        characterDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
        RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                characterDescriptionRecyclerView, textView.getLineCount() * 118 + 180);
        characterDescriptionRecyclerView .setAdapter(gameDescriptionRecyclerAdapter);
        characterDescriptionRecyclerView.setMinimumHeight(160);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ArrayList<String> enemiesList = new ArrayList<>();
        enemiesList.add("Enemies");


        if(gameCharacters.getEnemies() != null) {
            for (int i = 0; i < gameCharacters.getEnemies().size(); i++) {
                enemiesList.add(gameCharacters.getEnemies().get(i).getName());
            }
        }

        RelevantGameRecyclerAdapter gameEnemiesRecyclerAdapter = new RelevantGameRecyclerAdapter(enemiesList, getActivity(),
                characterEnemiesRecyclerView, enemiesList.size() * 118);
        characterEnemiesRecyclerView.setLayoutManager(linearLayoutManager);
        characterEnemiesRecyclerView.setAdapter(gameEnemiesRecyclerAdapter);
        characterEnemiesRecyclerView.setMinimumHeight(160);


        return v;
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
