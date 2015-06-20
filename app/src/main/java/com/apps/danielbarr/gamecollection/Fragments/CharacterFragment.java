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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Adapter.RelevantGameRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;
import com.apps.danielbarr.gamecollection.Uitilites.ScreenSetupController;
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
    private RealmCharacter realmCharacter;
    private SynchronizedScrollView mScrollView;
    private Button backToTopButton;

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
        backToTopButton = (Button)v.findViewById(R.id.backToTheTopButton);

        realmCharacter = (RealmCharacter) getArguments().getSerializable((EXTRA_GIANTCHARACTER));

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.editToolbar);
        toolbar.setAlpha(0);

        mScrollView.setToolbar(toolbar);
        mScrollView.setAnchorView(v.findViewById(R.id.topView));
        mScrollView.setSynchronizedView(v.findViewById(R.id.sync));
        mScrollView.setToTheTopButton(backToTopButton);
        mScrollView.setToolbarTitle(realmCharacter.getName());

        backToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollView.smoothScrollTo(0,0);
                backToTopButton.setVisibility(View.GONE);
            }
        });

        Bitmap bmp = BitmapFactory.decodeByteArray(realmCharacter.getPhoto(), 0, realmCharacter.getPhoto().length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(realmCharacter.getPhoto(), 0, realmCharacter.getPhoto().length);
        if (bmp != null) {
            characterImageView.setImageBitmap(bmp);
            blurredCharacterImageView.setImageBitmap(PictureUtils.blurBitmap(bitmap, getActivity().getApplicationContext()));
            blurredCharacterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
            RelevantGameRecyclerAdapter gameDescriptionRecyclerAdapter = new RelevantGameRecyclerAdapter(descriptionList, getActivity(),
                    characterDescriptionRecyclerView);
            characterDescriptionRecyclerView.setAdapter(gameDescriptionRecyclerAdapter);
            characterDescriptionRecyclerView.setVisibility(View.VISIBLE);
        }else {
            characterDescriptionRecyclerView.setVisibility(View.GONE);
        }
        return v;
    }
}
