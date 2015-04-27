package com.apps.danielbarr.gamecollection.Old;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.apps.danielbarr.gamecollection.Activities.SingleFragmentActivity;
import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.R;

/**
 * Created by danielbarr on 1/19/15.
 */
public class EditGameActivity extends SingleFragmentActivity {

    private EditGameFragment editGameFragment;

    @Override
    protected Fragment createFragment() {
        editGameFragment = new EditGameFragment();
        return editGameFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().hide();

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            editGameFragment = (EditGameFragment)getFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//Save the fragment's instance
        getFragmentManager().putFragment(outState, "mContent", editGameFragment);


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void hideEditFragment() {
        getFragmentManager().beginTransaction().hide(editGameFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setupUI(findViewById(R.id.editGameContainer));
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().show(editGameFragment).commit();
        }
        else {
            super.onBackPressed();
        }
    }
}
