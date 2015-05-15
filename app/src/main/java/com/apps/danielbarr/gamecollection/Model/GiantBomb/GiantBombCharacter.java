package com.apps.danielbarr.gamecollection.Model.GiantBomb;

import com.google.gson.annotations.SerializedName;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombCharacter {
    private Image image;

    @SerializedName("description")
    private String description;
//    private Enemy test;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
//
//    public Enemy getEnemies() {
//        return test;
//    }
//
//    public void setEnemies(Enemy enemies) {
//        this.test = enemies;
//    }
}
