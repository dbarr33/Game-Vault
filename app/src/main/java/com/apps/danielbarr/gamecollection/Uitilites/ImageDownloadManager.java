package com.apps.danielbarr.gamecollection.Uitilites;

import android.os.Handler;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ImageDownloadManager<Token> {
    public  ImageDownloader<Token> imageDownloader;

    public ImageDownloadManager(){
        imageDownloader = new ImageDownloader(new Handler());
        imageDownloader.start();
        imageDownloader.getLooper();

    }
    public void setListener(ImageDownloader.Listener<Token> listener){

        if(imageDownloader != null) {
            imageDownloader.clearQueue();
        }

        imageDownloader.setListener(listener);
    }

    public void queueThumbnail(Token position,String url) {

        imageDownloader.queueThumbnail(position, url);
    }
}
