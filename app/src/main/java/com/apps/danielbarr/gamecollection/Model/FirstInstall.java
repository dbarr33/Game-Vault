package com.apps.danielbarr.gamecollection.Model;

import io.realm.RealmObject;

/**
 * Created by danielbarr on 1/17/15.
 */
public class FirstInstall extends RealmObject{

    private boolean isFirstInstall;

    public boolean isFirstInstall() {
        return isFirstInstall;
    }

    public void setFirstInstall(boolean isFirstInstall) {
        this.isFirstInstall = isFirstInstall;
    }

}
