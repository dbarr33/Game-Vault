package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class CharacterFragment extends Fragment {

    private ImageView characterImageView;
    private TextView characterName;
    private TextView description;
    private TextView expandTextView;


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
        description = (TextView)v.findViewById(R.id.character_description_textField);
        expandTextView = (TextView)v.findViewById(R.id.character_expandText);

        gameCharacters = (GameCharacters) getArguments().getSerializable((EXTRA_GIANTCHARACTER));

        Bitmap bmp = BitmapFactory.decodeByteArray(gameCharacters.getLargePhoto(), 0, gameCharacters.getLargePhoto().length);
        if (bmp != null) {
            characterImageView.setImageBitmap(bmp);
        }
        description.setText(gameCharacters.getDescription());
        characterName.setText(gameCharacters.getName());
        expandTextView.setOnClickListener(expandOnClick);

        setHasOptionsMenu(true);
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

    private View.OnClickListener expandOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            description.setMaxLines(Integer.MAX_VALUE);
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
