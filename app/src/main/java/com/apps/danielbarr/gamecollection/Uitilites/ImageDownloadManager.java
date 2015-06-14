package com.apps.danielbarr.gamecollection.Uitilites;

import android.os.Handler;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ImageDownloadManager<Token> {
    public  ImageDownloader<Token> imageDownloader;

    public void setListener(ImageDownloader.Listener<Token> listener){

        if(imageDownloader == null) {
            init();
        }
        else {
            imageDownloader.clearQueue();
        }

        imageDownloader.setListener(listener);
    }

    public void queueThumbnail(Token position,String url) {
        if(imageDownloader == null) {
            init();
        }

        imageDownloader.queueThumbnail(position, url);
    }

    private  void init() {
        imageDownloader = new ImageDownloader(new Handler());
        imageDownloader.start();
        imageDownloader.getLooper();
    }

}
