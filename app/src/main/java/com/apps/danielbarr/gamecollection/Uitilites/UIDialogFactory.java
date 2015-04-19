package com.apps.danielbarr.gamecollection.Uitilites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * @author Daniel Barr (Fuzz)
 */
public class UIDialogFactory {

    public interface DialogFucntions{
        public void OnPositive(Context context);
        public void OnNegative(Context context);
    }

    public static class OKDialogBuild implements DialogFucntions {

        @Override
        public void OnPositive(Context context) {
            Toast.makeText((context), "OK",
                    Toast.LENGTH_SHORT).show();

            //context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }

        @Override
        public void OnNegative(Context context) {

            Toast.makeText((context), "Hell No",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static class YesDialogBuild implements DialogFucntions {

        @Override
        public void OnPositive(Context context) {
            Toast.makeText((context), "Yes",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnNegative(Context context) {
            Toast.makeText((context), "No",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public static Dialog createDialog(final Context context, String type, final DialogFucntions dialogFucntions) {

        Dialog dialog = new AlertDialog.Builder(context).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogFucntions.OnPositive(context);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogFucntions.OnNegative(context);
            }
        }).create();
        return dialog;
    }
}
