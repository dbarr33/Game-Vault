package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
import com.apps.danielbarr.gamecollection.Uitilites.SynchronizedScrollView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

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
                    .into(new SimpleTarget<Bitmap>(1200, 800) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            blurredCharacterImageView.setImageBitmap(PictureUtils.blurBitmap(resource, getActivity().getApplicationContext()));
                            blurredCharacterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            blurredCharacterImageView.setVisibility(View.VISIBLE);
                        }
                    });
        }
        characterName.setText(realmCharacter.getName());
        ScreenSetupController.currentScreenCharacter(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Description");
        if(!realmCharacter.getDescription().matches("")) {
            descriptionList.add(realmCharacter.getDescription());
            TextView textView = new TextView(getActivity());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(realmCharacter.getDescription());
            textView.measure(0, 0);

            characterDescriptionRecyclerView.setLayoutManager(linearLayoutManager);
            ExpandableRecyclerAdapter gameDescriptionRecyclerAdapter = new ExpandableRecyclerAdapter(descriptionList,
                    characterDescriptionRecyclerView, true);
            characterDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
            characterDescriptionRecyclerView.setVisibility(View.VISIBLE);
        }else {
            characterDescriptionRecyclerView.setVisibility(View.GONE);
        }
        return v;
    }
}
