package com.apps.danielbarr.gamecollection.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Model.IgnResponse;
import com.apps.danielbarr.gamecollection.R;

/**
 * Created by danielbarr on 1/19/15.
 */
public class EditGameActivity extends SingleFragmentActivity {

    private EditGameFragment editGameFragment;
    private IgnResponse ignResponse;
    private ImageView gameImage;

    @Override
    protected Fragment createFragment() {
        editGameFragment = new EditGameFragment();
        ignResponse = new IgnResponse();
        gameImage = new ImageView(this);
        return editGameFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    public void setupUI(final View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    hideSoftKeyboard(editGameFragment.getActivity());
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public IgnResponse getIgnResponse() {
        return ignResponse;
    }

    public void setIgnResponse(IgnResponse ignResponse) {
        this.ignResponse = ignResponse;
    }

    public ImageView getGameImage() {
        return gameImage;
    }

    public void setGameImage(ImageView gameImage) {
        this.gameImage = gameImage;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.editGameContainer));
    }
}
