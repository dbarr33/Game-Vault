package com.apps.danielbarr.gamecollection.Model.GiantBomb.Character;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Image;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombCharacter {
    public Image image;
    public String description;

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
}
