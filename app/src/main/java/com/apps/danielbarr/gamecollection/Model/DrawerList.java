package com.apps.danielbarr.gamecollection.Model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by danielbarr on 1/17/15.
 */
public class DrawerList extends RealmObject {

    private RealmList<DrawerItem> Items;

    public RealmList<DrawerItem> getItems() {
        return Items;
    }

    public void setItems(RealmList<DrawerItem> items) {
        Items = items;
    }

}
