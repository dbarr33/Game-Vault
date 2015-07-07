package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Daniel Barr (Fuzz)
 */
public class InternetUtils {

    private static AlertDialog.Builder builder;
    private static Dialog dialog;
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static AlertDialog.Builder buildDialog(Context c) {

        if(builder == null) {
            builder = new AlertDialog.Builder(c);
        }

        builder.setTitle("No Internet connection");
        builder.setMessage("You are not connected to the internet");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return builder;
    }

    public static void showDialog(Context c) {
        if (dialog == null) {
            buildDialog(c);
            dialog = builder.create();
            dialog.show();
        }
        else {
            if(!dialog.isShowing()){
                dialog.show();
            }
        }


    }
}
