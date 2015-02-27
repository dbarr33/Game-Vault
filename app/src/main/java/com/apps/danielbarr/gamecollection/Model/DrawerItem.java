package com.apps.danielbarr.gamecollection.Model;

import io.realm.RealmObject;

/**
 * Created by danielbarr on 1/17/15.
 */
public class DrawerItem extends RealmObject{

    private String name = new String();

    private int iconID;

    private int position;

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
