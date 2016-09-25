package com.apps.danielbarr.gamecollection.Uitilites;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SnackbarBuilder {
    private Snackbar snackbar;

    public SnackbarBuilder(View parent, String text) {
        snackbar =  Snackbar.make(parent, text, Snackbar.LENGTH_LONG);
        TextView textView = (TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
    }

    public void setOnClickListener(View.OnClickListener onClickListner){
        TextView textView = (TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        textView.setTextColor(Color.parseColor("#FF5722"));
        snackbar.setAction("Undo",onClickListner);
    }

    public void show() {
        snackbar.show();
    }
}
