package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.BaseObject;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class StringArrayListBuilder {

    public static ArrayList<String> createArryList(String name, ArrayList<BaseObject> objects) {
        ArrayList<String> list = new ArrayList<>();
        list.add(name);
        if(objects.size()  < 6) {
            for (int i = 0; i < objects.size(); i++) {
                list.add(objects.get(i).getName());
            }
        }
        else {
            for (int i = 0; i < 6; i++) {
                list.add(objects.get(i).getName());
            }
        }

        return list;
    }
}
