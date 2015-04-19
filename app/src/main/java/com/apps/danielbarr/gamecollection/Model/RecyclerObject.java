package com.apps.danielbarr.gamecollection.Model;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RecyclerObject {

    private int ID;
    private String name;
    private byte[] photo;
    private byte[] largePhoto;
    private String description;
    private boolean photosLoaded;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getLargePhoto() {
        return largePhoto;
    }

    public void setLargePhoto(byte[] largePhoto) {
        this.largePhoto = largePhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPhotosLoaded() {
        return photosLoaded;
    }

    public void setPhotosLoaded(boolean photosLoaded) {
        this.photosLoaded = photosLoaded;
    }
}
