package com.apps.danielbarr.gamecollection.Uitilites;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;

/**
 * Created by danielbarr on 1/27/15.
 */
public class PictureUtils {

    public static byte[] convertBitmapToByteArray( Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        return stream.toByteArray();
    }

    public static int dpTOPX(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return  Math.round(dp / ( DisplayMetrics.DENSITY_DEFAULT/displayMetrics.xdpi) );
    }
}

