package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.HTMLUtil;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author Daniel Barr (Fuzz)
 */
public class CharacterFragment extends Fragment {

    private ImageView characterImageView;
    private ImageView blurredCharacterImageView;
    private TextView characterName;
    private RecyclerView characterDescriptionRecyclerView;
    private RealmCharacter realmCharacter;
    private SynchronizedScrollView mScrollView;

    public static final String EXTRA_GIANTCHARACTER = "com.apps.danielbarr.gamecollection.character";

    public static CharacterFragment newInstance(RealmCharacter realmCharacter) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_GIANTCHARACTER, realmCharacter);

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
        characterDescriptionRecyclerView = (RecyclerView)v.findViewById(R.id.characterDescriptionsRecyclerView);
        mScrollView = (SynchronizedScrollView)v.findViewById(R.id.mScrollView);

        realmCharacter = (RealmCharacter) getArguments().getSerializable((EXTRA_GIANTCHARACTER));

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);

        mScrollView.setToolbarTitle(realmCharacter.getName());
        mScrollView.init(getActivity(), v);

        if (realmCharacter.getImageURL() != null) {
            Glide.with(this)
                    .load(realmCharacter.getImageURL())
                    .into(characterImageView);
            Glide.with(this)
                    .load(realmCharacter.getImageURL())
                    .asBitmap()
                    .transform(new BlurTransformation(getActivity().getApplicationContext()))
                    .into(blurredCharacterImageView);
        }
        else {
            Glide.with(this)
                    .load(realmCharacter.getPhoto())
                    .into(characterImageView);
            Glide.with(this)
                    .load(realmCharacter.getPhoto())
                    .asBitmap()
                    .transform(new BlurTransformation(getActivity().getApplicationContext()))
                    .into(blurredCharacterImageView);
        }
        characterName.setText(realmCharacter.getName());
        ScreenSetupController.currentScreenCharacter(getActivity());

        if(!realmCharacter.getDescription().matches("")) {
            ArrayList<String> descriptionList = new ArrayList<>();
            descriptionList.add("Description");
            descriptionList.add(HTMLUtil.stripHtml(realmCharacter.getDescription()));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            characterDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
            ExpandableRecyclerAdapter adapter = new ExpandableRecyclerAdapter(descriptionList);
            characterDescriptionRecyclerView.setAdapter(adapter);
            characterDescriptionRecyclerView.setVisibility(View.VISIBLE);

        }
        else {
            characterDescriptionRecyclerView.setVisibility(View.GONE);
        }
        return v;
    }
}
