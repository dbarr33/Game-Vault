package com.apps.danielbarr.gamecollection.Uitilites;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by danielbarr on 1/27/15.
 */
public class PictureUtils {

    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, Bitmap bitmap)
    {
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();


        float srcWidth = bitmap.getWidth();
        float srcHeight = bitmap.getHeight();

        int inSampleSize = 1;

        if(srcHeight > destHeight || srcWidth >destWidth)
        {
            if(srcWidth > srcHeight)
            {
                inSampleSize = Math.round(srcHeight/destHeight);
            }
            else
            {
                inSampleSize = Math.round((srcWidth/destWidth));
            }
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        //Bitmap.createBitmap(bitmap,options);

        return new BitmapDrawable(a.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView)
    {
        if(!(imageView.getDrawable() instanceof BitmapDrawable))
        {
            return;
        }

        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

    public static byte[] convertBitmapToByteArray( Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        return stream.toByteArray();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(25.f);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();

        return outBitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());

        int height;
        int width;
        if(ratio < 1) {
            width = Math.round((float) ratio * realImage.getWidth());
            height = Math.round((float) ratio * realImage.getHeight());
        }
        else {
            return realImage;
        }

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpTOPX(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return  Math.round(dp / ( DisplayMetrics.DENSITY_DEFAULT/displayMetrics.xdpi) );
    }
}

