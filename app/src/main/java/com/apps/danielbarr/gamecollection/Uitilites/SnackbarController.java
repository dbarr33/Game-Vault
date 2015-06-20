package com.apps.danielbarr.gamecollection.Uitilites;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SnackbarController {
    private Snackbar snackbar;

    public SnackbarController(View parent, String text) {
        snackbar =  Snackbar.make(parent, "Saved Game", Snackbar.LENGTH_SHORT);
        TextView textView = (TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
    }

    public void show() {
        snackbar.show();
    }

}
