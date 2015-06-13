package com.apps.danielbarr.gamecollection.Model.GiantBomb.Search;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Image;

import java.io.Serializable;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantBombSearch implements Serializable {

    public String aliases;
    public String description;
    public Image image;
    public String name;
    public int id;

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
