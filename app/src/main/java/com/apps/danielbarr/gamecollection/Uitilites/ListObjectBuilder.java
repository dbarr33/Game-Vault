package com.apps.danielbarr.gamecollection.Uitilites;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.BaseObject;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.NameInterface;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ListObjectBuilder {

    public static ArrayList<String> createArrayList(String name, ArrayList<BaseObject> objects) {
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

    public static ArrayList<String> createArrayList(String name,  RealmList<? extends NameInterface> nameInterfaces) {
        ArrayList<String> list = new ArrayList<>();
        list.add(name);
        if(nameInterfaces.size()  < 6) {
            for (int i = 0; i < nameInterfaces.size(); i++) {
                list.add(nameInterfaces.get(i).getName());
            }
        }
        else {
            for (int i = 0; i < 6; i++) {
                list.add(nameInterfaces.get(i).getName());
            }
        }

        return list;
    }
}
