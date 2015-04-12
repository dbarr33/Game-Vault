package com.apps.danielbarr.gamecollection.Model.GiantBomb;

import java.io.Serializable;

/**
 * @author Daniel Barr (Fuzz)
 */
public class Image implements Serializable{
    String icon_url;
    String medium_url;
    String screen_url;
    String small_url;
    String super_url;
    String thumb_url;
    String tiny_url;

    public String getIcon_url() {
        return icon_url;
    }

    public String getMedium_url() {
        return medium_url;
    }

    public String getScreen_url() {
        return screen_url;
    }

    public String getSmall_url() {
        return small_url;
    }

    public String getSuper_url() {
        return super_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public String getTiny_url() {
        return tiny_url;
    }
}
