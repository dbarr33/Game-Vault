package com.apps.danielbarr.gamecollection.Uitilites;

import android.text.Html;

/**
 * @author Daniel Barr (Fuzz)
 */
public class HTMLUtil {

    public static  String stripHtml(String html) {
        if(html != null) {
            String temp = Html.fromHtml(html).toString();
            if(temp.length() > 4000) {
                return temp.substring(0, 4000);
            }
            else {
                return temp;
            }
        }
        else {
            return "";
        }
    }
}
